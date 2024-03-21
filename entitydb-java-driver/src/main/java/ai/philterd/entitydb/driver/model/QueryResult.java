/*
 * Copyright (c) 2016 Mountain Fog, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.philterd.entitydb.driver.model;

import java.util.List;

/**
 * The result of an entity query that can be provided
 * to the end-user. An instance of this class is
 * created from a {@link QueryResult} in this class's
 * constructor.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class QueryResult {

	private List<IndexedEntity> entities;
	private String queryId;
		
	/**
	 * Gets the list of {@link IndexedEntity entities}.
	 * @return A list of {@link IndexedEntity entities}.
	 */
	public List<IndexedEntity> getEntities() {
		return entities;
	}

	/**
	 * Sets the stored entities resulting from the query.
	 * @param entities A list of {@link IndexedEntity entities.}.
	 */
	public void setEntities(List<IndexedEntity> entities) {
		this.entities = entities;
	}

	/**
	 * Gets the ID of the query.
	 * @return The ID of the query.
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * Sets the ID of the query.
	 * @param queryId The ID of the query.
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
}