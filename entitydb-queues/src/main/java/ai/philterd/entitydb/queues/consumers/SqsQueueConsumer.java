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
package ai.philterd.entitydb.queues.consumers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.gson.Gson;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConstants;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.queue.QueueIngestMessage;
import ai.philterd.entitydb.model.queue.QueueUpdateAclMessage;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.search.IndexedEntity;

/**
 * A queue consumer that consumes from an AWS SQS queue.
 * 
 * @author Philterd, LLC
 *
 */
public class SqsQueueConsumer extends AbstractQueueConsumer implements QueueConsumer {	

	private static final Logger LOGGER = LogManager.getLogger(SqsQueueConsumer.class);
	
	private Gson gson;

	private AmazonSQSClient client;	
	private String queueUrl;
	
	private static final int DEFAULT_READ_TIMEOUT = (int)TimeUnit.SECONDS.toMillis(10);
	private static final int DEFAULT_CONNECT_TIMEOUT  = (int)TimeUnit.SECONDS.toMillis(10);
	private static final int DEFAULT_MAX_RETRIES  = (int)TimeUnit.SECONDS.toMillis(10);
	private static final int DEFAULT_MAX_CONNECTIONS  = (int)TimeUnit.SECONDS.toMillis(10);
	
	private int visibilityTimeout;
	
	public SqsQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			AuditLogger auditLogger, MetricReporter metricReporter, String endpoint, String queueUrl, int visibilityTimeout,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		super(entityStore, rulesEngines,  auditLogger, metricReporter, indexerCache);
		
		client = new AmazonSQSClient(getClientConfiguration());
		client.setEndpoint(endpoint);
		gson = new Gson();
		
		this.queueUrl = queueUrl;
		this.visibilityTimeout = visibilityTimeout;
		
	}
	
	public SqsQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines, 
			AuditLogger auditLogger, MetricReporter metricReporter, String endpoint, String queueUrl, 
			String accessKey, String secretKey, int visibilityTimeout,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
			
		super(entityStore, rulesEngines, auditLogger, metricReporter, indexerCache);
		
		client = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey), getClientConfiguration());
		client.setEndpoint(endpoint);
		
		gson = new Gson();
					
		this.queueUrl = queueUrl;
		this.visibilityTimeout = visibilityTimeout;
		
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void consume() {
		
		int messagesConsumed = 0;
					
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
		receiveMessageRequest.setQueueUrl(queueUrl);
		receiveMessageRequest.setMaxNumberOfMessages(10);
		receiveMessageRequest.setVisibilityTimeout(visibilityTimeout);
		
		// Have to specify the names of the attributes to receive.
		// Can specify "All" to retrieve all attributes.
		receiveMessageRequest.setMessageAttributeNames(Arrays.asList("All"));
		
		ReceiveMessageResult receiveMessageResult = client.receiveMessage(receiveMessageRequest);
		
		List<Message> messages = receiveMessageResult.getMessages();
	
		LOGGER.info("Consumed {} messages from the queue.", messages.size());
		
		for(Message message : messages) {
			
			boolean processed = false;
			
			final String action = message.getMessageAttributes().get(QueueConstants.ACTION).getStringValue();
			
			if(StringUtils.equalsIgnoreCase(action, QueueConstants.ACTION_INGEST)) {
			
				// Ingest the entity.
				QueueIngestMessage queueIngestMessage = gson.fromJson(message.getBody(), QueueIngestMessage.class);						
				
				try {
				
					processed = ingestEntity(queueIngestMessage);					
				
				} catch (IOException ex) {
										
					LOGGER.error("The received ACL " + queueIngestMessage.getAcl() + " is malformed.", ex);
					
				}
			
			} else if(StringUtils.equalsIgnoreCase(action, QueueConstants.ACTION_UPDATE_ACL)) {
				
				// Update the entity's ACL.
				QueueUpdateAclMessage queuUpdateAclMessage = gson.fromJson(message.getBody(), QueueUpdateAclMessage.class);
				
				try {
									
					processed = updateEntityAcl(queuUpdateAclMessage);
					
				} catch (EntityStoreException ex) {
					
					LOGGER.error("Unable to update entity " + queuUpdateAclMessage.getEntityId() + " to have ACL " + queuUpdateAclMessage.getAcl() + ".", ex);
					
				}
				
			} else {
				
				LOGGER.warn("Consumed message had invalid action: {}", action);
				
			}
			
			if(processed) {
				
				LOGGER.debug("Message was consumed from SQS queue.");
				
				DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
				deleteMessageRequest.setReceiptHandle(message.getReceiptHandle());
				deleteMessageRequest.setQueueUrl(queueUrl);
				
				client.deleteMessage(deleteMessageRequest);
				
				messagesConsumed++;
				
				// The deleteMessageResult is not checked because if the delete fails
				// the message will remain on the queue. The next time that message is
				// received it will see that the entity already exists and it will
				// attempt to delete the message again. After so many attempts the
				// message will be moved to the dead letter queue (if configured).
			
			} else {
				
				LOGGER.debug("Message was not consumed from SQS queue.");
				
			}
			
		}
				
	}
	

	@Override
	public int getSize() {
		
		 Set<String> attributes = new HashSet<String>();
		 attributes.add("ApproximateNumberOfMessages");

		 GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest()
				 .withQueueUrl(queueUrl)
				 .withAttributeNames(attributes);
		 
		 Map<String, String> result = client.getQueueAttributes(getQueueAttributesRequest).getAttributes();
		 int size = Integer.parseInt(result.get("ApproximateNumberOfMessages"));
		 
		 return size;
		
	}
	
	/**
	 * Gets the client configuration for the AWS SQS client.
	 * @return An AWS {@link ClientConfiguration}.
	 */
	private ClientConfiguration getClientConfiguration() {
		
		ClientConfiguration clientConfiguration = new ClientConfiguration()
				.withConnectionTimeout(DEFAULT_CONNECT_TIMEOUT)
				.withSocketTimeout(DEFAULT_READ_TIMEOUT)
				.withMaxConnections(DEFAULT_MAX_CONNECTIONS)
				.withMaxErrorRetry(DEFAULT_MAX_RETRIES);
		
		return clientConfiguration;
		
	}

}