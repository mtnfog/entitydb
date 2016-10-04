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
package com.mtnfog.entitydb.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;
import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.services.UserService;

/**
 * Implementation of {@link UserService} for managing users. This implementation
 * manages users through a local users.properties file.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Component
public class DefaultUserService implements UserService {
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultUserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserByApiKey(String apiKey) {
				
		UserEntity userEntity = userRepository.getByApiKey(apiKey);
		
		return User.fromEntity(userEntity);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authenticate(String apiKey) {
	
		UserEntity userEntity = userRepository.getByApiKey(apiKey);
		
		if(userEntity != null) {
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
		
}