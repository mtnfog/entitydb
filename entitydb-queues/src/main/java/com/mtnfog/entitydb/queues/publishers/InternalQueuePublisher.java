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
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.InternalQueue;
import com.mtnfog.entitydb.queues.messages.InternalQueueIngestMessage;
import com.mtnfog.entitydb.queues.messages.InternalQueueUpdateAclMessage;

public class InternalQueuePublisher implements QueuePublisher {

	private static final Logger LOGGER = LogManager.getLogger(InternalQueuePublisher.class);
	
	@Override
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException {
		
		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		try {
				
			InternalQueueUpdateAclMessage message = new InternalQueueUpdateAclMessage(entityId, acl, apiKey);
			
			InternalQueue.getUpdateAclQueue().add(message);
		
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
		
		try {
		
			for(Entity entity : entities) {
			
				InternalQueueIngestMessage message = new InternalQueueIngestMessage(entity, acl, apiKey);
				
				InternalQueue.getIngestQueue().add(message);
			
			}
						
			LOGGER.info("Queued {} entities.", entities.size());
			LOGGER.info("Queue size: {}", InternalQueue.getIngestQueue().size());
		
		} catch (Exception ex) {
			
			LOGGER.error("Unable to queue entities.", ex);
			
			throw new EntityPublisherException("Unable to queue entities.", ex);
			
		}
		
	}

}