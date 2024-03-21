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

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * Implementation of {@link QueuePublisher} that publishes messages to an ActiveMQ queue.
 * 
 * @author Philterd, LLC
 *
 */
public class ActiveMQQueuePublisher implements QueuePublisher {
	
	private static final Logger LOGGER = LogManager.getLogger(ActiveMQQueuePublisher.class);
	
	private static final String ACL_QUEUE_NAME = "update-acl";
	private static final String INGEST_QUEUE_NAME = "ingest";
	
	private Gson gson;
	private Connection connection;
	private Session session;
		
	private MetricReporter metricReporter;
	
	/**
	 * Creates a new ActiveMQ publisher.
	 * @param brokerURL The broker URL.
	 * @param queueName The name of the queue.
	 * @throws JMSException Thrown if the queue publisher cannot be created.
	 */
	public ActiveMQQueuePublisher(String brokerURL, String queueName, MetricReporter metricReporter) throws JMSException {
		
		this.metricReporter = metricReporter;
		gson = new Gson();
		
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        
        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();
        
        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                
	}	
	

	@Override
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {
		
		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		long startTime = System.currentTimeMillis();
				
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(ACL_QUEUE_NAME);
			
	        // Create a MessageProducer from the Session to the Topic or Queue
	        MessageProducer producer = session.createProducer(destination);
	        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			        
	        // Create a message.
        	QueueUpdateAclMessage internalQueueMessage = new QueueUpdateAclMessage(entityId, acl, apiKey);	     
	        TextMessage message = session.createTextMessage(gson.toJson(internalQueueMessage));	
	        message.setStringProperty(QueueConstants.ACTION, QueueConstants.ACTION_UPDATE_ACL);
	        
	        producer.send(message);
	        
	        metricReporter.reportElapsedTime("QueueIngest", "time", startTime);
	                
		} catch (Exception ex) {
			
			throw new EntityPublisherException("Unable to queue entities.", ex);
			
		}
		
	}
	

	@Override
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {

		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
				
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(INGEST_QUEUE_NAME);
			
	        // Create a MessageProducer from the Session to the Topic or Queue
	        MessageProducer producer = session.createProducer(destination);
	        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	
	        for(Entity entity : entities) {
		        
		        // Create a message.
		        QueueIngestMessage internalQueueIngestMessage = new QueueIngestMessage(entity, acl, apiKey);	     
		        TextMessage message = session.createTextMessage(gson.toJson(internalQueueIngestMessage));	
		        message.setStringProperty(QueueConstants.ACTION, QueueConstants.ACTION_INGEST);
		        
		        // Put the message onto the queue.
		        producer.send(message);
	        
	        }
        
		} catch (Exception ex) {
			
			throw new EntityPublisherException("Unable to queue entities.", ex);
			
		}
		
	}
	
	@PreDestroy
	public void close() throws JMSException {
		
		LOGGER.info("Shutting down the ActiveMQ queue publisher.");
		
        session.close();
        connection.close();
        
	}

}