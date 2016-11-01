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
package com.mtnfog.entitydb.model.services;

import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;

/**
 * Interface for an entity ACL service.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface EntityAclService {

	/**
	 * Queues an update to an entity's ACL.
	 * @param entityId The ID of the entity.
	 * @param acl The new ACL for the entity.
	 * @param apiKey The requesting user's API key.
	 * @throws MalformedAclException Thrown if the new ACL is invalid.
	 * @throws NonexistantEntityException Thrown if the entity does not exist.
	 * @throws EntityPublisherException Thrown if the request to update the entity's ACL cannot be published to the queue.
	 */
	public void queueEntityAclUpdate(String entityId, String acl, String apiKey) throws MalformedAclException, NonexistantEntityException, EntityPublisherException;
	
}