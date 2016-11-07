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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueIngestMessage;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.queue.QueueUpdateAclMessage;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.InternalQueue;

/**
 * Implementation of {@link QueuePublisher} that publishes messages to an internal queue
 * held in memory.
 * 
 * @author Mountain Fog, Inc.
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
	
	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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