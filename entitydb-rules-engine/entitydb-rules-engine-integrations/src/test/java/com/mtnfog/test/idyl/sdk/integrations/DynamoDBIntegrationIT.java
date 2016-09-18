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
package com.mtnfog.test.idyl.sdk.integrations;

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
import com.mtnfog.entity.Entity;
import com.mtnfog.idyl.sdk.integrations.aws.DynamoDBIntegration;

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