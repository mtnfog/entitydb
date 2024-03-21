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

import java.util.Collection;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;

/**
 * Interface for queue publishers. The implementing classes publish
 * entities and their associated information to a queue for processing.
 * 
 * @author Philterd, LLC
 *
 */
public interface QueuePublisher {

	/**
	 * Publish entities to the ingest queue.
	 * @param entities A collection of {@link Entity entities}.
	 * @param acl The entity's ACL.
	 * @param apiKey The API key of the requester.
	 * @throws MalformedAclException Thrown if the ACL does not meet the ACL regular expression.
	 * @throws EntityPublisherException Thrown if the request cannot be queued.
	 */
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException;
	
	/**
	 * Publish requests for entity ACL updates to the queue.
	 * @param entityId The entity's ID.
	 * @param acl The new ACL.
	 * @param apiKey The API key of the requester.
	 * @throws MalformedAclException Thrown if the ACL does not meet the ACL regular expression.
	 * @throws EntityPublisherException Thrown if the request cannot be queued.
	 */
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException;
	
}