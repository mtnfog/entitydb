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
package ai.philterd.entitydb.model.entitystore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.security.Acl;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Interface for entity stores.
 * 
 * @author Philterd, LLC
 *
 */
public interface EntityStore<T extends AbstractStoredEntity> {
	
	/**
	 * Gets the status of the entity store.
	 * @return The status of the entity store.
	 */
	String getStatus();
	
	/**
	 * Gets a list of visible entities that have not been indexed.
	 * @param The maximum number of entities to retrieve.
	 * @return A list of visible entities that have not been indexed.
	 */
	List<T> getNonIndexedEntities(int limit);
	
	/**
	 * Sets the entity's indexed property to the current time.
	 * @param entityId The entity ID.
	 * @return <code>true</code> if the operation is successful; otherwise <code>false</code>.
	 */
	boolean markEntityAsIndexed(String entityId);
	
	/**
	 * Sets the entitys' indexed property to the current time.
	 * @param entityIds A list of entity IDs.
	 * @return The number of updated entities.
	 */
	long markEntitiesAsIndexed(Collection<String> entityIds);
	
	/**
	 * Store the entity in the store.
	 * @param entity The {@link Entity} to store.
	 * @param acl The entity's ACL.
	 * @return The stored entity's ID.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	String storeEntity(Entity entity, String acl) throws EntityStoreException;
	
	/**
	 * Store the entities in the store.
	 * @param entities The {@link Entity entities} to store.
	 * @param acl The entity's ACL.
	 * @return A map of entities to their IDs.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	Map<Entity, String> storeEntities(Set<Entity> entities, String acl) throws EntityStoreException;
		
	/**
	 * Update the ACL of an entity. This sets the existing entity as not visible
	 * and creates a cloned entity with the new {@link Acl acl}. The ACL is not
	 * validated - the ACL should have already been validated.
	 * @param entityId The entity ID.
	 * @param acl The new ACL.
	 * @return The new entity's ID.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 * @throws NonexistantEntityException Thrown if the entity to update does not exist.
	 */
	String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException;
	
	/**
	 * Deletes an entity from the entity store.
	 * @param entityId The entity's ID.
	 */
	void deleteEntity(String entityId);
	
	/**
	 * Execute a query on the entity store. Implementations of the function
	 * must convert the input parameters included in the {@link EntityQuery}
	 * to a query suitable for the underlying store, such as SQL for
	 * a relational database.
	 * @param entityQuery The {@link EntityQuery} to execute.
	 * @return The query {@link QueryResult result}.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	QueryResult query(EntityQuery entityQuery) throws EntityStoreException;

	/**
	 * Get a count of the stored entities.
	 * @return A value that is the count of stored entities.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	long getEntityCount() throws EntityStoreException;
	
	/**
	 * Gets an entity from the store by ID.
	 * @param id The ID (UUID) of the entity.
	 * @return The entity or <code>null</code> if no matching entity is found.
	 */
	T getEntityById(String id);
	
	/**
	 * Gets a collection of entities by a collection of entity IDs.
	 * @param entityIds A collection of entity IDs.
	 * @param maskAcl Set to <code>true</code> to hide the ACL for each returned entity.
	 * @return A list of stored entities.
	 */
	List<T> getEntitiesByIds(List<String> entityIds, boolean maskAcl);
	
	/**
	 * Get a count of the stored entities for a context.
	 * @param context The context.
	 * @return A value that is the count of stored entities.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	long getEntityCount(String context) throws EntityStoreException;
	
	/**
	 * Gets a list of the stored contexts.
	 * @return A list of the stored contexts.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	List<String> getContexts() throws EntityStoreException;
	
	/**
	 * Deletes all entities for a given context. Use this function
	 * with caution as all entities stored under the given context will be deleted.
	 * @param context The context.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	void deleteContext(String context) throws EntityStoreException;
	
	/**
	 * Deletes all entities for a given document. Use this function
	 * with caution as all entities stored under the given document ID will be deleted.
	 * @param documentId The document ID.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	void deleteDocument(String documentId) throws EntityStoreException;
	
	/**
	 * Closes the connection to the entity store. This function may
	 * not be needed by all implementations of this interface.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	void close() throws EntityStoreException;
	
}