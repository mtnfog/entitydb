/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.queues.consumers;

import java.util.List;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.rulesengine.RuleEvaluationResult;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.commons.caching.IdylCache;

/**
 * Abstract class for queue consumers that provides the functionality to process
 * entities consumed from the queue in the <code>processEntity</code> function.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public abstract class AbstractQueueConsumer {
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractQueueConsumer.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
	
	private EntityStore<?> entityStore;
	private List<RulesEngine> rulesEngines;
	private IdylCache idylCache;
	private AuditLogger auditLogger;	

	public AbstractQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			IdylCache idylCache, AuditLogger auditLogger) {
		
		this.entityStore = entityStore;
		this.rulesEngines = rulesEngines;
		this.idylCache = idylCache;
		this.auditLogger = auditLogger;
		
	}
	
	/**
	 * Updates an entity's ACL in the entity store and the search index.
	 * @param entityId The entity's ID.
	 * @param acl The entity's new ACL.
	 * @return <code>true</code> if the entity's ACL was successfully changed; otherwise <code>false</code>.
	 */
	protected boolean updateEntityAcl(String entityId, Acl acl) throws EntityStoreException {
	
		// The ACL was validated when it was received through the API.
		// There is no need to validate it again here.
		
		boolean updated = true;
		
		try {
		
			entityStore.updateAcl(entityId, acl.toString());
			
		} catch (NonexistantEntityException ex) {
			
			LOGGER.warn("Entity {} does not exist.", entityId);
			
			updated = false;
			
		}
		
		return updated;
		
	}
	
	/**
	 * Process the entity through the rules engine, store the entity, and index it.
	 * @param entity The {@link Entity entity}.
	 * @param context The context under which the entity was extracted.
	 * @param documentId The document ID under which the entity was extracted.
	 * @param acl The entity's {@link Acl ACL}.
	 * @return <code>true</code> if the entity was successfully ingested; otherwise <code>false</code>.
	 * @throws MalformedAclException 
	 */
	protected boolean ingestEntity(Entity entity, Acl acl, String apiKey) throws MalformedAclException {
			
		boolean ingested = true;				
		
		// The ACL was validated when it was received through the API.
		// There is no need to validate it again here.		
		
		final String entityId = EntityIdGenerator.generateEntityId(entity.getText(), entity.getConfidence(), entity.getLanguageCode(), entity.getContext(), entity.getDocumentId(), acl.toString());
		
		LOGGER.trace("Consumed entity {} from the queue.", entityId);
		
		// The rules engine should execute whether or not the entity exists in the store.
		LOGGER.trace("Executing the rules engine on entity {}.", entityId);
		String updatedAcl = executeRulesEngine(entity);
		
		// See if the rule execution returned an updated ACL.
		if(StringUtils.isNotEmpty(updatedAcl)) {
			acl = new Acl(updatedAcl);
		}
		
		// Make sure this entity is not already stored.
		if(entityStore.getEntityById(entityId) == null) {
			
			try {	
				
				LOGGER.trace("Storing entity {}.", entityId);
				entityStore.storeEntity(entity, acl.toString());
							
				LOGGER.trace("Writing entity {} to audit log.", entityId);
				auditLogger.audit(entityId, System.currentTimeMillis(), apiKey, AuditAction.STORED, properties.getAuditId());								
				
			} catch (EntityStoreException ex) {
				
				LOGGER.error("Unable to store entity: " + entity.toString(),  ex);
				ingested = false;
				
				// This will leave this entity on the queue.
				
			}
			
		} else {
			
			LOGGER.info("Entity {} already exists in the entity store.", entityId);
			auditLogger.audit(entityId, System.currentTimeMillis(), apiKey, AuditAction.SKIPPED, properties.getAuditId());
			
			// We will want to return true here because the entity was successfully processed.
			
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
				
		String acl = StringUtils.EMPTY;
		
		for(RulesEngine rulesEngine : rulesEngines) {
		
			// Process through the rules engine.
			RuleEvaluationResult result = rulesEngine.evaluate(entity);
			
			if(StringUtils.isNotEmpty(result.getAcl())) {
				acl = result.getAcl();
			}
		
		}	
		
		return acl;
		
	}
	
}