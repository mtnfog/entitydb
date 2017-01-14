/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.queues.consumers;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;
import com.mtnfog.entitydb.model.queue.QueueIngestMessage;
import com.mtnfog.entitydb.model.queue.QueueUpdateAclMessage;
import com.mtnfog.entitydb.model.rulesengine.RuleEvaluationResult;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.services.EntityQueryService;

/**
 * Abstract class for queue consumers that provides the functionality to process
 * entities consumed from the queue in the <code>processEntity</code> function.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public abstract class AbstractQueueConsumer {
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractQueueConsumer.class);
	
	private EntityStore<?> entityStore;
	private List<RulesEngine> rulesEngines;
	private AuditLogger auditLogger;	
	private MetricReporter metricReporter;
	private ConcurrentLinkedQueue<IndexedEntity> indexerCache;
	
	/**
	 * Base constructor for queue consumers.
	 * @param entityStore An {@link EntityStore}.
	 * @param rulesEngines A list of {@link RuleEngine}.
	 * @param auditLogger An {@link AuditLogger}.
	 * @param entityQueryService An {@link EntityQueryService}.
	 * @param metricReporter A {@link MetricReporter}.
	 * @param indexerCache The indexer's cache.
	 */
	public AbstractQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			AuditLogger auditLogger, MetricReporter metricReporter,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		this.entityStore = entityStore;
		this.rulesEngines = rulesEngines;
		this.auditLogger = auditLogger;
		this.metricReporter = metricReporter;
		this.indexerCache = indexerCache;
		
	}
	
	/**
	 * Updates an entity's ACL in the entity store and the search index.
	 * @param queueUpdateAclMessage The {@link QueueUpdateAclMessage} that contains the entity ID and the new ACL.
	 * @return <code>true</code> if the entity's ACL was successfully changed; otherwise <code>false</code>.
	 */
	protected boolean updateEntityAcl(QueueUpdateAclMessage queueUpdateAclMessage) throws EntityStoreException {
	
		// The ACL was validated when it was received through the API.
		// There is no need to validate it again here.
		
		boolean updated = false;
		
		try {
		
			String entityId = entityStore.updateAcl(queueUpdateAclMessage.getEntityId(), queueUpdateAclMessage.getAcl());
						
			// Audit this update.
			auditLogger.audit(queueUpdateAclMessage.getEntityId(), System.currentTimeMillis(), queueUpdateAclMessage.getApiKey(), AuditAction.ACL_UPDATED);
			
			// Put the entity onto the internal list for indexing.
			IndexedEntity indexedEntity = entityStore.getEntityById(entityId).toIndexedEntity();
			indexerCache.add(IndexedEntity.fromEntity(indexedEntity, queueUpdateAclMessage.getEntityId(), queueUpdateAclMessage.getAcl()));						
			
			// Report how long this ACL update was in the queue.
			metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_INGEST, "timeInAclUpdateQueue", queueUpdateAclMessage.getTimestamp());
			
			updated = true;
			
		} catch (NonexistantEntityException ex) {
			
			// Should not be thrown because the entity is checked for existence when received through the API.
			LOGGER.warn("Entity {} does not exist.", queueUpdateAclMessage.getEntityId());
			
		} catch (MalformedAclException ex) {
			
			// Should not be thrown because the ACL is validated when received through the API.
			LOGGER.warn("Updated ACL {} for entity {} is invalid.", queueUpdateAclMessage.getAcl(), queueUpdateAclMessage.getEntityId());
			
		}
		
		return updated;
		
	}
	
	/**
	 * Process the entity through the rules engine, store the entity, and index it.
	 * @param queueIngestMessage The {@link QueueIngestMessage} containing the entity to ingest.
	 * @return <code>true</code> if the entity was successfully ingested; otherwise <code>false</code>.
	 * @throws MalformedAclException 
	 */
	protected boolean ingestEntity(QueueIngestMessage queueIngestMessage) throws MalformedAclException {
			
		long startTime = System.currentTimeMillis();
		
		boolean ingested = true;
		
		// The rules engine should execute whether or not the entity exists in the store.
		String updatedAcl = executeRulesEngine(queueIngestMessage.getEntity());
		
		// Set the ACL to a string for easier reference.
		String acl = queueIngestMessage.getAcl();
		
		// See if the rule execution returned an updated ACL.
		if(StringUtils.isNotEmpty(updatedAcl)) {
			acl = updatedAcl;
		}
		
		// Generate the entity's ID.
		final String entityId = EntityIdGenerator.generateEntityId(queueIngestMessage.getEntity().getText(), queueIngestMessage.getEntity().getConfidence(), 
				queueIngestMessage.getEntity().getLanguageCode(), queueIngestMessage.getEntity().getContext(), queueIngestMessage.getEntity().getDocumentId(), 
				queueIngestMessage.getAcl().toString());
		
		LOGGER.trace("Consumed entity {} from the queue.", entityId);

		// Make sure this entity is not already stored.
		if(entityStore.getEntityById(entityId) == null) {
			
			try {	
				
				LOGGER.trace("Storing entity {}.", entityId);
				entityStore.storeEntity(queueIngestMessage.getEntity(), acl);													
				
			} catch (EntityStoreException ex) {
				
				LOGGER.error("Unable to store entity: " + queueIngestMessage.getEntity().toString(),  ex);
				
				// This will leave the entity on the queue.
				ingested = false;
				
				metricReporter.report(MetricReporter.MEASUREMENT_INGEST, "ingestException", 1L, Unit.COUNT);
				
			}
			
			if(ingested) {
			
				// Audit this entity ingest.
				auditLogger.audit(entityId, System.currentTimeMillis(), queueIngestMessage.getApiKey(), AuditAction.STORED);
			
				// This entity is ready for indexing.
				indexerCache.add(IndexedEntity.fromEntity(queueIngestMessage.getEntity(), entityId, acl));
				
				// Report the elapsed time to ingest.
				metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_INGEST, "time", startTime);
				
				// Report how long this entity was in the queue.
				metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_INGEST, "timeInIngestQueue", queueIngestMessage.getTimestamp());
				
			}
			
		} else {
			
			LOGGER.info("Entity {} already exists in the entity store.", entityId);
			auditLogger.audit(entityId, System.currentTimeMillis(), queueIngestMessage.getApiKey(), AuditAction.SKIPPED);
			
			// We will want to return true here because the entity was successfully processed
			// but there's no need to store the entity again. So don't set ingested = false.
			
			metricReporter.report(MetricReporter.MEASUREMENT_INGEST, "duplicateEntity", 1L, Unit.COUNT);
			
		}				
		
		return ingested;
	
	}
	
	/**
	 * Executes the rules engines on the entity.
	 * @param entity An {@link Entity entity}.
	 * @return An updated ACL for the entity or <code>null</code>.
	 */
	private String executeRulesEngine(Entity entity) {
		
		LOGGER.trace("Evaluating the entity against the rules.");
		
		long startTime = System.currentTimeMillis();
				
		String acl = StringUtils.EMPTY;
		
		for(RulesEngine rulesEngine : rulesEngines) {
		
			// Process through the rules engine.
			RuleEvaluationResult result = rulesEngine.evaluate(entity);
			
			if(StringUtils.isNotEmpty(result.getAcl())) {
				acl = result.getAcl();
			}
		
		}	
		
		metricReporter.reportElapsedTime("RulesEngine", "time", startTime);
		
		return acl;
		
	}
		
}