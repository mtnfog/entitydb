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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.services;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.datastore.repository.ContinuousQueryRepository;
import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;
import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.model.exceptions.api.BadRequestException;
import com.mtnfog.entitydb.model.exceptions.api.InternalServerErrorException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.exceptions.QueryGenerationException;
import com.mtnfog.entitydb.eql.model.EntityQuery;

@Component
public class DefaultEntityQueryService implements EntityQueryService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultEntityQueryService.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
			
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Autowired
	private ContinuousQueryRepository continuousQueryRepository;
		
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryResult eql(String query, String apiKey, int continuous, int days) {
				
		QueryResult queryResult = null;
		
		try {
			
			EntityQuery entityQuery = Eql.generate(query);
			
				// Audit this query.
				boolean auditResult = auditLogger.audit(query, System.currentTimeMillis(), apiKey, properties.getAuditId());
				
				if(auditResult) {
				
					// Get the user from the API key.					
					UserEntity userEntity = userRepository.getByApiKey(apiKey);
					
					User user = User.fromEntity(userEntity);
					
					// Execute the query.
					queryResult = executeQuery(entityQuery, user, apiKey);
					
					if(continuous > 0) {
					
						// The query was validated and executed successfully.
						// If it is set to be continuous we can now persist it.
						continuousQueryRepository.save(new ContinuousQueryEntity(userEntity, query, new Date(), days));
					
					}
				
				} else {
					
					// The search could not be audited.
					throw new InternalServerErrorException("Unable to audit query.");
					
				}
			
		} catch (BadRequestException | QueryGenerationException | IllegalStateException ex) {
			
			LOGGER.error("Malformed query: " + ex.getMessage(), ex);
			
			throw new BadRequestException("Malformed query.");		
									
		} catch (Exception ex) {
			
			LOGGER.error("Unable to execute query.", ex);
			
			throw new InternalServerErrorException("Unable to process entity query. See the log for more information.");
			
		}
		
		return queryResult;
		
	}
	
	private QueryResult executeQuery(EntityQuery entityQuery, User user, String apiKey) throws QueryGenerationException, EntityStoreException, InvalidQueryException {
		
		QueryResult queryResult = null;
				
		LOGGER.trace("Executing search against the search index.");
		
		// Give an ID to this query.
		String queryId = UUID.randomUUID().toString();
		
		// Execute the entity query against the search index..
		List<IndexedEntity> indexedEntities = searchIndex.queryForIndexedEntities(entityQuery, user);
				
		if(CollectionUtils.isNotEmpty(indexedEntities)) {
			
			Iterator<IndexedEntity> it = indexedEntities.iterator(); 
			
			while(it.hasNext()) {
				
				IndexedEntity indexedEntity = (IndexedEntity) it.next();
				
				// Set to true by default in case auditing is not enabled.
				boolean auditResult = true;
				
				if(properties.isAuditEnabled()) {
				
					auditResult = auditLogger.audit(indexedEntity.getEntityId(), System.currentTimeMillis(), apiKey, AuditAction.SEARCH_RESULT, properties.getAuditId());
					
				}
								
				if(!auditResult) {
				
					// If it can't be audited don't return it.
					LOGGER.warn("Entity ID {} could not be audited so it was not returned in query results.", indexedEntity.getEntityId());
					it.remove();
					
				} else {
					
					// It was audited successfully.
				
					//  Mask the entity ACLs?
					if(properties.isMaskEntityAcl()) {				
						indexedEntity.setAcl(null);					
					}
					
				}								
				
			}
			
			queryResult = new QueryResult(indexedEntities, queryId);
		
		} else {
			
			LOGGER.trace("No matching entities found.");
			
			queryResult = new QueryResult(Collections.emptyList(), queryId);
			
		}
			
		return queryResult;
					
	}
		
}