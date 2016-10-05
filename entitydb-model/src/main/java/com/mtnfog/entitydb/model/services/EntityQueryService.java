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
package com.mtnfog.entitydb.model.services;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.entitystore.QueryResult;

/**
 * Interface for the entity query service. Implementations of this interface
 * provide the querying capabilities for EntityDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface EntityQueryService {

	/**
	 * Execute an EQL query.
	 * @param query The EQL query.
	 * @param apiKey The user's API key.
	 * @param continuous <code>1</code> if the query is to be a continuous query. 
	 * @param days The number of days to be continuous.
	 * @return The {@link ExternalQueryResult result}.
	 */
	public QueryResult eql(String query, String apiKey, int continuous, int days);
	
	/**
	 * Executes all continuous queries against the entity.
	 * @param entity The {@link Entity entity}.
	 * @param entityId The ID of the entity.
	 */
	public void executeContinuousQueries(Entity entity, String entityId);
	
}