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
import java.util.List;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.datastore.entities.ContinuousQueryEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.MalformedQueryException;
import ai.philterd.entitydb.model.exceptions.QueryExecutionException;
import ai.philterd.entitydb.model.exceptions.QueryGenerationException;
import ai.philterd.entitydb.model.security.Acl;

/**
 * Interface for the entity query service. Implementations of this interface
 * provide the querying capabilities for EntityDB.
 * 
 * @author Philterd, LLC
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
	 * @return The {@link QueryResult result}.
	 */
	public QueryResult eql(String query, String apiKey, int continuous, int days) throws MalformedQueryException, QueryExecutionException;
	
	/**
	 * Executes all continuous queries against the entity.
	 * @param entities A collection {@link Entity entities}.
	 * @param acl The {@link Acl ACL} of the entity.
	 * @param entitiesReceivedTimestamp The timestamp of when the entities were received.
	 */
	public void executeContinuousQueries(Collection<Entity> entities, Acl acl, long entitiesReceivedTimestamp);
	
}