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
package ai.philterd.entitydb.entitystore.dynamodb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.entitystore.dynamodb.model.DynamoDBEndpoint;
import ai.philterd.entitydb.entitystore.dynamodb.model.DynamoDBStoredEntity;
import ai.philterd.entitydb.model.eql.EntityMetadataFilter;
import ai.philterd.entitydb.model.eql.EntityQuery;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.search.IndexedEntity;

/**
 * Implementation of {@link EntityStore} that utilizes
 * a AWS DynamoDB table to store {@link Entity entities}.
 * 
 * The querying functionality may not be optimized and
 * better queries can be written using low level DynamoDB APIs.
 * 
 * This entity store does not support case-sensitive querying of entity
 * metadata.
 * 
 * @author Philterd, LLC
 *
 */
public class DynamoDBEntityStore implements EntityStore<DynamoDBStoredEntity> {

	private static final Logger LOGGER = LogManager.getLogger(DynamoDBEntityStore.class);
	
	/**
	 * The default DynamoDB table name. The table name can be changed by calling
	 * <code>setTableName()</code> after instantiating a new {@link DynamoDBEntityStore store}.
	 */
	public static final String DEFAULT_TABLE_NAME = "entities";
	
	private AmazonDynamoDBClient client;
	private DynamoDBMapper mapper;
	private String tableName = DEFAULT_TABLE_NAME;
	
	/**
	 * Creates a new {@link DynamoDBEntityStore}.
	 * @param endpoint The AWS DynamoDB {@link DynamoDBEndpoint endpoint}. Set a local endpoint to use the local DynamoDB.
	 * @param tableName The name of the DynamoDB table.
	 */
	public DynamoDBEntityStore(DynamoDBEndpoint endpoint, String tableName) {
		
		client = new AmazonDynamoDBClient();
		client.setEndpoint(endpoint.getEndpoint());
		
		this.tableName = tableName;
		
		mapper = new DynamoDBMapper(client);
		
	}
	
	/**
	 * Creates a new {@link DynamoDBEntityStore}.
	 * @param endpoint The AWS DynamoDB endpoint. Set a local endpoint to use the local DynamoDB.
	 * @param tableName The name of the DynamoDB table.
	 */
	public DynamoDBEntityStore(String endpoint, String tableName) {
		
		client = new AmazonDynamoDBClient();
		client.setEndpoint(endpoint);
		
		this.tableName = tableName;
		
		mapper = new DynamoDBMapper(client);
		
	}
	
