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
package ai.philterd.entitydb.queues.publishers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConstants;
import ai.philterd.entitydb.model.queue.QueueIngestMessage;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.queue.QueueUpdateAclMessage;
import ai.philterd.entitydb.model.security.Acl;

/**
 * Implementation of {@link QueuePublisher} that publishes messages to an AWS SQS queue.
 * 
 * @author Philterd, LLC
 *
 */
public class SqsQueuePublisher implements QueuePublisher {

	private static final Logger LOGGER = LogManager.getLogger(SqsQueuePublisher.class);
	
	private static final String STRING = "String";
	
	private AmazonSQSClient client;
	private Gson gson;
	private MetricReporter metricReporter;

	private String queueUrl;
	
	/**
	 * Creates a new SQS queue publisher. Credentials are obtained through the credential
	 * provider chain. Credentials need IAM permissions
	 * that allow publishing messages to an SQS queue.
	 * @param queueUrl The URL of the SQS queue.
	 * @param endpoint The AWS SQS endpoint.
	 * @param metricReporter A {@link MetricReporter}.
	 */
	public SqsQueuePublisher(String queueUrl, String endpoint, MetricReporter metricReporter) {

		this.queueUrl = queueUrl;
		this.metricReporter = metricReporter;
		
		client = new AmazonSQSClient();
		client.setEndpoint(endpoint);
		
		gson = new Gson();

	}
	
	/**
	 * Creates a new SQS queue publisher. The access/secret key combination need IAM permissions
	 * that allow publishing messages to an SQS queue.
	 * @param queueUrl The URL of the SQS queue.
	 * @param endpoint The AWS SQS endpoint.
	 * @param accessKey The access key.
	 * @param secretKey The secret key.
	 * @param metricReporter A {@link MetricReporter}.
	 */
	public SqsQueuePublisher(String queueUrl, String endpoint, String accessKey, String secretKey, MetricReporter metricReporter) {

		this.queueUrl = queueUrl;
		this.metricReporter = metricReporter;
		
		client = new AmazonSQSClient(new BasicAWSCredentials(accessKey, secretKey));
		client.setEndpoint(endpoint);
		
		gson = new Gson();

	}
	

	@Override
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {
		
		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		try {
				
			SendMessageRequest request = new SendMessageRequest();
			
			Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
			attributes.put(QueueConstants.ACTION, new MessageAttributeValue().withDataType(STRING).withStringValue(QueueConstants.ACTION_UPDATE_ACL));
			
			QueueUpdateAclMessage queueUpdateAclMessage = new QueueUpdateAclMessage(entityId, acl, apiKey);
			
			request.setQueueUrl(queueUrl);
			request.setMessageBody(gson.toJson(queueUpdateAclMessage));
			request.setMessageAttributes(attributes);
	
			client.sendMessage(request);
		
		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entity ACL change.", ex);
			
			throw new EntityPublisherException("Unable to queue entity ACL change.", ex);
			
		}
		
	}


	@Override
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {

		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		long startTime = System.currentTimeMillis();
		
		try {
			
			List<SendMessageBatchRequestEntry> entries = new LinkedList<SendMessageBatchRequestEntry>();
		
			for (Entity entity : entities) {
	
				Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
				attributes.put(QueueConstants.ACTION, new MessageAttributeValue().withDataType(STRING).withStringValue(QueueConstants.ACTION_INGEST));				
				
				QueueIngestMessage quueIngestMessage = new QueueIngestMessage(entity, acl, apiKey);				
				
				SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry();
				
				entry.setMessageBody(gson.toJson(quueIngestMessage));
				entry.setMessageAttributes(attributes);				
				entry.setId(UUID.randomUUID().toString());
				
				// TODO: Check the size of `entry` to make sure it is less than 256 KB.
				// TODO: Make sure the batch will still be less than 256 KB after adding `entry`.
				
				entries.add(entry);
				
				if(entries.size() == 10) {
					
					// TODO: Check the result of sendMessageBatch() for failed messages.
					client.sendMessageBatch(queueUrl, entries);
					entries.clear();
					
				}
				
			}
			
			if(CollectionUtils.isNotEmpty(entries)) {
				client.sendMessageBatch(queueUrl, entries);
			}
			
			metricReporter.reportElapsedTime("QueueIngest", "time", startTime);

		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entities for ingest.", ex);
			
			throw new EntityPublisherException("Unable to queue entities for ingest.", ex);
			
		}
			
	}

}