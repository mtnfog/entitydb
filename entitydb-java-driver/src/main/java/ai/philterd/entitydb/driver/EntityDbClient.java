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
package com.mtnfog.entitydb.driver;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.driver.model.QueryResult;
import com.mtnfog.entitydb.driver.model.exceptions.EntityDbClientException;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class EntityDbClient implements EntityDb {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityDbClient.class);
	
	private static int DEFAULT_TIMEOUT = 120;
	
	private EntityDb entityDbApi = null;
	
	public EntityDbClient(String endpoint, final String apiKey, int timeout) {
		
		RequestInterceptor requestInterceptor = new RequestInterceptor() {  
		    @Override
		    public void intercept(RequestFacade request) {
		        request.addHeader("Authorization", apiKey);
		        request.addHeader("Content-Type", "application/json");
		        request.addHeader("Accept", "application/json");
		    }
		};
		
		final OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setReadTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		
		RestAdapter restAdapter = new RestAdapter
				.Builder()
				.setClient(new OkClient(okHttpClient))
				.setLogLevel(RestAdapter.LogLevel.FULL)
				.setRequestInterceptor(requestInterceptor)
				.setEndpoint(endpoint).build(); 
	
		entityDbApi = restAdapter.create(EntityDb.class);		
		
	}
	
	public EntityDbClient(String endpoint, final String apiKey) {
		
		this(endpoint, apiKey, DEFAULT_TIMEOUT);
		
	}
	
	@Override
	public Response store(String acl, Collection<Entity> entities) throws EntityDbClientException {
		
		Response response = null;
		
		try {
		
			response = entityDbApi.store(acl, entities);
		
		} catch (RetrofitError ex) {
			
			LOGGER.error("Unable to queue entities for EntityDB: " + ex.getMessage(), ex.getCause());
			
			throw new EntityDbClientException("Unable to queue entities for EntityDB: " + ex.getMessage())
				.withHttpResponseCode(ex.getResponse().getStatus());
			
		}

		LOGGER.trace("Successfully sent {} entities to EntityDB's queue.", entities.size());
		
		return response;
		
	}
		
	@Override
	public QueryResult eql(String query) throws EntityDbClientException {
		
		try {
		
			return entityDbApi.eql(query);
		
		} catch (RetrofitError ex) {
			
			LOGGER.error("Unable to execute EQL query: " + ex.getMessage(), ex.getCause());
			
			throw new EntityDbClientException("Unable to execute EQL query: " + ex.getMessage())
				.withHttpResponseCode(ex.getResponse().getStatus());
			
		}
		
	}

}