	/**
	 * Creates a new {@link DynamoDBEntityStore}.
	 * @param accessKey The AWS DynamoDb access key.
	 * @param secretKey The AWS DynamoD secret key.
	 * @param endpoint The AWS DynamoDB endpoint. Set a local endpoint to use the local DynamoDB.
	 * @param tableName The name of the DynamoDB table.
	 */
	public DynamoDBEntityStore(String accessKey, String secretKey, String endpoint, String tableName) {
		
		client = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));
		client.setEndpoint(endpoint);
		
		this.tableName = tableName;
		
		mapper = new DynamoDBMapper(client);
		
	}
	
	/**
	 * Creates a new {@link DynamoDBEntityStore}. Initializes an
	 * {@link AmazonDynamoDBClient} using instance role IAM permissions.
	 * @param endpoint The AWS DynamoDB {@link DynamoDBEndpoint endpoint}. Set a local endpoint to use the local DynamoDB.
	 */
	public DynamoDBEntityStore(DynamoDBEndpoint endpoint) {
		
		client = new AmazonDynamoDBClient();
		client.setEndpoint(endpoint.getEndpoint());
		
		mapper = new DynamoDBMapper(client);
		
	}
	

	@Override
	public String getStatus() {
	
		return String.format("DynamoDB table name: %s", tableName);
		
	}
	

	@Override
	public List<DynamoDBStoredEntity> getNonIndexedEntities(int limit) {
		
		/*Condition rangeKeyCondition = new Condition()
		        .withComparisonOperator(ComparisonOperator.EQ.toString())
		        .withAttributeValueList(new AttributeValue().withN("0"));

		DynamoDBQueryExpression<DynamoDBStoredEntity> queryExpression = new DynamoDBQueryExpression<DynamoDBStoredEntity>()
		        .withRangeKeyCondition(DynamoDBStoredEntity.FIELD_INDEXED, rangeKeyCondition);
				
		List<DynamoDBStoredEntity> nonIndexedEntities = mapper.query(DynamoDBStoredEntity.class, queryExpression);
		
		return nonIndexedEntities;*/
		
		// The following code gets the entities that are visible but not indexed
		// from the DynamoDB table using a table scan. This is not ideal and should
		// be changed.
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();		
		scanExpression.setConsistentRead(false);
		
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		Map<String, String> expressionAttributeNames = new HashMap<>();				
				
		expressionAttributeNames.put("#indexed", DynamoDBStoredEntity.FIELD_INDEXED);			
		expressionAttributeValues.put(":indexed", new AttributeValue().withN("0"));			
		
		expressionAttributeNames.put("#visible", DynamoDBStoredEntity.FIELD_VISIBLE);			
		expressionAttributeValues.put(":visible", new AttributeValue().withN("1"));	
		
		scanExpression.setFilterExpression("(#indexed = :indexed) and (#visible = :visible)");
					
		scanExpression.setExpressionAttributeValues(expressionAttributeValues);
		scanExpression.setExpressionAttributeNames(expressionAttributeNames);
		
		ScanResultPage<DynamoDBStoredEntity> scanPage = mapper.scanPage(DynamoDBStoredEntity.class, scanExpression);
				
		return scanPage.getResults();
		
	}
	

	@Override
	public boolean markEntityAsIndexed(String entityId) {

		boolean result = false;
		
		DynamoDBStoredEntity entity = getEntityById(entityId);

		if(entity != null) {
		
			entity.setIndexed(System.currentTimeMillis());
			
			try {
			
				mapper.save(entity, getMapperConfig());
				
				result = true;
				
			} catch (Exception ex) {
			
				LOGGER.error("The entity " + entityId + " in DynamoDB could not be marked as indexed.", ex);
				
			}
			
		}

		return result;
		
	}
	

	@Override
	public long markEntitiesAsIndexed(Collection<String> entityIds) {
		
		int indexed = 0;
		
		for(String entityId : entityIds) {
			
			boolean marked = markEntityAsIndexed(entityId);
			
			if(marked) {
				indexed++;
			}
			
		}
		
		return indexed;
		
	}
	

	@Override
	public String storeEntity(Entity entity, String acl) throws EntityStoreException {
		
		LOGGER.trace("Storing entity {} in DynamoDB under context {}.", entity.toString(), entity.getContext());
		
		String entityId = null;
		
		try {
		
			// Convert the entity to a StoredDynamoDBEntity.
			DynamoDBStoredEntity storedDynamoDbEntity = DynamoDBStoredEntity.fromEntity(entity, acl);
			
			// Save it to DynamoDb.
	        mapper.save(storedDynamoDbEntity, getMapperConfig());
	        
	        entityId = storedDynamoDbEntity.getId().toString();
        
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to store entity.", ex);
			
		}
		
		return entityId;
	        
	}


	@Override
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) {
		
		LOGGER.trace("Storing {} entities in DynamoDB.");
		
		Map<Entity, String> storedEntities = new HashMap<Entity, String>();
		
		List<DynamoDBStoredEntity> storedDynamoDbEntities = new LinkedList<DynamoDBStoredEntity>();
		
		for(Entity entity : entities) {			
			
			DynamoDBStoredEntity dynamoDbStoredEntity = DynamoDBStoredEntity.fromEntity(entity, acl);
			
			storedDynamoDbEntities.add(dynamoDbStoredEntity);	
			
			storedEntities.put(entity, dynamoDbStoredEntity.getId().toString());
			
		}
		
		mapper.batchWrite(storedDynamoDbEntities, Collections.emptyList(), getMapperConfig());
		
		return storedEntities;
		
	}
	

	@Override
	public List<DynamoDBStoredEntity> getEntitiesByIds(List<String> entityIds, boolean maskAcl) {
		
		List<DynamoDBStoredEntity> dynamoDBStoredEntities = new LinkedList<DynamoDBStoredEntity>();
		
		for(String entityId : entityIds) {
			
			DynamoDBStoredEntity dynamoDBStoredEntity = getEntityById(entityId);
			
			if(maskAcl) {
				dynamoDBStoredEntity.setAcl(StringUtils.EMPTY);
			}
			
			dynamoDBStoredEntities.add(dynamoDBStoredEntity);
			
		}
		
		return dynamoDBStoredEntities;
		
	}
	

	@Override
	public void deleteEntity(String entityId) {
		
		DynamoDBStoredEntity entity = new DynamoDBStoredEntity();
		entity.setId(entityId);
		
		mapper.delete(entity);
		
	}
	
	/**
	 * 
	 */
	@Override
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException {
		
		String newEntityId = StringUtils.EMPTY;
		
		DynamoDBStoredEntity entity = getEntityById(entityId);
		
		if(entity != null) {
			
			// Set the original entity as not visible and update it.
		
			entity.setVisible(0);
			mapper.save(entity, getMapperConfig());
			
			// Create the cloned entity and save it.
			DynamoDBStoredEntity cloned = new DynamoDBStoredEntity();
			
			cloned.setAcl(acl.toString());
			cloned.setTimestamp(System.currentTimeMillis());
			cloned.setConfidence(entity.getConfidence());
			cloned.setContext(entity.getContext());
			cloned.setDocumentId(entity.getDocumentId());
			cloned.setMetadata(entity.getMetadata());
			cloned.setExtractionDate(entity.getExtractionDate());
			cloned.setLanguage(entity.getLanguage());
			cloned.setText(entity.getText());
			cloned.setType(entity.getType());
			cloned.setUri(entity.getUri());
			
			// Make the ID for the new entity and set it.
			newEntityId = EntityIdGenerator.generateEntityId(cloned.getText(), cloned.getConfidence(), cloned.getLanguage(), cloned.getContext(), cloned.getDocumentId(), acl);
			cloned.setId(newEntityId);
			
			mapper.save(cloned, getMapperConfig());
		
		} else {
			
			throw new NonexistantEntityException("The entity does not exist.");
			
		}
		
		return newEntityId;
		
	}
	
	/**
	 * {@inheritDoc}
	 * This query is case-sensitive. This query performs a table scan. 
	 * Wildcards are not permitted.
	 * Reads are not consistent and all conditions of the
	 * query are logically AND'd.
	 */
	@Override
	public QueryResult query(EntityQuery entityQuery) throws EntityStoreException {
		
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		Map<String, String> expressionAttributeNames = new HashMap<>();
		
		StringBuilder sb = new StringBuilder();
		
		// All DynamoDB queries are case-sensitive.
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		if(!StringUtils.isEmpty(entityQuery.getText())) {
			
			LOGGER.debug("Querying for entity text {}.", entityQuery.getText());
			
			// text is a DyanmoDB reserved keyword.
			expressionAttributeNames.put("#text", DynamoDBStoredEntity.FIELD_TEXT);			
			expressionAttributeValues.put(":text", new AttributeValue().withS(entityQuery.getText()));			
			sb.append("(#text = :text) and ");
			
		}
		
		if(entityQuery.getType() != null) {
			
			LOGGER.debug("Querying against entity type {}.", entityQuery.getType());
			
			expressionAttributeNames.put("#type", DynamoDBStoredEntity.FIELD_TYPE);			
			expressionAttributeValues.put(":type", new AttributeValue().withS(entityQuery.getType()));			
			sb.append("(#type = :type) and ");
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getUri())) {
			
			LOGGER.debug("Querying against URI {}.", entityQuery.getUri());
						
			expressionAttributeNames.put("#uri", DynamoDBStoredEntity.FIELD_URI);			
			expressionAttributeValues.put(":uri", new AttributeValue().withS(entityQuery.getUri()));			
			sb.append("(#uri = :uri) and ");
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getLanguageCode())) {
			
			LOGGER.debug("Querying against language {}.", entityQuery.getLanguageCode());
						
			expressionAttributeNames.put("#language", DynamoDBStoredEntity.FIELD_LANGUAGE);			
			expressionAttributeValues.put(":language", new AttributeValue().withS(entityQuery.getLanguageCode()));			
			sb.append("(#language = :language) and ");
			
		}
				
		if(!StringUtils.isEmpty(entityQuery.getContext())) {
			
			LOGGER.debug("Querying against {} contexts.", entityQuery.getContext());
						
			expressionAttributeNames.put("#context", DynamoDBStoredEntity.FIELD_CONTEXT);			
			expressionAttributeValues.put(":context", new AttributeValue().withS(entityQuery.getContext()));			
			sb.append("(#context = :context) and ");
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getDocumentId())) {
			
			LOGGER.debug("Querying against {} document IDs.", entityQuery.getDocumentId());

			expressionAttributeNames.put("#documentId", DynamoDBStoredEntity.FIELD_DOCUMENT_ID);			
			expressionAttributeValues.put(":documentId", new AttributeValue().withS(entityQuery.getDocumentId()));			
			sb.append("(#documentId = :documentId) and ");
			
		}
				
		// Add a confidence range to the query if it is in the query.
		if(entityQuery.getConfidenceRange() != null) {
			
			LOGGER.debug("Querying with mininum confidence: {}", entityQuery.getConfidenceRange().getMinimum());
			LOGGER.debug("Querying with maximum confidence: {}", entityQuery.getConfidenceRange().getMaximum());
			
			expressionAttributeValues.put(":minConfidence", new AttributeValue().withN(String.valueOf(entityQuery.getConfidenceRange().getMinimum())));
			expressionAttributeValues.put(":maxConfidence", new AttributeValue().withN(String.valueOf(entityQuery.getConfidenceRange().getMaximum())));
			
			sb.append("((confidence >= :minConfidence) and (confidence <= :maxConfidence)) and ");
						
		}
				
		if(!CollectionUtils.isEmpty(entityQuery.getEntityMetadataFilters())) {
									
			for(EntityMetadataFilter filter : entityQuery.getEntityMetadataFilters()) {					

				expressionAttributeValues.put(":" + filter.getName(), new AttributeValue().withS(filter.getValue()));			
				sb.append("(metadata." + filter.getName() + " = :" + filter.getName() + ") and ");
				
			}										
			
		}
		
		// Remove the trailing " and " from the filter expression.
		String filter = sb.toString();
		
		if(filter.length() > 0) {
			
			filter = filter.substring(0, filter.length() - 5);
			LOGGER.info("DynamoDB scan filter is: {}", filter);
			scanExpression.setFilterExpression(filter);
			
		}				
		
		scanExpression.setLimit(entityQuery.getLimit());
				
		// See: http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#Expressions.SpecifyingConditions.ConditionExpressions
		
		// Can't add expression names and values is there is no expression.
		if(StringUtils.isNotEmpty(filter)) {
		
			// DynamoDB will complain if we add an empty collection.
			if(expressionAttributeValues.size() > 0) {
				scanExpression.setExpressionAttributeValues(expressionAttributeValues);
			}
			
			// DynamoDB will complain if we add an empty collection.
			if(expressionAttributeNames.size() > 0) {
				scanExpression.setExpressionAttributeNames(expressionAttributeNames);
			}
		
		}
				
		ScanResultPage<DynamoDBStoredEntity> scanPage = mapper.scanPage(DynamoDBStoredEntity.class, scanExpression);				
		
		String queryId = UUID.randomUUID().toString();

		List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		for(DynamoDBStoredEntity entity : scanPage.getResults()) {
			
			try {
				
				indexedEntities.add(entity.toIndexedEntity());
				
			} catch (MalformedAclException ex) {
				
				LOGGER.error("The ACL for entity " + entity.getId() + " is malformed.", ex);
				
				throw new EntityStoreException("The ACL for entity " + entity.getId() + " is malformed.");
				
			}
			
		}
		
		QueryResult queryResult = new QueryResult(indexedEntities, queryId);
		
		return queryResult;
		
	}


	@Override
	public DynamoDBStoredEntity getEntityById(String id) {
				
		DynamoDBStoredEntity dynamoDBStoredEntity = new DynamoDBStoredEntity();
		dynamoDBStoredEntity.setId(id);
		
		DynamoDBQueryExpression<DynamoDBStoredEntity> queryExpression = new DynamoDBQueryExpression<DynamoDBStoredEntity>()
		        .withHashKeyValues(dynamoDBStoredEntity);
		
		List<DynamoDBStoredEntity> queryResults = mapper.query(DynamoDBStoredEntity.class, queryExpression);
		
		if(CollectionUtils.isNotEmpty(queryResults)) {
		
			return queryResults.get(0);
			
		} else {
			
			return null;
			
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * This count is approximate.
	 */
	@Override
	public long getEntityCount() {

		DescribeTableResult result = client.describeTable(getTableName());
		
		return result.getTable().getItemCount();
		
	}


	@Override
	public long getEntityCount(String context) {

		DynamoDBStoredEntity templateStoredDynamoDbEntity = new DynamoDBStoredEntity();
		templateStoredDynamoDbEntity.setContext(context);
		
        DynamoDBQueryExpression<DynamoDBStoredEntity> queryExpression = new DynamoDBQueryExpression<DynamoDBStoredEntity>()
            .withHashKeyValues(templateStoredDynamoDbEntity);
        
        int count = mapper.count(DynamoDBStoredEntity.class, queryExpression, getMapperConfig());
        
        return count;
        
	}


	@Override
	public List<String> getContexts() {

		// TODO: Find a good way to get the unique contexts in the DynamoDB table.
		
		throw new NotImplementedException("Not implemented.");
		
	}


	@Override
	public void deleteContext(String context) {
		
		DynamoDBStoredEntity templateStoredDynamoDbEntity = new DynamoDBStoredEntity();
		templateStoredDynamoDbEntity.setContext(context);
		
        DynamoDBQueryExpression<DynamoDBStoredEntity> queryExpression = new DynamoDBQueryExpression<DynamoDBStoredEntity>()
            .withHashKeyValues(templateStoredDynamoDbEntity);
        
        PaginatedQueryList<DynamoDBStoredEntity> result = mapper.query(DynamoDBStoredEntity.class, queryExpression, getMapperConfig());
        
		mapper.batchDelete(result);
		
	}
	
	/**
	 * {@inheritDoc}
	 * This performs a table scan to find all entities extracted
	 * under the given <code>documentId</code>.
	 */
	@Override
	public void deleteDocument(String documentId) {

		Map<String, Condition> filter = new HashMap<String, Condition>();
		
		filter.put("documentId", new Condition().withComparisonOperator(ComparisonOperator.EQ)
		        .withAttributeValueList(new AttributeValue().withS(documentId)));
		 
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withScanFilter(filter);
		
		PaginatedScanList<DynamoDBStoredEntity> result = mapper.scan(DynamoDBStoredEntity.class, scanExpression, getMapperConfig());
		
		LOGGER.debug("Going to delete {} rows for document {}.", result.size(), documentId);
		
		mapper.batchDelete(result);
		
	}

	/**
	 * Calling this function is not necessary for
	 * {@link DynamoDBEntityStore} and it has no effect.
	 */
	@Override
	public void close() {
		// Not needed for this implementation.
	}
	
	private DynamoDBMapperConfig getMapperConfig() {
		return new DynamoDBMapperConfig(new TableNameOverride(tableName));
	}

	/**
	 * Gets the name of the DynamoDB table.
	 * @return The name of the DynamoDB table.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the name of the DynamoDB table.
	 * @param tableName The name of the DynamoDB table.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}