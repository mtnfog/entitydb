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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueIngestMessage;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.queue.QueueUpdateAclMessage;
import ai.philterd.entitydb.model.security.Acl;
import ai.philterd.entitydb.queues.InternalQueue;

/**
 * Implementation of {@link QueuePublisher} that publishes messages to an internal queue
 * held in memory.
 * 
 * @author Philterd, LLC
 *
 */
public class InternalQueuePublisher implements QueuePublisher {

	private static final Logger LOGGER = LogManager.getLogger(InternalQueuePublisher.class);
	
	private MetricReporter metricReporter;
	
	/**
	 * Creates a new internal queue publisher. Messages placed onto the internal queue
	 * are held in memory and any messages on the queue will be lost should the EntityDB process exit.
	 * @param metricReporter A {@link MetricReporter}.
	 */
	public InternalQueuePublisher(MetricReporter metricReporter) {
		
		this.metricReporter = metricReporter;
		
	}
	

	@Override
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {
		
		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		long startTime = System.currentTimeMillis();
		
		try {
				
			QueueUpdateAclMessage message = new QueueUpdateAclMessage(entityId, acl, apiKey);
			
			InternalQueue.getQueue().add(message);
			
			metricReporter.reportElapsedTime("QueueAcl", "time", startTime);
		
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
		
			for(Entity entity : entities) {
			
				QueueIngestMessage message = new QueueIngestMessage(entity, acl, apiKey);
				
				InternalQueue.getQueue().add(message);
			
			}
						
			LOGGER.debug("Queued {} entities.", entities.size());
			LOGGER.debug("Queue size: {}", InternalQueue.getQueue().size());
			
			metricReporter.reportElapsedTime("QueueIngest", "time", startTime);
		
		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entities.", ex);
			
			throw new EntityPublisherException("Unable to queue entities.", ex);
			
		}
		
	}

}