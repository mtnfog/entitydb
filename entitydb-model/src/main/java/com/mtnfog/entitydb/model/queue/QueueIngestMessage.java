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
package com.mtnfog.entitydb.model.queue;

import com.mtnfog.entity.Entity;

/**
 * A message that describes an entity for ingest that is
 * placed on an ingest queue. Note that not all queue implementations
 * may use this class.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class QueueIngestMessage extends QueueMessage {

	private Entity entity;
	private String acl;
	private String apiKey;
	
	/**
	 * Creates a new message.
	 * @param entity The {@link Entity} to be ingested.
	 * @param acl The ACL for the entity.
	 * @param apiKey The API key of the client ingesting the entity.
	 */
	public QueueIngestMessage(Entity entity, String acl, String apiKey) {
		
		this.entity = entity;
		this.acl = acl;
		this.apiKey = apiKey;
		
	}

	/**
	 * Gets the {@link Entity entity}.
	 * @return The {@link Entity entity}.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Gets the entity's ACL.
	 * @return The entity's ACL.
	 */
	public String getAcl() {
		return acl;
	}
	
	/**
	 * Gets the client's API key.
	 * @return The client's API key.
	 */
	public String getApiKey() {
		return apiKey;
	}

}