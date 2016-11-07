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

import java.util.List;

import com.mtnfog.entitydb.model.search.IndexedEntity;

/**
 * The result of an entity query. An instance of this class
 * should not be provided to the end-user. Instead, create
 * an instance of {@link ExternalQueryResult} from an instance
 * of this class.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class QueryResult {

	private List<IndexedEntity> entities;
	private String queryId;
	
	/**
	 * Creates a query result.
	 * @param entities The entities returned as a result of the query.
	 * @param queryId The ID of the query.
	 */
	public QueryResult(List<IndexedEntity> entities, String queryId) {
		
		this.entities = entities;
		this.queryId = queryId;
		
	}

	/**
	 * Gets the query's ID.
	 * @return The query's ID.
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * Sets the query's ID.
	 * @param queryId The query's ID.
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	/**
	 * Gets the list {@link IndexedEntity entities}.
	 * @return The list {@link IndexedEntity entities}.
	 */
	public List<IndexedEntity> getEntities() {
		return entities;
	}

	/**
	 * sets the {@link IndexedEntity entities}.
	 * @param entities A list of {@link IndexedEntity entities}.
	 */
	public void setEntities(List<IndexedEntity> entities) {
		this.entities = entities;
	}

}