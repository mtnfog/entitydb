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
package ai.philterd.entitydb.integrations.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

/**
 * Integration for AWS DynamoDB that stores the entities in a DynamoDB table.
 * The DynamoDB table must already exist - this class does not create the table.
 * 
 * @author Philterd, LLC
 *
 */
public class DynamoDBIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(DynamoDBIntegration.class);

	private AmazonDynamoDBClient dynamoDBClient;
	private DynamoDB dynamoDB;
	private String tableName;
	
	/**
	 * Creates a new DynamoDB integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param tableName The name of the table.
	 * @param region The AWS service region.
	 */
	public DynamoDBIntegration(String tableName, Region region) {
		
		this.tableName = tableName;
		
		dynamoDBClient = new AmazonDynamoDBClient();
		dynamoDBClient.setRegion(region);
		
		dynamoDB = new DynamoDB(dynamoDBClient);		
		
	}
	
	/**
	 * Creates a new DynamoDB integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param tableName The name of the table.
	 * @param endpoint The AWS service endpoint.
	 */
	public DynamoDBIntegration(String tableName, String endpoint) {
		
		this.tableName = tableName;
		
		dynamoDBClient = new AmazonDynamoDBClient();
		dynamoDBClient.setEndpoint(endpoint);
				
		dynamoDB = new DynamoDB(dynamoDBClient);
		
	}
	
	/**
	 * Creates a new DynamoDB integration.
	 * @param tableName The name of the table.
	 * @param region The AWS service region.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public DynamoDBIntegration(String tableName, Region region, String accessKey, String secretKey) {
		
		this.tableName = tableName;		
		
		if(StringUtils.isEmpty(accessKey)) {
			
			dynamoDBClient = new AmazonDynamoDBClient();
			
		} else {

			dynamoDBClient = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
				
		dynamoDBClient.setRegion(region);
		
		dynamoDB = new DynamoDB(dynamoDBClient);
		
	}
	
	/**
	 * Creates a new DynamoDB integration.
	 * @param tableName The name of the table.
	 * @param endpoint The AWS service endpoint.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public DynamoDBIntegration(String tableName, String endpoint, String accessKey, String secretKey) {
		
		this.tableName = tableName;		

		if(StringUtils.isEmpty(accessKey)) {
			
			dynamoDBClient = new AmazonDynamoDBClient();
			
		} else {

			dynamoDBClient = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		dynamoDBClient.setEndpoint(endpoint);
		
		dynamoDB = new DynamoDB(dynamoDBClient);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Stores the entities in a DynamoDB table.
	 */
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
		
		Table table = dynamoDB.getTable(tableName);
			
		for(Entity entity : entities) {
			
			try {
					
				// DynamoDB will throw an error if a value is null or an empty string.
				// So replace any values that could be null or empty with a value.
				String uri = (StringUtils.isEmpty(entity.getUri())) ? "none" : entity.getUri();
				String context = (StringUtils.isEmpty(entity.getContext())) ? "not-set": entity.getContext();
				String documentId = (StringUtils.isEmpty(entity.getDocumentId())) ? "not-set" : entity.getDocumentId();
				
				Item item = new Item()
					.withPrimaryKey("id", UUID.randomUUID().toString())
					.withString("text", entity.getText())
					.withString("uri", uri)
					.withDouble("confidence", entity.getConfidence())
					.withString("type", entity.getType())
					.withString("context", context)
					.withString("documentId", documentId)
					.withMap("metadata", entity.getMetadata());
					
				table.putItem(item);
				
			} catch (Exception ex) {
					
				LOGGER.error("DynamoDB integration failed on entity: {}", entity);
					
				LOGGER.error("Unable to process integration.", ex);
				
				throw new IntegrationException("Unable to process DynamoDB integration. Not all entities may have been persisted.", ex);
					
			}
				
		}			
		
	}
	
	@Override
	public void process(Entity entity) throws IntegrationException {
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		process(entities);
		
	}
		
}