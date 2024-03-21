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
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS SQS that publishes the entities to an AWS SNS topic.
 * 
 * @author Philterd, LLC
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