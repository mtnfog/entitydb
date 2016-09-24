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
package com.mtnfog.entitydb.services;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.model.exceptions.api.BadRequestException;
import com.mtnfog.entitydb.model.exceptions.api.InternalServerErrorException;
import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.users.User;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.exceptions.QueryGenerationException;
import com.mtnfog.entitydb.eql.model.EntityQuery;

@Component
public class EntityQueryService {

	private static final Logger LOGGER = LogManager.getLogger(EntityQueryService.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
			
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private AuditLogger auditLogger;
	
	//@Autowired
	//private ContinuousQueryRepository continuousQueryRepository;
	
	//@Autowired
	//private UserRepository userRepository;
	
	@Autowired
	private List<User> usersAndGroups;
	
	/**
	 * Execute an EQL query.
	 * @param query The EQL query.
	 * @param apiKey The user's API key.
	 * @param continuous <code>1</code> if the query is to be a continuous query. 
	 * @param days The number of days to be continuous.
	 * @return An {@link ExternalQueryResult result}.
	 */
	public QueryResult eql(String query, String apiKey, int continuous, int days) {
				
		QueryResult queryResult = null;
		
		try {
			
			EntityQuery entityQuery = Eql.generate(query);
			
				// Audit this query.
				boolean auditResult = auditLogger.audit(query, System.currentTimeMillis(), apiKey, properties.getAuditId());
				
				if(auditResult) {
				
				// Get the user from the API key.
				User user = getUserByApiKey(apiKey);
				
				queryResult = executeQuery(entityQuery, user, apiKey);
				
				// The query was validated and executed successfully.
				// If it is set to be continuous we can now persist it.
				//continuousQueryRepository.save(new ContinuousQuery(userRepository.getByApiKey(apiKey), query, new Date(), days));
			
			} else {
				
				// The search could not be audited.
				throw new InternalServerErrorException("Unable to audit query.");
				
			}
			
		} catch (BadRequestException | QueryGenerationException | IllegalStateException ex) {
			
			LOGGER.error("Malformed query: " + ex.getMessage(), ex);
			
			throw new BadRequestException("Malformed query.");		
						
		} catch (UnauthorizedException ex) {
			
			LOGGER.warn("Invalid authorization for API request.");
			
			throw ex;
			
		} catch (Exception ex) {
			
			LOGGER.error("Unable to execute query.", ex);
			
			throw new InternalServerErrorException("Unable to process entity query. See the log for more information.");
			
		}
		
		return queryResult;
		
	}
	
	/**
	 * Executes a query.
	 * @param entityText The text of the entity.
	 * @param minConfidence The minimum confidence.
	 * @param maxConfidence The maximum confidence.
	 * @param context The context.
	 * @param documentId The document ID.
	 * @param enrichment The enrichment.
	 * @param type The entity type.
	 * @param language The entity language code.
	 * @param uri The entity URI.
	 * @param offset The offset for paging queries.
	 * @param limit The limit for paging queries.
	 * @param apiKey The user's API key.
	 * @return An {@link ExternalQueryResult result}.
	 * @throws QueryGenerationException Thrown if the EQL is malformed.
	 * @throws EntityStoreException Thrown if the query cannot be executed.
	 * @throws InvalidQueryException Thrown if the query is invalid.
	 */
	/*public QueryResult query(String entityText, int minConfidence,
			int maxConfidence, String context, String documentId, String enrichment, String type, 
			String language, String uri, int offset, int limit, String apiKey) throws QueryGenerationException, EntityStoreException, InvalidQueryException {
		
		EntityQuery entityQuery = new EntityQuery();
		
		entityQuery.setOffset(offset);
		entityQuery.setLimit(limit);
		
		if(!StringUtils.isEmpty(entityText)) {
			entityQuery.setText(entityText);
		} else {
			LOGGER.debug("No entity text received in query.");
		}
		
		if(minConfidence != 0 || maxConfidence != 100) {
		
			// Convert the confidences to doubles.
			// This is because the confidence values are stored as decimals.
			double minConfidenceValue = (double) minConfidence / 100;
			double maxConfidenceValue = (double) maxConfidence / 100;
			
			entityQuery.setConfidenceRange(new ConfidenceRange(minConfidenceValue, maxConfidenceValue));
		
		}
		
		if(!StringUtils.isEmpty(context)) {			
			entityQuery.setContext(context);			
		} else {
			LOGGER.debug("No context received in query.");
		}
		
		if(!StringUtils.isEmpty(documentId)) {					
			entityQuery.setDocumentId(documentId);			
		} else {
			LOGGER.debug("No document ID received in query.");
		}
		
		if(!StringUtils.isEmpty(type)) {			
			entityQuery.setType(type);			
		} else {
			LOGGER.debug("No type received in query.");
		}
		
		if(!StringUtils.isEmpty(language)) {					
			entityQuery.setLanguage(language);			
		} else {
			LOGGER.debug("No language received in query.");
		}
		
		if(!StringUtils.isEmpty(uri)) {					
			entityQuery.setUri(uri);			
		} else {
			LOGGER.debug("No URI received in query.");
		}
		
		if(!StringUtils.isEmpty(enrichment)) {
			
			// The enrichments come in as a CSV string in the format name=value,name=value.
			
			List<String> enrichments = new ArrayList<String>(Arrays.asList(enrichment.split(",")));
			
			List<EntityEnrichmentFilter> entityEnrichmentFilters = new ArrayList<EntityEnrichmentFilter>();
			
			for(String enrichmentNameValue : enrichments) {
				
				// There has to be a colon in the enrichmentNameValue.
				String[] nameValue = enrichmentNameValue.split(":");
				
				// Make sure there was a colon to avoid IndexOutOfBounds.
				if(nameValue.length > 1) {
					
					String name = nameValue[0];
					String value = nameValue[1];
					
					entityEnrichmentFilters.add(new EntityEnrichmentFilter(name, value));
					
				} else {
					
					// Throw a bad request exception.
					throw new BadRequestException("Provided enrichment does not contain an equals sign. Enrichments should be provided"
							+ " in the format of: &enrichment=name1:value1,name2:value2,name3:value3");
					
				}
				
			}
			
			entityQuery.setEntityEnrichmentFilters(entityEnrichmentFilters);
			
		} else {
			LOGGER.debug("No enrichments received in query.");
		}
		
		// Get the user's ACL.
		Acl acl = getUserAcl(apiKey);
		
		QueryResult queryResult = executeQuery(entityQuery, acl, apiKey);
		
		return queryResult;			
		
	}*/
	
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
	
	private User getUserByApiKey(String apiKey) {
		
		for(User user : usersAndGroups) {
			
			if(StringUtils.equals(user.getApiKey(), apiKey)) {
				
				return user;
				
			}
			
		}
		
		return null;
		
	}
	
}