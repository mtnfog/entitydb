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
package com.mtnfog.entitydb.queues.publishers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.QueueConstants;

/**
 * Implementation of {@link QueuePublisher} that publishes messages to an AWS SQS queue.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class SqsQueuePublisher implements QueuePublisher {

	private static final Logger LOGGER = LogManager.getLogger(SqsQueuePublisher.class);
	
	private AmazonSQSClient client;
	private Gson gson;
	private MetricReporter metricReporter;

	private String queueUrl;
	
	public SqsQueuePublisher(String queueUrl, String endpoint, MetricReporter metricReporter) {

		this.queueUrl = queueUrl;
		this.metricReporter = metricReporter;
		
		client = new AmazonSQSClient();
		gson = new Gson();

	}
	
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
			attributes.put(QueueConstants.ACTION, new MessageAttributeValue().withDataType("String").withStringValue(QueueConstants.ACTION_UPDATE_ACL));
			attributes.put(QueueConstants.ACL, new MessageAttributeValue().withDataType("String").withStringValue(acl));
			attributes.put(QueueConstants.API_KEY, new MessageAttributeValue().withDataType("String").withStringValue(apiKey));
			
			request.setQueueUrl(queueUrl);
			request.setMessageBody(entityId);
			request.setMessageAttributes(attributes);
	
			client.sendMessage(request);
		
		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entity ACL change.", ex);
			
			throw new EntityPublisherException("Unable to queue entity ACL change.", ex);
			
		}
		
	}

	@Override
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {

		// One at a time or in a batch? It may be better to do one at a time.
		/* Delivers up to ten messages to the specified queue. This is a batch version of SendMessage. 
		 * The result of the send action on each message is reported individually in the response. 
		 * The maximum allowed individual message size is 256 KB (262,144 bytes).
		 * The maximum total payload size (i.e., the sum of all a batch's individual message lengths) is also 256 KB (262,144 bytes).
		 */	

		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		long startTime = System.currentTimeMillis();
		
		try {
			
			List<SendMessageBatchRequestEntry> entries = new LinkedList<SendMessageBatchRequestEntry>();
		
			for (Entity entity : entities) {
	
				Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
				attributes.put(QueueConstants.ACTION, new MessageAttributeValue().withDataType("String").withStringValue(QueueConstants.ACTION_INGEST));				
				attributes.put(QueueConstants.ACL, new MessageAttributeValue().withDataType("String").withStringValue(acl));
				attributes.put(QueueConstants.API_KEY, new MessageAttributeValue().withDataType("String").withStringValue(apiKey));
				
				SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry();
				
				entry.setMessageBody(gson.toJson(entity));
				entry.setMessageAttributes(attributes);
	
				entries.add(entry);
	
			}
			
			client.sendMessageBatch(queueUrl, entries);
			
			metricReporter.reportElapsedTime("QueueIngest", "time", startTime);

		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entities for ingest.", ex);
			
			throw new EntityPublisherException("Unable to queue entities for ingest.", ex);
			
		}
			
	}

}