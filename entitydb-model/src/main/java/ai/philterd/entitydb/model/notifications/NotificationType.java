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
package ai.philterd.entitydb.model.notifications;

/**
 * Types of notifications.
 * 
 * @author Philterd, LLC
 *
 */
public enum NotificationType {

	/**
	 * A notification that was generated as a result of an entity match to a continuous query.
	 */
	CONTINUOUS_QUERY(1);
	
	private int type;
	
	private NotificationType(int type) {
		this.type = type;
	}
	
	/**
	 * Gets the integer value of the notification type.
	 * @return The integer value of the notification type.
	 */
	public int getValue() {
		return type;
	}
	
}