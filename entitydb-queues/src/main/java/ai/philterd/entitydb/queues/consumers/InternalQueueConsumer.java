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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.queue.QueueIngestMessage;
import ai.philterd.entitydb.model.queue.QueueMessage;
import ai.philterd.entitydb.model.queue.QueueUpdateAclMessage;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.queues.InternalQueue;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;

/**
 * The internal queue consumer consumes messages from an in-memory queue. This queue
 * should only be used for product evaluation and testing.
 * 
 * @author Philterd, LLC
 *
 */
public class InternalQueueConsumer extends AbstractQueueConsumer implements QueueConsumer {	

	private static final Logger LOGGER = LogManager.getLogger(InternalQueueConsumer.class);
	
	private MetricReporter metricReporter;
	
	private boolean consume = true;
	
	/**
	 * Creates a new internal queue consumer.
	 * @param entityStore The {@link EntityStore}.
	 * @param rulesEngines A list of {@link RulesEngine rules engines}.
	 * @param searchIndex A {@link SearchIndex search index}.
	 */
	public InternalQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines, 
			AuditLogger auditLogger, MetricReporter metricReporter,
			ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		super(entityStore, rulesEngines, auditLogger, metricReporter, indexerCache);
		
		this.metricReporter = metricReporter;
		
	}
	

	@Override
	public void shutdown() {
		
		consume = false;
		
	}
	

	@Override
	public void consume() {
		
		int messagesConsumed = 0;
		
		while(InternalQueue.getQueue().size() > 0 && consume == true) {
		
			QueueMessage message = InternalQueue.getQueue().poll();	
		
			if(message != null) {
			
				try {
					
					if(message instanceof QueueIngestMessage) {
				
						QueueIngestMessage internalQueueIngestMessage = (QueueIngestMessage) message;
						
						boolean successful = ingestEntity(internalQueueIngestMessage);
						
						if(successful) {
							
							metricReporter.reportElapsedTime("queue", "EntityIngestQueueMessageTimeToProcess", internalQueueIngestMessage.getTimestamp());
							
						}
					
					} else if(message instanceof QueueUpdateAclMessage) {
						
						QueueUpdateAclMessage internalQueueUpdateAclMessage = (QueueUpdateAclMessage) message;
						
						boolean successful = updateEntityAcl(internalQueueUpdateAclMessage);
						
						if(successful) {
							
							metricReporter.reportElapsedTime("queue", "EntityAclQueueMessageTimeToProcess", internalQueueUpdateAclMessage.getTimestamp());
							
						}
						
					}
					
					messagesConsumed++;
					
				} catch (Exception ex) {
					
					LOGGER.error("Unable to consume message from internal queue. The message has been lost.", ex);
					
				}
			
			}
			
		}
			
	}
	

	@Override
	public int getSize() {
		
		return InternalQueue.getQueue().size();
		
	}
	
}