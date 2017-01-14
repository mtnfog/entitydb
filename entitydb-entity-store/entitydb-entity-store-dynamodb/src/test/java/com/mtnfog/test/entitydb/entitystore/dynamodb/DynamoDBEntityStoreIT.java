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
package com.mtnfog.test.entitydb.entitystore.dynamodb;

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
import com.mtnfog.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import com.mtnfog.entitydb.entitystore.dynamodb.model.DynamoDBStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.testing.AbstractEntityStoreTest;

/**
 * Integration tests for {@link DynamoDBEntityStore}. These tests can
 * be run against a local DynamoDB by using the local ENDPOINT
 * in {@link DynamoDBENDPOINT}.
 * 
 * This is an IT test because the Maven plugin that starts and stops
 * the DynamoDBLocal is bound to the integration test goals.
 * 
 * @author Mountain Fog, Inc.
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