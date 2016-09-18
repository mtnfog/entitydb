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
package com.mtnfog.idyl.sdk.integrations.aws;

import java.util.ArrayList;
import java.util.Collection;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS SQS that publishes the entities to an AWS SNS topic.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class SqsIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(SqsIntegration.class);

	private AmazonSQSClient sqsClient;
	private String queueUrl;
	private int delaySeconds;
	private Gson gson;
	
	/**
	 * Creates a new SQS integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param queueUrl The URL of the SQS queue.
	 * @param delaySeconds The number of seconds before a message becomes visible on the queue.
	 * @param region The AWS service region.
	 */
	public SqsIntegration(String queueUrl, int delaySeconds, Region region) {
		
		sqsClient = new AmazonSQSClient();
		gson = new Gson();
		
		this.queueUrl = queueUrl;
		this.delaySeconds = delaySeconds;
		
		sqsClient.setRegion(region);
		
	}
	
	/**
	 * Creates a new SQS integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param queueUrl The URL of the SQS queue.
	 * @param delaySeconds The number of seconds before a message becomes visible on the queue.
	 * @param endpoint The AWS service endpoint.
	 */
	public SqsIntegration(String queueUrl, int delaySeconds, String endpoint) {
		
		sqsClient = new AmazonSQSClient();
		gson = new Gson();
		
		this.queueUrl = queueUrl;
		this.delaySeconds = delaySeconds;
		
		sqsClient.setEndpoint(endpoint);
		
	}
	
	/**
	 * Creates a new SQS integration.
	 * @param queueUrl The URL of the SQS queue.
	 * @param delaySeconds The number of seconds before a message becomes visible on the queue.
	 * @param region The AWS service region.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SqsIntegration(String queueUrl, int delaySeconds, Region region, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			sqsClient = new AmazonSQSClient();
			
		} else {

			sqsClient = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.queueUrl = queueUrl;
		this.delaySeconds = delaySeconds;		
		
		sqsClient.setRegion(region);
		
	}
	
	/**
	 * Creates a new SQS integration.
	 * @param queueUrl The URL of the SQS queue.
	 * @param delaySeconds The number of seconds before a message becomes visible on the queue.
	 * @param endpoint The AWS service endpoint.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SqsIntegration(String queueUrl, int delaySeconds, String endpoint, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			sqsClient = new AmazonSQSClient();
			
		} else {

			sqsClient = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.queueUrl = queueUrl;
		this.delaySeconds = delaySeconds;		
		
		sqsClient.setEndpoint(endpoint);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Publishes the entities to an AWS SQS queue. The entities published as
	 * JSON documents. The context and documentId under which the entities were 
	 * extracted are published as message attributes with the names context
	 * and documentId.
	 */
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
								
		for(Entity entity : entities) {
			
			try {
				
				String message = gson.toJson(entity);
				
				LOGGER.debug("Publishing message to queue: {}", message);
			
				SendMessageRequest sendMessageRequest = new SendMessageRequest();
				sendMessageRequest.setMessageBody(message);
				sendMessageRequest.setDelaySeconds(delaySeconds);
				sendMessageRequest.setQueueUrl(queueUrl);

				// Not going to check max message size. Messages that are too large will be rejected.
				
				sqsClient.sendMessage(sendMessageRequest);

			} catch (Exception ex) {
				
				LOGGER.error("SQS integration failed on entity: {}", entity);
				
				LOGGER.error("Unable to process integration.", ex);
				
				throw new IntegrationException("Unable to process SQS integration. Not all entities may have been queued.", ex);
				
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