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
package ai.philterd.test.entitydb.integrations;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.integrations.aws.DynamoDBIntegration;

public class DynamoDBIntegrationIT {		

	private static final Logger LOGGER = LogManager.getLogger(DynamoDBIntegrationIT.class);
    
	private static AmazonDynamoDBClient client;	
	
	private static final String PORT = System.getProperty("dynamodb.integration.port");
	private static final String ENDPOINT = "http://localhost:" + PORT;
	private static final String ACCESS_KEY = "accesskey";
	private static final String SECRET_KEY = "secretkey";
	
	private static final long READ_THROUGHPUT = 5L;
	private static final long WRITE_THROUGHPUT = 5L;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		
		LOGGER.debug("Using DynamoDB endpoint {}.", ENDPOINT);
			
		boolean tableExists = true;
		
		client = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
		client.setEndpoint(ENDPOINT);
		
		try {
		
			// Determine if the required table exists.		
			DescribeTableResult describeTableResult = client.describeTable("test");
			
		} catch (Exception ex) {
			
			LOGGER.info("DynamoDB table {} already exists.", "test");
			
			tableExists = false;
			
		}
		
		if(tableExists == false) {
				
			CreateTableRequest request = new CreateTableRequest();
			request.setTableName("test");
			
			// Add the attribute definitions.
			
			AttributeDefinition idAttributeDefinition = new AttributeDefinition();
			idAttributeDefinition.setAttributeName("id");
			idAttributeDefinition.setAttributeType("S");
			
			AttributeDefinition contextAttributeDefinition = new AttributeDefinition();
			contextAttributeDefinition.setAttributeName("context");
			contextAttributeDefinition.setAttributeType("S");
			
			Collection<AttributeDefinition> attributeDefinitions = new ArrayList<>();
			attributeDefinitions.add(idAttributeDefinition);
			attributeDefinitions.add(contextAttributeDefinition);
			
			request.setAttributeDefinitions(attributeDefinitions);
			
			// Add the key schemas.
			
			KeySchemaElement hashKeySchemaElement = new KeySchemaElement();
			hashKeySchemaElement.setAttributeName("context");
			hashKeySchemaElement.setKeyType(KeyType.HASH);
			
			KeySchemaElement rangeKeySchemaElement = new KeySchemaElement();
			rangeKeySchemaElement.setAttributeName("id");
			rangeKeySchemaElement.setKeyType(KeyType.RANGE);
			
			Collection<KeySchemaElement> keySchemaElements = new ArrayList<>();		
			keySchemaElements.add(hashKeySchemaElement);
			keySchemaElements.add(rangeKeySchemaElement);
			request.setKeySchema(keySchemaElements);
			
			// Add the throughput. This is ignored with the local DynamoDB client
			// but included for good measure because they will be used for real tables.
			request.setProvisionedThroughput(new ProvisionedThroughput(READ_THROUGHPUT, WRITE_THROUGHPUT));
			
			client.createTable(request);
		
		}
		
	}
	
    @Test
    public void save() {
    	
    	Entity entity = new Entity("George Washington");
    	entity.setConfidence(0.50);
    	entity.setType("person");
    	entity.setContext("context");
		entity.setDocumentId("documentId");
    	    	
    	DynamoDBIntegration dynamoDBIntegration = new DynamoDBIntegration("test", ENDPOINT, "accesskey", "accesskey");
    	dynamoDBIntegration.process(entity);
    	
    	
    }
    	
}