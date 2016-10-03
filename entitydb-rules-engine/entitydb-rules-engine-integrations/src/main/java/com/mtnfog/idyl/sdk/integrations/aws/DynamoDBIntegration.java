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
package com.mtnfog.idyl.sdk.integrations.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS DynamoDB that stores the entities in a DynamoDB table.
 * The DynamoDB table must already exist - this class does not create the table.
 * 
 * @author Mountain Fog, Inc.
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
					.withMap("enrichments", entity.getEnrichments());
					
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