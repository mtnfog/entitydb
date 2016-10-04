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
package com.mtnfog.entitydb.model.services;

import com.mtnfog.entitydb.model.domain.User;

/**
 * Interface for user service.
 * 
 * @author Mountain Fog, Inc.
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
		
}