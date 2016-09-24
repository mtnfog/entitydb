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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.model.users;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * A user of EntityDB's API.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class User {

	private String username;
	private String apiKey;
	private Set<String> groups;
	
	/**
	 * Creates a new user.
	 * @param username The user's name.
	 * @param apiKey The user's API key.
	 * @param groups A list of the user's groups.
	 */
	public User(String username, String apiKey, Set<String> groups) {
		
		this.username = username;
		this.apiKey = apiKey;
		this.groups = groups;
				
	}
	
	/**
	 * Determine if a {@link User user} in the list has the given API key.
	 * @param users A list of {@link User users}.
	 * @param apiKey The API key.
	 * @return <code>true</code> if the list of users contains a user that
	 * has the given API key; otherwise <code>false</code>.
	 */
	public static boolean authenticate(List<User> users, String apiKey) {
		
		for(User user : users) {
			
			if(StringUtils.equals(user.getApiKey(), apiKey)) {
				
				return true;
				
			}
			
		}
		
		return false;
		
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
	
}