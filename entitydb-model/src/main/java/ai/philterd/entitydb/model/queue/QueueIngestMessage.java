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

import ai.philterd.entitydb.model.entity.Entity;

/**
 * A message that describes an entity for ingest that is
 * placed on an ingest queue. Note that not all queue implementations
 * may use this class.
 * 
 * @author Philterd, LLC
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