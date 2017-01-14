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
package com.mtnfog.entitydb.model.queue;

/**
 * A message that describes an update to an existing entity's
 * ACL that is published to the internal ACl update queue.
 * Note that not all queue implementations may use this class.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class QueueUpdateAclMessage extends QueueMessage {

	private String entityId;
	private String acl;
	private String apiKey;
	
	/**
	 * Creates a new message.
	 * @param entityId The ID of the entity to update.
	 * @param acl The new ACL for the entity.
	 * @param apiKey The API key of the client ingesting the entity.
	 */
	public QueueUpdateAclMessage(String entityId, String acl, String apiKey) {
		
		this.entityId = entityId;
		this.acl = acl;
		this.apiKey = apiKey;
		
	}

	/**
	 * Gets the ID of the entity.
	 * @return The ID of the entity.
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * Gets the new ACL for the entity.
	 * @return The new ACL for the entity.
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