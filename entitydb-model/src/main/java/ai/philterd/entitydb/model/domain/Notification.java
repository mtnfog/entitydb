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
import java.util.Date;

import ai.philterd.entitydb.model.datastore.entities.NotificationEntity;

/**
 * A notification to a user.
 * 
 * @author Philterd, LLC
 *
 */
public class Notification implements Serializable {
	
	private static final long serialVersionUID = 5833939084527987491L;
	
	private long id;
	private User user;
	private Date timestamp;
	private String notification;
	private int type;
	
	/**
	 * Creates a {@link Notification} from a {@link NotificationEntity}.
	 * @param notificationEntity A {@link NotificationEntity}.
	 * @return A {@link Notification}.
	 */
	public static Notification fromEntity(NotificationEntity notificationEntity) {
		
		Notification notification = new Notification();
		notification.setId(notificationEntity.getId());
		notification.setNotification(notificationEntity.getNotification());
		notification.setTimestamp(notificationEntity.getTimestamp());
		notification.setType(notificationEntity.getType());
		notification.setUser(User.fromEntity(notificationEntity.getUser()));
		
		return notification;
		
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}	

}