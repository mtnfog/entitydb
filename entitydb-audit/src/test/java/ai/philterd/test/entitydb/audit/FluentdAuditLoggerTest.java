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
package ai.philterd.test.entitydb.audit;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import ai.philterd.entitydb.audit.FluentdAuditLogger;
import ai.philterd.entitydb.model.audit.AuditAction;

public class FluentdAuditLoggerTest {

	@Test
	@Ignore
	public void logEntity() {
		
		FluentdAuditLogger logger = new FluentdAuditLogger("junit");
		
		String entityId = UUID.randomUUID().toString();
		long timestamp = System.currentTimeMillis();
		String apiKey = "apikey";
		
		logger.audit(entityId, timestamp, apiKey, AuditAction.SEARCH_RESULT);
		
	}
	
}