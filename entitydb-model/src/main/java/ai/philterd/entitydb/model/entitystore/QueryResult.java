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

import java.util.List;

import ai.philterd.entitydb.model.search.IndexedEntity;

/**
 * The result of an entity query. An instance of this class
 * should not be provided to the end-user. Instead, create
 * an instance of {@link ExternalQueryResult} from an instance
 * of this class.
 * 
 * @author Philterd, LLC
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