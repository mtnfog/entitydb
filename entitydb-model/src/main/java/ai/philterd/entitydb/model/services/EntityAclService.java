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
package ai.philterd.entitydb.model.services;

import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;

/**
 * Interface for an entity ACL service.
 * 
 * @author Philterd, LLC
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