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
package ai.philterd.entitydb.model.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ai.philterd.entitydb.model.datastore.entities.GroupEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;

/**
 * A user of EntityDB.
 * 
 * @author Philterd, LLC
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = -3813377004516421415L;
	
	private long id;
	private String username;
	private String email;
	private String mobile;
	private String apiKey;
	private Set<String> groups;
	
	/**
	 * Creates a new user.
	 * @param username The user's name.
	 * @param email The user's email address. The email address is used for notifications. Cannot be <code>null</code>.
	 * @param mobile The user's mobile phone number. The mobile phone number is used for notifications. Can be <code>null</code>.
	 * @param apiKey The user's API key.
	 * @param groups A list of the user's groups.
	 */
	public User(long id, String username, String email, String mobile, String apiKey, Set<String> groups) {
		
		this.id = id;
		this.username = username;		
		this.email = email;
		this.mobile = mobile;
		this.apiKey = apiKey;
		this.groups = groups;
				
	}
	
	/**
	 * Creates a {@link User} from a {@link UserEntity}.
	 * @param userEntity A {@link UserEntity}.
	 * @return A {@link User}.
	 */
	public static User fromEntity(UserEntity userEntity) {
		
		Set<String> groups = new HashSet<String>();
		
		for(GroupEntity groupEntity : userEntity.getGroups()) {

			groups.add(groupEntity.getGroupName());
			
		}
		
		
		User user = new User(userEntity.getId(), userEntity.getUserName(), userEntity.getEmail(), userEntity.getMobile(), userEntity.getApiKey(), groups);
		
		return user;
		
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}