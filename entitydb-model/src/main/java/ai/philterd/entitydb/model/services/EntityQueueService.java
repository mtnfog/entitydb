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

import java.util.Collection;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;

/**
 * Interface for entity queue services.
 * 
 * @author Philterd, LLC
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