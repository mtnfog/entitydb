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
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.QueueConstants;
import com.mtnfog.entitydb.queues.messages.QueueIngestMessage;
import com.mtnfog.entitydb.queues.messages.QueueUpdateAclMessage;

/**
 * Implementation of {@link QueuePublisher} that publishes messages to an ActiveMQ queue.
 * 
 * @author Mountain Fog, Inc.
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