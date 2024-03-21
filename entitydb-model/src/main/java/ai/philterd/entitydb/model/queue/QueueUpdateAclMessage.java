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
package ai.philterd.entitydb.model.queue;

/**
 * A message that describes an update to an existing entity's
 * ACL that is published to the internal ACl update queue.
 * Note that not all queue implementations may use this class.
 * 
 * @author Philterd, LLC
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