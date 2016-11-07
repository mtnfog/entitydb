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
package com.mtnfog.entitydb.queues.consumers;

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
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConstants;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueueIngestMessage;
import com.mtnfog.entitydb.model.queue.QueueUpdateAclMessage;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.search.IndexedEntity;

/**
 * A queue consumer that consumes from an AWS SQS queue.
 * 
 * @author Mountain Fog, Inc.
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
	
	private int sleepSeconds;
	private int visibilityTimeout;
	
	public SqsQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			AuditLogger auditLogger, EntityQueryService entityQueryService, 
			MetricReporter metricReporter, String endpoint, String queueUrl, int sleepSeconds, int visibilityTimeout,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		super(entityStore, rulesEngines,  auditLogger, entityQueryService, metricReporter, indexerCache);
		
		client = new AmazonSQSClient(getClientConfiguration());
		client.setEndpoint(endpoint);
		gson = new Gson();
		
		this.queueUrl = queueUrl;
		this.sleepSeconds = sleepSeconds;
		this.visibilityTimeout = visibilityTimeout;
		
	}
	
	public SqsQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines, 
			AuditLogger auditLogger, EntityQueryService entityQueryService, 
			MetricReporter metricReporter, String endpoint, String queueUrl, String accessKey, String secretKey, int sleepSeconds, int visibilityTimeout,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
			
		super(entityStore, rulesEngines, auditLogger, entityQueryService, metricReporter, indexerCache);
		
		client = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey), getClientConfiguration());
		client.setEndpoint(endpoint);
		
		gson = new Gson();
					
		this.queueUrl = queueUrl;
		this.sleepSeconds = sleepSeconds;
		this.visibilityTimeout = visibilityTimeout;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void consume() {
					
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
				
				} catch (MalformedAclException ex) {
										
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
				
				// The deleteMessageResult is not checked because if the delete fails
				// the message will remain on the queue. The next time that message is
				// received it will see that the entity already exists and it will
				// attempt to delete the message again.
			
			} else {
				
				LOGGER.debug("Message was not consumed from SQS queue.");
				
			}
			
		}
		
		if(getSize() <= 0) {
		
			try {
				LOGGER.info("Queue processor thread {} is sleeping for {} seconds.", Thread.currentThread().getId(), sleepSeconds);
				Thread.sleep(sleepSeconds * 1000);
			} catch (InterruptedException e) {
				// Ignoring.
			}
		
		}
				
	}
	
	/**
	 * {@inheritDoc}
	 */
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