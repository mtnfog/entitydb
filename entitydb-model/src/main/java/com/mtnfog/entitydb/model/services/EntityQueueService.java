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

import java.util.Collection;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;

/**
 * Interface for entity queue services.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface EntityQueueService {

	/**
	 * Queues entities for ingest.
	 * @param entities A collection of {@link Entity entities}.
	 * @param acl The ACL for the entities.
	 * @param apiKey The client's API key.
	 * @throws MalformedAclException Thrown if the ACL is malformed.
	 * @throws EntityPublisherException Thrown if the entities cannot be queued.
	 */
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException;
	
}