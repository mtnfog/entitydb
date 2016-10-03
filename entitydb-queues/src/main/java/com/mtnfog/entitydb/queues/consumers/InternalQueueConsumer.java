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
package com.mtnfog.entitydb.queues.consumers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.InternalQueue;
import com.mtnfog.entitydb.queues.messages.InternalQueueIngestMessage;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;

/**
 * The internal queue consumer consumes messages from an in-memory queue. This queue
 * should only be used for product evaluation and testing.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class InternalQueueConsumer extends AbstractQueueConsumer implements QueueConsumer {	

	private static final Logger LOGGER = LogManager.getLogger(InternalQueueConsumer.class);
	
	private int sleepSeconds;
	private boolean consume = true;
	
	/**
	 * Creates a new internal queue consumer.
	 * @param entityStore The {@link EntityStore}.
	 * @param rulesEngines A list of {@link RulesEngine rules engines}.
	 * @param searchIndex A {@link SearchIndex search index}.
	 */
	public InternalQueueConsumer(EntityStore<?> entityStore, List<RulesEngine> rulesEngines, 
			AuditLogger auditLogger, int sleepSeconds) {
		
		super(entityStore, rulesEngines, auditLogger);
		
		this.sleepSeconds = sleepSeconds;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void consume() {
		
		// TODO: Also consume from the ACL updates queue.
				
		while(InternalQueue.getIngestQueue().size() > 0 && consume == true) {
		
			InternalQueueIngestMessage message = InternalQueue.getIngestQueue().poll();	
		
			if(message != null) {
			
				try {
				
					ingestEntity(message.getEntity(), new Acl(message.getAcl()), message.getApiKey());
					
				} catch (Exception ex) {
					
					LOGGER.error("Unable to consume message from internal queue. The message has been lost.", ex);
					
				}
			
			}
			
			if(getSize() <= 0) {
				
				try {
					LOGGER.info("Queue processor thread {} is sleeping for {} seconds.", Thread.currentThread().getId(), sleepSeconds);
					Thread.sleep(sleepSeconds * 1000);
				} catch (InterruptedException e) {
					// Ignoring.
				}
			
			}
			
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		
		return InternalQueue.getIngestQueue().size();
		
	}
	
}