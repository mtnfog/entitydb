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
 * An audited action.
 * 
 * @author Philterd, LLC
 *
 */
public enum AuditAction {

	/**
	 * An entity was visible due to a search.
	 */
	SEARCH_RESULT("search_result"),
	
	/**
	 * A query that was executed.
	 */
	QUERY("query"),
	
	/**
	 * An entity was stored.
	 */
	STORED("stored"),
	
	/**
	 * An entity's ACL was updated.
	 */
	ACL_UPDATED("acl_updated"),
	
	/**
	 * An entity was skipped because it already exists in the store.
	 */
	SKIPPED("skipped"),
	
	/**
	 * The entity was indexed.
	 */
	INDEXED("indexed");
	
	private String auditAction;
	
	private AuditAction(String auditAction) {
		
		this.auditAction = auditAction;
		
	}
	
	/**
	 * Gets the name of the audit action.
	 * @return The name of the audit action.
	 */
	@Override
	public String toString() {
		
		return auditAction;
		
	}
	
}