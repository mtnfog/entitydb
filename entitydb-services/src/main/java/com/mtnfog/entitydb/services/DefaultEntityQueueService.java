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
package com.mtnfog.entitydb.services;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.model.services.EntityQueueService;

/**
 * Default implementation of {@link EntityQueueService}.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Component
public class DefaultEntityQueueService implements EntityQueueService {

	@Autowired
	private QueuePublisher queuePublisher;
	
	@Autowired
	private EntityQueryService entityQueryService;
	
	@Autowired
	private ThreadPoolExecutor executor;
	
	/**
	 * {@inheritDoc}
	 * 
	 * In addition to queuing the entities this function also executes
	 * the continuous queries against the entities in a separate thread.
	 */
	@Override
	public void queueIngest(final Collection<Entity> entities, final String acl, final String apiKey) throws MalformedAclException, EntityPublisherException {
		
		// Validate the ACL.
		Acl entityAcl = new Acl(acl);
		
		queuePublisher.queueIngest(entities, acl, apiKey);
		
		executor.execute(new Runnable() {
			
		    @Override
		    public void run() {
		    	
		    	executeContinuousQueries(entities, entityAcl);
		    	
		    }
		    
		});				
		
	}
	
	private void executeContinuousQueries(final Collection<Entity> entities, final Acl acl) {
			
		long timestamp = System.currentTimeMillis();
		
		entityQueryService.executeContinuousQueries(entities, acl, timestamp);
		
	}

}