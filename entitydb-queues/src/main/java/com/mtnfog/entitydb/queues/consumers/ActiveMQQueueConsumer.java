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
package com.mtnfog.entitydb.queues.consumers;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.messages.InternalQueueIngestMessage;
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
			AuditLogger auditLogger, String brokerURL,
			String queueName, int timeout) throws JMSException {
		
		super(entityStore, rulesEngines, auditLogger);

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
			AuditLogger auditLogger, 
			String brokerURL, String queueName) throws JMSException {
	
		this(entityStore, rulesEngines, auditLogger, brokerURL, queueName, DEFAULT_TIMEOUT);
		
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
		
		// TODO: Also consume from the ACL updates queue.
		
		try {
		
			// Create a MessageConsumer from the Session to the Topic or Queue
	        MessageConsumer consumer = session.createConsumer(destination);
			
	        while(consume == true) {
	        
				// Wait for a message
		        Message message = consumer.receive(timeout);
		
		        if (message instanceof TextMessage) {
		        	
		            TextMessage textMessage = (TextMessage) message;
		            InternalQueueIngestMessage internalQueueIngestMessage = gson.fromJson(textMessage.getText(), InternalQueueIngestMessage.class);
		            
		            // Ingest the entity.
		            boolean result = ingestEntity(internalQueueIngestMessage.getEntity(), new Acl(internalQueueIngestMessage.getAcl()), internalQueueIngestMessage.getApiKey());
		            
		            if(result) {
		            
		            	message.acknowledge();
		            	
		            }
		            
		        }
				
		        // No easy way to get the size of an AciveMQ queue.
		        
		        consumer.close();
		        
	        }
        
		} catch (Exception ex) {
			
			try {
			
				session.recover();
			
			} catch (JMSException e) {

				LOGGER.error("Unable to recover ActiveMQ session.", ex);
				
			}
		
			LOGGER.error("Unable to consume message from ActiveMQ queue.", ex);
			
		}
        
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