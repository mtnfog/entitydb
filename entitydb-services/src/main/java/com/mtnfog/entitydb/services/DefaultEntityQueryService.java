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

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.datastore.repository.ContinuousQueryRepository;
import com.mtnfog.entitydb.datastore.repository.NotificationRepository;
import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;
import com.mtnfog.entitydb.model.datastore.entities.NotificationEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;
import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.model.exceptions.api.BadRequestException;
import com.mtnfog.entitydb.model.exceptions.api.InternalServerErrorException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;
import com.mtnfog.entitydb.model.notifications.NotificationType;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.model.services.NotificationService;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.exceptions.QueryGenerationException;
import com.mtnfog.entitydb.eql.filters.EqlFilters;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Default implementation of {@link EntityQueryService}.
 * 
 * @author Mountain Fog, Inc.
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
	public QueryResult eql(String query, String apiKey, int continuous, int days) {
				
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
					throw new InternalServerErrorException("Unable to audit query.");
					
				}
			
		} catch (BadRequestException | QueryGenerationException | IllegalStateException ex) {
			
			LOGGER.error("Malformed query: " + ex.getMessage(), ex);
			
			throw new BadRequestException("Malformed query.");		
									
		} catch (Exception ex) {
			
			LOGGER.error("Unable to execute query.", ex);
			
			throw new InternalServerErrorException("Unable to execute the query. See the log for more information.");
			
		}
		
		return queryResult;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeContinuousQueries(Entity entity, String entityId) {
		
		long startTime = System.currentTimeMillis();
		
		List<ContinuousQueryEntity> continuousQueryEntities = continuousQueryRepository.getNonExpiredContinuousQueries();
		
		// Report the number of continuous queries being executed.
		metricReporter.report(MetricReporter.MEASUREMENT_CONTINUOUS_QUERY, "count", continuousQueryEntities.size(), Unit.COUNT);
		
		for(ContinuousQueryEntity continuousQueryEntity : continuousQueryEntities) {
									
			boolean match = EqlFilters.isMatch(entity, continuousQueryEntity.getQuery());		
			
			if(match) {
				
				// If this is a match notify the owner of the continuous query.
				
				String notification = String.format("Continuous query %s matched on entity %s.", continuousQueryEntity.getId(), entityId);
				
				NotificationEntity notificationEntity = new NotificationEntity();
				notificationEntity.setUser(continuousQueryEntity.getUser());
				notificationEntity.setType(NotificationType.CONTINUOUS_QUERY.getValue());
				notificationEntity.setNotification(notification);
				
				notificationRepository.save(notificationEntity);
				
				// TODO: Send a message to the continuous query's SNS topic.
			
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