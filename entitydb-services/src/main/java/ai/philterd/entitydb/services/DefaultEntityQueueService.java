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
package ai.philterd.entitydb.services;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

import ai.philterd.entitydb.model.exceptions.QueryGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.security.Acl;
import ai.philterd.entitydb.model.services.EntityQueryService;
import ai.philterd.entitydb.model.services.EntityQueueService;

/**
 * Default implementation of {@link EntityQueueService}.
 * 
 * @author Philterd, LLC
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
		final Acl entityAcl = new Acl(acl);
		
		queuePublisher.queueIngest(entities, acl, apiKey);
		
		executor.execute(new Runnable() {
			
		    @Override
		    public void run() {

		    	executeContinuousQueries(entities, entityAcl);
		    	
		    }
		    
		});				
		
	}
	
	private void executeContinuousQueries(final Collection<Entity> entities, final Acl acl)  {
			
		long timestamp = System.currentTimeMillis();
		
		entityQueryService.executeContinuousQueries(entities, acl, timestamp);
		
	}

}