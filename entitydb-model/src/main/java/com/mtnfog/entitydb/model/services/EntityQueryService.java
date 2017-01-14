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
package com.mtnfog.entitydb.model.services;

import java.util.Collection;
import java.util.List;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.MalformedQueryException;
import com.mtnfog.entitydb.model.exceptions.QueryExecutionException;
import com.mtnfog.entitydb.model.security.Acl;

/**
 * Interface for the entity query service. Implementations of this interface
 * provide the querying capabilities for EntityDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface EntityQueryService {
	
	/**
	 * Gets a list of non-expired continuous queries.
	 * @return A list of {@link ContinuousQueryEntity}.
	 */
	public List<ContinuousQueryEntity> getNonExpiredContinuousQueries();
	
	/**
	 * Gets a list of continuous queries for a user.
	 * @param userEntity The {@link UserEntity}.
	 * @return A list of {@link ContinuousQueryEntity}.
	 */
	public List<ContinuousQueryEntity> findByUserOrderByIdDesc(UserEntity userEntity);

	/**
	 * Saves a continuous query.
	 * @param continuousQueryEntity A {@link ContinuousQueryEntity}.
	 * @return The persisted {@link ContinuousQueryEntity}.
	 */
	public ContinuousQueryEntity save(ContinuousQueryEntity continuousQueryEntity);
	
	/**
	 * Deletes a continuous query.
	 * @param continuousQueryEntity The {@link ContinuousQueryEntity} to delete.
	 */
	public void delete(ContinuousQueryEntity continuousQueryEntity);

	/**
	 * Execute an EQL query.
	 * @param query The EQL query.
	 * @param apiKey The user's API key.
	 * @param continuous <code>1</code> if the query is to be a continuous query. 
	 * @param days The number of days to be continuous.
	 * @return The {@link ExternalQueryResult result}.
	 */
	public QueryResult eql(String query, String apiKey, int continuous, int days) throws MalformedQueryException, QueryExecutionException;
	
	/**
	 * Executes all continuous queries against the entity.
	 * @param entity A collection {@link Entity entities}.
	 * @param acl The {@link Acl ACL} of the entity.
	 * @param entitiesReceivedTimestamp The timestamp of when the entities were received.
	 */
	public void executeContinuousQueries(Collection<Entity> entities, Acl acl, long entitiesReceivedTimestamp);
	
}