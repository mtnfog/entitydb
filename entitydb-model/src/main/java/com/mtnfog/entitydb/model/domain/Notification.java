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
package com.mtnfog.entitydb.model.domain;

import java.util.Date;

import com.mtnfog.entitydb.model.datastore.entities.NotificationEntity;

/**
 * A notification to a user.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class Notification {
	
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