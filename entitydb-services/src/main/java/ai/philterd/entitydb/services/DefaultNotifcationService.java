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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.domain.ContinuousQuery;
import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.services.NotificationService;

/**
 * Implementation of {@link NotificationService}.
 *  
 * @author Philterd, LLC
 *
 */
@Component
public class DefaultNotifcationService implements NotificationService {

	@Autowired
	private MetricReporter metricReporter;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendContinuousQueryNotification(ContinuousQuery continuousQuery, Entity entity) {

		// TODO: Notify the user of a match via the SNS topic.
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createNotificationTopic(User user) {
		
		// TODO: Create the SNS topic and subscribe the user to the topic.
		// TODO: Return the new topic's ARN.
		
		return null;
		
	}
	
}