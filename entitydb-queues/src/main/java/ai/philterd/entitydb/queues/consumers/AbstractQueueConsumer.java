/*
 * Copyright 2024 Philterd, LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ai.philterd.entitydb.queues.consumers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import ai.philterd.entitydb.model.exceptions.QueryGenerationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.audit.AuditAction;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;
import ai.philterd.entitydb.model.queue.QueueIngestMessage;
import ai.philterd.entitydb.model.queue.QueueUpdateAclMessage;
import ai.philterd.entitydb.model.rulesengine.RuleEvaluationResult;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.services.EntityQueryService;

/**
 * Abstract class for queue consumers that provides the functionality to process
 * entities consumed from the queue in the <code>processEntity</code> function.
 * 
 * @author Philterd, LLC
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
	 * @param rulesEngines A list of rules engines.
	 * @param auditLogger An {@link AuditLogger}.
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

			try {

				// Process through the rules engine.
				RuleEvaluationResult result = rulesEngine.evaluate(entity);

				if (StringUtils.isNotEmpty(result.getAcl())) {
					acl = result.getAcl();
				}

			} catch (QueryGenerationException ex) {
				// TODO: Handle this exception.
			}
		
		}	
		
		metricReporter.reportElapsedTime("RulesEngine", "time", startTime);
		
		return acl;
		
	}
		
}