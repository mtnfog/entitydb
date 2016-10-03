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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.mtnfog.entitydb.configuration.UserProperties;
import com.mtnfog.entitydb.model.services.UserService;
import com.mtnfog.entitydb.model.users.User;

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
	
	private static final UserProperties usersProperties = ConfigFactory.create(UserProperties.class);
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserByApiKey(String apiKey) {
		
		for(User user : getUsersAndGroups()) {
			
			if(StringUtils.equals(user.getApiKey(), apiKey)) {
				
				return user;
				
			}
			
		}
		
		return null;
		
	}
		
	private List<User> getUsersAndGroups() {
		
		List<User> users = new LinkedList<User>();
		
		InputStream input = null;

		try {

			Properties properties = new Properties();
			input = new FileInputStream("users.properties");
			properties.load(input);
			
			for(String username : usersProperties.getUsers().split(",")) {
				
				final String userApiKey = properties.getProperty("user." + username + ".apikey");				
				final String userGroups = properties.getProperty("user." + username + ".groups");
				
				Set<String> groups = null;
				
				if(StringUtils.isNotEmpty(userGroups)) {	
					
					groups = Sets.newHashSet(userGroups.split(","));
					
				} else {
					
					groups = new HashSet<String>();
					
				}
				
				User user = new User(username, userApiKey, groups);
				
				users.add(user);
				
			}

		} catch (IOException ex) {			
			LOGGER.error("Unable to load user.properties.", ex);			
		} finally {
			IOUtils.closeQuietly(input);			
		}
		
		return users;
		
	}
	
}