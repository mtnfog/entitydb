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
package ai.philterd.entitydb.services;

import java.util.Collection;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.configuration.EntityDbProperties;
import ai.philterd.entitydb.datastore.repository.ContinuousQueryRepository;
import ai.philterd.entitydb.datastore.repository.NotificationRepository;
import ai.philterd.entitydb.datastore.repository.UserRepository;
import ai.philterd.entitydb.model.audit.AuditAction;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.datastore.entities.ContinuousQueryEntity;
import ai.philterd.entitydb.model.datastore.entities.NotificationEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;
import ai.philterd.entitydb.model.domain.ContinuousQuery;
import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.InvalidQueryException;
import ai.philterd.entitydb.model.exceptions.MalformedQueryException;
import ai.philterd.entitydb.model.exceptions.QueryExecutionException;
import ai.philterd.entitydb.model.exceptions.UnableToAuditException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;
import ai.philterd.entitydb.model.notifications.NotificationType;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.model.security.Acl;
import ai.philterd.entitydb.model.services.EntityQueryService;
import ai.philterd.entitydb.model.services.NotificationService;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.exceptions.QueryGenerationException;
import com.mtnfog.entitydb.eql.filters.EqlFilters;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Default implementation of {@link EntityQueryService}.
 * 
 * @author Philterd, LLC
 *
 */
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
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MetricReporter metricReporter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Cacheable("nonExpiredContinuousQueries")
	public List<ContinuousQueryEntity> getNonExpiredContinuousQueries() {		
		return continuousQueryRepository.getNonExpiredContinuousQueries();		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Cacheable("continuousQueriesByUser")
	public List<ContinuousQueryEntity> findByUserOrderByIdDesc(UserEntity userEntity) {
		return continuousQueryRepository.findByUserOrderByIdDesc(userEntity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@CachePut("nonExpiredContinuousQueries")
	public ContinuousQueryEntity save(ContinuousQueryEntity continuousQueryEntity) {
		return continuousQueryRepository.save(continuousQueryEntity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@CacheEvict(value = "nonExpiredContinuousQueries", allEntries=true)
	public void delete(ContinuousQueryEntity continuousQueryEntity) {
		continuousQueryRepository.delete(continuousQueryEntity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryResult eql(String query, String apiKey, int continuous, int days) throws MalformedQueryException, QueryExecutionException {
				
		long startTime = System.currentTimeMillis();

		QueryResult queryResult = null;
		
		try {
			
			EntityQuery entityQuery = Eql.generate(query);
			
				// Audit this query.
				boolean auditResult = auditLogger.audit(query, System.currentTimeMillis(), apiKey);
				
				if(auditResult) {
				
					// Get the user from the API key.					
					UserEntity userEntity = userRepository.getByApiKey(apiKey);
					
					User user = User.fromEntity(userEntity);
					
					// Execute the query.
					queryResult = executeQuery(entityQuery, user);
					
					if(continuous > 0) {
					
						String snsTopicArn = notificationService.createNotificationTopic(user);
						
						// The query was validated and executed successfully.
						// If it is set to be continuous we can now persist it.
						continuousQueryRepository.save(new ContinuousQueryEntity(userEntity, query, new Date(), days, snsTopicArn));
					
					}
					
					// Report the execution time for successful queries.
					metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_QUERY, "executionTime", startTime);
				
				} else {
					
					// The search could not be audited.
					throw new UnableToAuditException("Unable to audit query.");
					
				}
			
		} catch (QueryGenerationException | IllegalStateException ex) {
			
			LOGGER.error("Malformed query: " + ex.getMessage(), ex);
			
			throw new MalformedQueryException("Malformed query.");		
									
		} catch (Exception ex) {
			
			LOGGER.error("Unable to execute query.", ex);
			
			throw new QueryExecutionException("Unable to execute the query. See the log for more information.");
			
		}
		
		return queryResult;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeContinuousQueries(Collection<Entity> entities, Acl acl, long entitiesReceivedTimestamp) {
		
		long startTime = System.currentTimeMillis();
		
		List<ContinuousQueryEntity> continuousQueryEntities = continuousQueryRepository.getNonExpiredContinuousQueries();
		
		// Report the number of continuous queries being executed.
		metricReporter.report(MetricReporter.MEASUREMENT_CONTINUOUS_QUERY, "count", continuousQueryEntities.size(), Unit.COUNT);
		
		for(ContinuousQueryEntity continuousQueryEntity : continuousQueryEntities) {
				
			// Execute the query on each entity.
			for(Entity entity : entities) {
			
				// Generate the entity's ID.
				final String entityId = EntityIdGenerator.generateEntityId(entity, acl.toString());
				
				// Get the user (owner) of the continuous query.
				UserEntity userEntity = continuousQueryEntity.getUser();
				User user = User.fromEntity(userEntity);
				
				// Make sure the owner of this continuous query is actually able to see this entity.
				// This check is likely cheaper than evaluating the continuous query so do it first.
				boolean isVisible = Acl.isEntityVisibleToUser(acl, user);
				
				if(isVisible) {
				
					boolean match = EqlFilters.isMatch(entity, continuousQueryEntity.getQuery());		
					
					if(match) {
							
						// Notify the owner of the continuous query of the match.
						
						String notification = String.format("Continuous query %s matched on entity %s.", continuousQueryEntity.getId(), entityId);
						
						NotificationEntity notificationEntity = new NotificationEntity();
						notificationEntity.setUser(continuousQueryEntity.getUser());
						notificationEntity.setType(NotificationType.CONTINUOUS_QUERY.getValue());
						notificationEntity.setNotification(notification);
						
						notificationRepository.save(notificationEntity);
						
						// Generate a notification for the user.
						ContinuousQuery continuousQuery = ContinuousQuery.fromEntity(continuousQueryEntity);
						notificationService.sendContinuousQueryNotification(continuousQuery, entity);
						
						// Record this time-to-alert metric.
						metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_CONTINUOUS_QUERY, "timeToAlert", entitiesReceivedTimestamp);
							
					}
					
				}
			
			}
						
		}
		
		// Report the time taken to execute the continuous queries.
		metricReporter.reportElapsedTime(MetricReporter.MEASUREMENT_CONTINUOUS_QUERY, "executionTime", startTime);
		
	}
	
	private QueryResult executeQuery(EntityQuery entityQuery, User user) throws QueryGenerationException, EntityStoreException, InvalidQueryException {
		
		QueryResult queryResult = null;
				
		LOGGER.trace("Executing search against the search index.");
		
		// Give an ID to this query.
		String queryId = UUID.randomUUID().toString();
		
		// Execute the entity query against the search index.
		List<IndexedEntity> indexedEntities = searchIndex.queryForIndexedEntities(entityQuery, user);
				
		if(CollectionUtils.isNotEmpty(indexedEntities)) {
			
			Iterator<IndexedEntity> it = indexedEntities.iterator(); 
			
			while(it.hasNext()) {
				
				IndexedEntity indexedEntity = (IndexedEntity) it.next();
				
				// Set to true by default in case auditing is not enabled.
				boolean auditResult = true;
				
				if(properties.isAuditEnabled()) {				
					auditResult = auditLogger.audit(indexedEntity.getEntityId(), System.currentTimeMillis(), user.getUsername(), AuditAction.SEARCH_RESULT);					
				}
								
				if(!auditResult) {
				
					// If it can't be audited don't return it.					
					it.remove();
					
					LOGGER.warn("Entity ID {} could not be audited so it was not returned in query results.", indexedEntity.getEntityId());
					
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
			
			queryResult = new QueryResult(Collections.<IndexedEntity>emptyList(), queryId);
			
		}
			
		return queryResult;
					
	}
			
}