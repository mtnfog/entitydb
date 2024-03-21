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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.model.security.Acl;
import ai.philterd.entitydb.model.services.EntityAclService;

/**
 * Default implementation of {@link EntityAclService}.
 *  
 * @author Philterd, LLC
 *
 */
@Component
public class DefaultEntityAclService implements EntityAclService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultEntityAclService.class);
			
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private QueuePublisher queuePublisher;
	
	@Autowired
	private MetricReporter metricReporter;
	

	@Override
	public void queueEntityAclUpdate(String entityId, String acl, String apiKey) throws MalformedAclException, NonexistantEntityException, EntityPublisherException {
		
		metricReporter.report(MetricReporter.MEASUREMENT_API, "entityAclUpdate", 1, Unit.COUNT);
		
		// The entity needs to exist (in the search index). 
		// The request to change the ACL will be put onto the queue.				
		
		IndexedEntity indexedEntity = searchIndex.getEntity(entityId);
		
		if(indexedEntity == null) {
			
			throw new NonexistantEntityException("Entity with ID " + entityId + " was not found.");
			
		} else {
		
			// Validate the ACL.
			if(!Acl.validate(acl)) {
				throw new MalformedAclException("The acl is malformed.");
			}
			
			LOGGER.trace("Queueing the entity ACL change request.");
			
			queuePublisher.queueUpdateAcl(entityId, acl, apiKey);
		
		}
		
	}
	
}