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
 * A group of users.
 * 
 * @author Philterd, LLC
 *
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 7032996772573941419L;
	
	private long id;
	private String groupName;
	private Set<String> users;
	
	/**
	 * Creates a {@link Group} from a {@link GroupEntity}.
	 * @param groupEntity A {@link GroupEntity}.
	 * @return A {@link Group}.
	 */
	public static Group fromEntity(GroupEntity groupEntity) {
				
		Set<String> users = new HashSet<String>();
		
		for(UserEntity userEntity : groupEntity.getUsers()) {
			users.add(userEntity.getUserName());
		}
		
		Group group = new Group();
		group.setId(groupEntity.getId());
		group.setGroupName(groupEntity.getGroupName());
		group.setUsers(users);
		
		return group;
		
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}
	
}