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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.domain.ContinuousQuery;
import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.services.NotificationService;

/**
 * Implementation of {@link NotificationService}.
 *  
 * @author Mountain Fog, Inc.
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