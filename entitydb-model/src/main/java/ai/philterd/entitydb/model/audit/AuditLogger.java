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
package ai.philterd.entitydb.model.audit;

/**
 * Interface for audit log stores.
 * 
 * @author Philterd, LLC
 *
 */
public interface AuditLogger {

	/**
	 * For auditable events that are caused by the system and not an individual user.
	 */
	public static final String SYSTEM = "SYSTEM";
	
	/**
	 * Write the audit log to the 
	 * @param entityId The ID of the entity.
	 * @param timestamp When the event took place.
	 * @param userName The user's name.
	 * @param auditAction The {@link AuditAction action} being audited.
	 * @return <code>true</code> when the audit operation succeeds; <code>false</code> otherwise.
	 */
	public boolean audit(String entityId, long timestamp, String userName, AuditAction auditAction);
	
	/**
	 * Write the audit log to the 
	 * @param query The ID of the entity.
	 * @param timestamp When the event took place.
	 * @param userIdentifier A unique identifier of the user.
	 * @return <code>true</code> when the audit operation succeeds; <code>false</code> otherwise.
	 */
	public boolean audit(String query, long timestamp, String userName);
	
	/**
	 * Closes and releases any resources.
	 * Implementing this function may not be required for all implementations.
	 */
	public void close();
	
}