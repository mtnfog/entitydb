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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import ai.philterd.entitydb.model.search.SearchIndex;

/**
 * Implementation of {@link QueueConsumer} that uses an ActiveMQ queue.
 * 
 * @author Philterd, LLC
 *
 */
public class ActiveMQQueueConsumer extends AbstractQueueConsumer implements QueueConsumer {
	
	private static final Logger LOGGER = LogManager.getLogger(ActiveMQQueueConsumer.class);

	private static final int DEFAULT_TIMEOUT = 100;
	
	private Gson gson;
	private Connection connection;
	private Session session;
	private Destination destination;	
	private int timeout;
	
	private MetricReporter metricReporter;
	private boolean consume = true;
	
	/**
	 * Creates a new ActiveMQ consumer.
	 * @param entityStore The {@link EntityStore}.
	 * @param rulesEngines The list of {@link RulesEngine rules engines}.
	 * @param searchIndex The {@link SearchIndex}.
	 * @param brokerURL The ActiveMQ broker URL.
	 * @param queueName The name of the queue.
	 * @param timeout The timeout in milliseconds. See the JMX {@link MessageConsumer} documentation
	 * at https://docs.oracle.com/javaee/7/api/javax/jms/MessageConsumer.html#receive-long- for a full description.
	 * @throws JMSException Thrown if the ActiveMQ consumer cannot be created.
	 */
	public ActiveMQQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			AuditLogger auditLogger, MetricReporter metricReporter,
			String brokerURL, String queueName, int timeout, ConcurrentLinkedQueue<IndexedEntity> indexerCache) throws JMSException {
		
		super(entityStore, rulesEngines, auditLogger, metricReporter, indexerCache);

		gson = new Gson();
		
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        
        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();
        
        // Create a Session
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        
        // Create the destination (Topic or Queue)
        destination = session.createQueue(queueName);
        
        this.metricReporter = metricReporter;
        this.timeout = timeout;
        
	}
	
	/**
	 * Creates a new ActiveMQ consumer with the default timeout value of 100.
	 * @param entityStore The {@link EntityStore}.
	 * @param rulesEngines The list of {@link RulesEngine rules engines}.
	 * @param searchIndex The {@link SearchIndex}.
	 * @param brokerURL The ActiveMQ broker URL.
	 * @param queueName The name of the queue.	
	 * @throws JMSException Thrown if the ActiveMQ consumer cannot be created.
	 */
	public ActiveMQQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines,
			AuditLogger auditLogger, MetricReporter metricReporter, String brokerURL, String queueName,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) throws JMSException {
	
		this(entityStore, rulesEngines, auditLogger, metricReporter, brokerURL, queueName, DEFAULT_TIMEOUT, indexerCache);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {;
		
		consume = false;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void consume() {
		
		int messagesConsumed = 0;
		
		try {
		
	        MessageConsumer consumer = session.createConsumer(destination);
			
	        while(consume == true) {
	        
				// Wait for a message
		        Message message = consumer.receive(timeout);
		
		        if (message instanceof TextMessage) {
		        	
		        	boolean successful = false;
		        	
		            TextMessage textMessage = (TextMessage) message;		            		            
		            
		            if(StringUtils.equalsIgnoreCase(message.getStringProperty(QueueConstants.ACTION), QueueConstants.ACTION_INGEST)) {
		            
		            	QueueIngestMessage internalQueueIngestMessage = gson.fromJson(textMessage.getText(), QueueIngestMessage.class);
		            
		            	try {
		            	
		            		// Ingest the entity.
		            		successful = ingestEntity(internalQueueIngestMessage);
								
		            		if(successful) {
								
								metricReporter.reportElapsedTime("queue", "EntityIngestQueueMessageTimeToProcess", internalQueueIngestMessage.getTimestamp());
								
							}
		            		
						} catch (MalformedAclException ex) {
							
							LOGGER.error("Unable to ingest entity " + internalQueueIngestMessage.getEntity().toString() + ".", ex);
							
						}
			            		            
		            } else if(StringUtils.equalsIgnoreCase(message.getStringProperty(QueueConstants.ACTION), QueueConstants.ACTION_UPDATE_ACL)) {		            	
		        				        		            
		            	QueueUpdateAclMessage internalQueueUpdateAclMessage = gson.fromJson(textMessage.getText(), QueueUpdateAclMessage.class);
		            	
		            	try {
		            	
		            		// Update the entity's ACL.
		            		successful = updateEntityAcl(internalQueueUpdateAclMessage);
		            		
		            		if(successful) {
								
								metricReporter.reportElapsedTime("queue", "EntityAclQueueMessageTimeToProcess", internalQueueUpdateAclMessage.getTimestamp());
								
							}
		            		
						} catch (EntityStoreException ex) {
							
							LOGGER.error("Unable to update entity " + internalQueueUpdateAclMessage.getEntityId() + " to have ACL " + internalQueueUpdateAclMessage.getAcl() + ".", ex);
							
						}
		            	
		            } else {
		            	
		            	LOGGER.warn("Message on ActiveMQ queue has invalid type: {}", message.getStringProperty("type"));
		            	
		            }
		            
		            if(successful) {
			            
		            	message.acknowledge();
		            	
		            	messagesConsumed++;
		            	
		            } else {
		            	
		            	LOGGER.warn("Unable to process message from ActiveMQ queue.");
		            	
		            }
		            
		        }
				
		        // No easy way to get the size of an AciveMQ queue.
		        
		        consumer.close();
		        
	        }
        
		} catch (JMSException ex) {
			
			try {
			
				session.recover();
			
			} catch (JMSException e) {

				LOGGER.error("Unable to recover ActiveMQ session.", ex);
				
			}
		
			LOGGER.error("Unable to consume message from ActiveMQ queue.", ex);
			
		}
		
		//return messagesConsumed;
        
	}

	/**
	 * {@inheritDoc}
	 * There's no straightforward way to get the size of the queue.
	 * See: http://activemq.apache.org/how-do-i-find-the-size-of-a-queue.html
	 * Because of this, <code>getSize()</code> always returns a value of 1.
	 */
	@Override	
	public int getSize() {
		return 1;
	}
	
	@PreDestroy
	public void close() throws JMSException {
		
		LOGGER.info("Shutting down the ActiveMQ queue consumer.");
		
        session.close();
        connection.close();
        
	}
	
}