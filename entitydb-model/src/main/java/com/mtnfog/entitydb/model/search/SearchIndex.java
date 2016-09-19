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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.model.search;

import java.util.List;
import java.util.Set;

import com.mtnfog.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.model.users.User;
import com.mtnfog.entitydb.eql.model.EntityQuery;

/**
 * Interface to a search index.
 * 
 * @author Mountain Fog, Inc.
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
	 * @return Count of indexed entities.
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
	 * @param user The {@link User user} executing the query.
	 * @return A list of entity IDs for the matching entities.
	 * @throws InvalidQueryException Thrown if the query is invalid.
	 */
	public List<String> queryForEntityIds(EntityQuery entityQuery, User user) throws InvalidQueryException;
	
	/**
	 * Execute a query against the index.
	 * @param entityQuery The {@link EntityQuery query} to execute.
	 * @param user The {@link User user} executing the query.
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