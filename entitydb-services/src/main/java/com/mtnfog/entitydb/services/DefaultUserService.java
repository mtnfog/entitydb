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

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.datastore.repository.ContinuousQueryRepository;
import com.mtnfog.entitydb.datastore.repository.NotificationRepository;
import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;
import com.mtnfog.entitydb.model.datastore.entities.NotificationEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;
import com.mtnfog.entitydb.model.domain.ContinuousQuery;
import com.mtnfog.entitydb.model.domain.Notification;
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
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private ContinuousQueryRepository continuousQueryRepository;
		
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Notification> getUserNotifications(String apiKey) {
		
		List<Notification> notifications = new LinkedList<Notification>();
		
		UserEntity userEntity = userRepository.getByApiKey(apiKey);
				
		// When coming through the API the user will exist because if not
		// the AuthorizationInterceptor will deny the request.
		
		if(userEntity != null) {
		
			List<NotificationEntity> notificationEntities = notificationRepository.findByUserOrderByIdDesc(userEntity);
			
			for(NotificationEntity notificationEntity : notificationEntities) {
				
				Notification notification = Notification.fromEntity(notificationEntity);
				
				notifications.add(notification);
				
			}
			
		} else {
			
			LOGGER.debug("Unable to get notifications for nonexistant user having API key {}.", apiKey);
			
		}
		
		return notifications;
					
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ContinuousQuery> getUserContinuousQueries(String apiKey) {
		
		List<ContinuousQuery> continuousQueries = new LinkedList<ContinuousQuery>();
		
		UserEntity userEntity = userRepository.getByApiKey(apiKey);
				
		// When coming through the API the user will exist because if not
		// the AuthorizationInterceptor will deny the request.
		
		if(userEntity != null) {
		
			List<ContinuousQueryEntity> continuousQueryEntities = continuousQueryRepository.findByUserOrderByIdDesc(userEntity);
			
			for(ContinuousQueryEntity continuousQueryEntity : continuousQueryEntities) {
				
				ContinuousQuery continuousQuery = ContinuousQuery.fromEntity(continuousQueryEntity);
				
				continuousQueries.add(continuousQuery);
				
			}
			
		} else {
			
			LOGGER.debug("Unable to get continuous queries for nonexistant user having API key {}.", apiKey);
			
		}
		
		return continuousQueries;
					
	}
		
}