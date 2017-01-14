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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.model.services.EntityAclService;

/**
 * Default implementation of {@link EntityAclService}.
 *  
 * @author Mountain Fog, Inc.
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
	
	/**
	 * {@inheritDoc}
	 */
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