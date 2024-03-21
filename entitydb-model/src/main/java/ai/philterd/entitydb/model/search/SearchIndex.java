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
package ai.philterd.entitydb.model.search;

import java.util.List;
import java.util.Set;

import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Interface to a search index.
 * 
 * @author Philterd, LLC
 *
 */
public interface SearchIndex {

	/**
	 * Gets the ID of the last indexed document (entity).
	 * @return The ID of the last indexed document (entity).
	 */
	public long getLastIndexedId();
	
	/**
	 * Insert the entity into the search index.
	 * @param indexedEntity The {@link IndexedEntity entity} to index.
	 * @return <code>true</code> if the entity was indexed; otherwise <code>false</code>.
	 */
	public boolean index(IndexedEntity indexedEntity);
	
	/**
	 * Insert the entities into the search index.
	 * @param indexedEntity A set of {@link IndexedEntity entities} to index.
	 * @return A set of entity IDs for entities that were not indexed.
	 */
	public Set<String> index(Set<IndexedEntity> indexedEntities);
	
	/**
	 * Gets a count of indexed entities.
	 * @return Count of indexed entities, or <code>-1</code> if an error occurred
	 * while trying to get the count of indexed entities.
	 */
	public long getCount();
	
	/**
	 * Gets the status of the search index.
	 * @return The status of the search index.
	 */
	public String getStatus();
	
	/**
	 * Execute a query against the index.
	 * @param entityQuery The {@link EntityQuery query} to execute.
	 * @param user The {@link UserEntity user} executing the query.
	 * @return A list of entity IDs for the matching entities.
	 * @throws InvalidQueryException Thrown if the query is invalid.
	 */
	public List<String> queryForEntityIds(EntityQuery entityQuery, User user) throws InvalidQueryException;
	
	/**
	 * Execute a query against the index.
	 * @param entityQuery The {@link EntityQuery query} to execute.
	 * @param user The {@link UserEntity user} executing the query.
	 * @return A list of {@link IndexedEntity entities}.
	 * @throws InvalidQueryException Thrown if the query is invalid.
	 */
	public List<IndexedEntity> queryForIndexedEntities(EntityQuery entityQuery, User user) throws InvalidQueryException;
	
	/**
	 * Gets a single entity from the index.
	 * @param entityId The entity's ID.
	 * @return An {@link IndexedEntity entity}, or <code>null</code> if no
	 * matching entity was found.
	 */
	public IndexedEntity getEntity(String entityId);
	
	/**
	 * Update an indexed entity.
	 * @param indexedEntity The {@link IndexedEntity} to update.
	 * @return <code>true</code> if the update was successful; otherwise <code>false</code>.
	 */
	public boolean update(IndexedEntity indexedEntity);
	
	/**
	 * Delete an entity from the index.
	 * @param entityId The entity ID.
	 * @return <code>true</code> if the entity was sucessfully deleted; otherwise <code>false</code>.
	 */
	public boolean delete(String entityId);
	
	/**
	 * Closes and releases resources.
	 */
	public void close();
	
}