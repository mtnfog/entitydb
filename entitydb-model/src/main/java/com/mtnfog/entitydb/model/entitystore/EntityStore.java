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
package com.mtnfog.entitydb.model.entitystore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Interface for entity stores.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface EntityStore<T extends AbstractStoredEntity> {
	
	/**
	 * Gets the status of the entity store.
	 * @return The status of the entity store.
	 */
	public String getStatus();
	
	/**
	 * Gets a list of entities that have not been indexed.
	 * @param The maximum number of entities to retrieve.
	 * @return A list of entities that have not been indexed.
	 */
	public List<T> getNonIndexedEntities(int limit);
	
	/**
	 * Sets the entity's indexed property to the current time.
	 * @param entityId The entity ID.
	 * @return <code>true</code> if the operation is successful; otherwise <code>false</code>.
	 */
	public boolean markEntityAsIndexed(String entityId);
	
	/**
	 * Sets the entitys' indexed property to the current time.
	 * @param entityIds A list of entity IDs.
	 * @return The number of updated entities.
	 */
	public long markEntitiesAsIndexed(Collection<String> entityIds);
	
	/**
	 * Store the entity in the store.
	 * @param entity The {@link Entity} to store.
	 * @param acl The entity's ACL.
	 * @return The stored entity's ID.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public String storeEntity(Entity entity, String acl) throws EntityStoreException;
	
	/**
	 * Store the entities in the store.
	 * @param entities The {@link Entity entities} to store.
	 * @param acl The entity's ACL.
	 * @return A map of entities to their IDs.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) throws EntityStoreException;
		
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
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException;
	
	/**
	 * Deletes an entity from the entity store.
	 * @param entityId The entity's ID.
	 */
	public void deleteEntity(String entityId);
	
	/**
	 * Execute a query on the entity store. Implementations of the function
	 * must convert the input parameters included in the {@link EntityQuery}
	 * to a query suitable for the underlying store, such as SQL for
	 * a relational database.
	 * @param entityQuery The {@link EntityQuery} to execute.
	 * @return The query {@link QueryResult result}.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public QueryResult query(EntityQuery entityQuery) throws EntityStoreException;

	/**
	 * Get a count of the stored entities.
	 * @return A value that is the count of stored entities.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public long getEntityCount() throws EntityStoreException;
	
	/**
	 * Gets an entity from the store by ID.
	 * @param id The ID (UUID) of the entity.
	 * @return The entity or <code>null</code> if no matching entity is found.
	 */
	public T getEntityById(String id);
	
	/**
	 * Gets a collection of entities by a collection of entity IDs.
	 * @param entityIds A collection of entity IDs.
	 * @param maskAcl Set to <code>true</code> to hide the ACL for each returned entity.
	 * @return A list of stored entities.
	 */
	public List<T> getEntitiesByIds(List<String> entityIds, boolean maskAcl);
	
	/**
	 * Get a count of the stored entities for a context.
	 * @param context The context.
	 * @return A value that is the count of stored entities.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public long getEntityCount(String context) throws EntityStoreException;
	
	/**
	 * Gets a list of the stored contexts.
	 * @return A list of the stored contexts.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public List<String> getContexts() throws EntityStoreException;
	
	/**
	 * Deletes all entities for a given context. Use this function
	 * with caution as all entities stored under the given context will be deleted.
	 * @param context The context.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public void deleteContext(String context) throws EntityStoreException;
	
	/**
	 * Deletes all entities for a given document. Use this function
	 * with caution as all entities stored under the given document ID will be deleted.
	 * @param documentId The document ID.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public void deleteDocument(String documentId) throws EntityStoreException;
	
	/**
	 * Closes the connection to the entity store. This function may
	 * not be needed by all implementations of this interface.
	 * @throws EntityStoreException Thrown if the operation cannot be completed.
	 */
	public void close() throws EntityStoreException;
	
}