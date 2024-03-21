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
package ai.philterd.test.entitydb.entitystore.dynamodb;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import ai.philterd.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import ai.philterd.entitydb.entitystore.dynamodb.model.DynamoDBStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.testing.AbstractEntityStoreTest;

/**
 * Integration tests for {@link DynamoDBEntityStore}. These tests can
 * be run against a local DynamoDB by using the local ENDPOINT
 * in {@link DynamoDBENDPOINT}.
 * 
 * This is an IT test because the Maven plugin that starts and stops
 * the DynamoDBLocal is bound to the integration test goals.
 * 
 * @author Philterd, LLC
 *
 */
public class DynamoDBEntityStoreIT extends AbstractEntityStoreTest<DynamoDBStoredEntity> {

	private static final Logger LOGGER = LogManager.getLogger(DynamoDBEntityStoreIT.class);
	
	@Override
	public EntityStore<DynamoDBStoredEntity> getEntityStore() throws EntityStoreException {
		return new DynamoDBEntityStore(ACCESS_KEY, SECRET_KEY, ENDPOINT, DynamoDBEntityStore.DEFAULT_TABLE_NAME);
	}
	
	private static AmazonDynamoDBClient client;	
	
	// Uncomment to use real DynamoDB table.
	// private static final DynamoDBEndpoint ENDPOINT = DynamoDBEndpoint.DYNAMODB_ENDPOINT_US_EAST_1;
	// private static final String ACCESS_KEY = "";
	// private static final String SECRET_KEY = "";
	
	// Uncomment to use local DynamoDB table.
	private static final String PORT = System.getProperty("dynamodb.entity.store.port");
	private static final String ENDPOINT = "http://localhost:" + PORT; // DynamoDBEndpoint.DYNAMODB_LOCAL;
	private static final String ACCESS_KEY = "accesskey";
	private static final String SECRET_KEY = "secretkey";
	
	private static final long READ_THROUGHPUT = 5L;
	private static final long WRITE_THROUGHPUT = 5L;	
		
	@Override
	@Test
	@Ignore
	public void getContexts() throws EntityStoreException {
		// DynamoDBEntityStore doesn't support this yet.
	}
		
	@Before
	public void beforeClass() throws Exception {
		
		LOGGER.debug("Using DynamoDB endpoint {}.", ENDPOINT);
			
		boolean tableExists = true;
		
		client = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
		client.setEndpoint(ENDPOINT);
		
		try {
		
			// Determine if the required table exists.		
			DescribeTableResult describeTableResult = client.describeTable(DynamoDBEntityStore.DEFAULT_TABLE_NAME);
			
		} catch (Exception ex) {
			
			LOGGER.info("DynamoDB table {} already exists.", DynamoDBEntityStore.DEFAULT_TABLE_NAME);
			
			tableExists = false;
			
		}
		
		if(tableExists == false) {
				
			CreateTableRequest request = new CreateTableRequest();
			request.setTableName(DynamoDBEntityStore.DEFAULT_TABLE_NAME);
			
			// Add the attribute definitions.
			
			AttributeDefinition idAttributeDefinition = new AttributeDefinition();
			idAttributeDefinition.setAttributeName("id");
			idAttributeDefinition.setAttributeType("S");
			
		/*	AttributeDefinition contextAttributeDefinition = new AttributeDefinition();
			contextAttributeDefinition.setAttributeName("context");
			contextAttributeDefinition.setAttributeType("S");
			
			AttributeDefinition indexedAttributeDefinition = new AttributeDefinition();
			indexedAttributeDefinition.setAttributeName("indexed");
			indexedAttributeDefinition.setAttributeType("N");*/
						
			Collection<AttributeDefinition> attributeDefinitions = new ArrayList<>();
			attributeDefinitions.add(idAttributeDefinition);
		//	attributeDefinitions.add(contextAttributeDefinition);
		//	attributeDefinitions.add(indexedAttributeDefinition);
			
			request.setAttributeDefinitions(attributeDefinitions);
			
			// Add the key schemas.
			
			KeySchemaElement hashKeySchemaElement = new KeySchemaElement();
			hashKeySchemaElement.setAttributeName(DynamoDBStoredEntity.FIELD_ID);
			hashKeySchemaElement.setKeyType(KeyType.HASH);
			
		/*	KeySchemaElement rangeKeySchemaElement = new KeySchemaElement();
			rangeKeySchemaElement.setAttributeName(DynamoDBStoredEntity.FIELD_CONTEXT);
			rangeKeySchemaElement.setKeyType(KeyType.RANGE);*/
			
			Collection<KeySchemaElement> keySchemaElements = new ArrayList<>();		
			keySchemaElements.add(hashKeySchemaElement);
			//keySchemaElements.add(rangeKeySchemaElement);						
						
			request.setKeySchema(keySchemaElements);
			
			// Add the throughput. This is ignored with the local DynamoDB client
			// but included for good measure because they will be used for real tables.
			request.setProvisionedThroughput(new ProvisionedThroughput(READ_THROUGHPUT, WRITE_THROUGHPUT));
			
			/*// Create the local secondary index on INDEXED.
			ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
			indexKeySchema.add(new KeySchemaElement().withAttributeName(DynamoDBStoredEntity.FIELD_ID).withKeyType(KeyType.HASH));  //Partition key
			indexKeySchema.add(new KeySchemaElement().withAttributeName(DynamoDBStoredEntity.FIELD_INDEXED).withKeyType(KeyType.RANGE));  //Sort key

			Projection projection = new Projection().withProjectionType(ProjectionType.KEYS_ONLY);
			//ArrayList<String> nonKeyAttributes = new ArrayList<String>();
			//nonKeyAttributes.add("Genre");
			//projection.setNonKeyAttributes(nonKeyAttributes);

			LocalSecondaryIndex localSecondaryIndex = new LocalSecondaryIndex()
			    .withIndexName("AlbumTitleIndex").withKeySchema(indexKeySchema).withProjection(projection);

			ArrayList<LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<LocalSecondaryIndex>();
			localSecondaryIndexes.add(localSecondaryIndex);
			request.setLocalSecondaryIndexes(localSecondaryIndexes);*/
			
			// LOGGER.debug("Creating DynamoDB table.");
			client.createTable(request);
		
		}
		
	}
	
	@After
	public void emptyTable() {
		
		client.deleteTable(DynamoDBEntityStore.DEFAULT_TABLE_NAME);		
		
	}
	
}