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
package com.mtnfog.entitydb.model.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.mtnfog.entitydb.model.datastore.entities.GroupEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;

/**
 * A user of EntityDB.
 * 
 * @author Mountain Fog, Inc.
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