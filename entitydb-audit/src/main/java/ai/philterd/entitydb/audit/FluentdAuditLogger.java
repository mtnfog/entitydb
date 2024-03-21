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
package ai.philterd.entitydb.audit;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fluentd.logger.FluentLogger;

import ai.philterd.entitydb.model.audit.AuditAction;
import ai.philterd.entitydb.model.audit.AuditLogger;

/**
 * Implementation of {@link AuditLogger} that used Fluentd
 * to log audit events.
 * 
 * @author Philterd, LLC
 *
 */
public class FluentdAuditLogger extends AbstractAuditLogger implements AuditLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(FluentdAuditLogger.class);

	private static final FluentLogger FLUENT_LOGGER = FluentLogger.getLogger("entitydb");
	
	public FluentdAuditLogger(String systemId) {
		super(systemId);
	}
	

	@Override
	public boolean audit(String entityId, long timestamp, String userIdentifier, AuditAction auditAction) {
		
		Map<String, Object> data = new HashMap<String, Object>();      		
		
		data.put("entityId", entityId);		
		data.put("timestamp", timestamp);
        data.put("userIdentifier", userIdentifier);
        data.put("action", auditAction.toString());
        data.put("systemId", systemId);
        
        return FLUENT_LOGGER.log("follow", data);
		
	}
	

	@Override
	public boolean audit(String query, long timestamp, String userName) {
		
		Map<String, Object> data = new HashMap<String, Object>();      		
		
		data.put("query", query);		
		data.put("timestamp", timestamp);
        data.put("userName", userName);
        data.put("action", AuditAction.QUERY.toString());
        data.put("systemId", systemId);
        
        return FLUENT_LOGGER.log("follow", data);
		
	}
	

	@Override
	public void close() {
		
		FLUENT_LOGGER.close();
		
	}
 
}