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
package ai.philterd.entitydb.services;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.datastore.repository.NotificationRepository;
import ai.philterd.entitydb.datastore.repository.UserRepository;
import ai.philterd.entitydb.model.datastore.entities.ContinuousQueryEntity;
import ai.philterd.entitydb.model.datastore.entities.NotificationEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;
import ai.philterd.entitydb.model.domain.ContinuousQuery;
import ai.philterd.entitydb.model.domain.Notification;
import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.services.EntityQueryService;
import ai.philterd.entitydb.model.services.UserService;

/**
 * Implementation of {@link UserService} for managing users. This implementation
 * manages users through a local users.properties file.
 * 
 * @author Philterd, LLC
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
	private EntityQueryService entityQueryService;
		
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
		
			List<ContinuousQueryEntity> continuousQueryEntities = entityQueryService.findByUserOrderByIdDesc(userEntity);
			
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