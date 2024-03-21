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

import java.util.List;

import ai.philterd.entitydb.model.domain.ContinuousQuery;
import ai.philterd.entitydb.model.domain.Notification;
import ai.philterd.entitydb.model.domain.User;

/**
 * Interface for user service.
 * 
 * @author Philterd, LLC
 *
 */
public interface UserService {

	/**
	 * Gets a user given an API key.
	 * @param apiKey The API key.
	 * @return A {@link UserEntity user} or <code>null</code> if no user is found.
	 */
	public User getUserByApiKey(String apiKey);
	
	/**
	 * Authenticate the API key.
	 * @param apiKey The API key.
	 * @return <code>true</code> if the API key is valid for a user, otherwise <code>false</code>.
	 */
	public boolean authenticate(String apiKey);
		
	/**
	 * Gets all notifications for a user.
	 * @param apiKey The user's API key.
	 * @return A list of {@link Notification notifications}.
	 */
	public List<Notification> getUserNotifications(String apiKey);
	
	/**
	 * Gets all continuous queries for a user.
	 * @param apiKey The user's API key.
	 * @return A list of {@link ContinuousQuery queries}.
	 */
	public List<ContinuousQuery> getUserContinuousQueries(String apiKey);
	
}