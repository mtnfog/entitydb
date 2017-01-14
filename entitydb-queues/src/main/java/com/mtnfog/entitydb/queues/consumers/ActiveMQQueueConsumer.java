/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConstants;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueueIngestMessage;
import com.mtnfog.entitydb.model.queue.QueueUpdateAclMessage;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;

/**
 * Implementation of {@link QueueConsumer} that uses an ActiveMQ queue.
 * 
 * @author Mountain Fog, Inc.
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
	 * @param idylCache The {@link IdylCache cache}.
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
	 * @param idylCache The {@link IdylCache cache}.
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