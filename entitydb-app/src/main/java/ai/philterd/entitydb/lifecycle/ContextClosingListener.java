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
package ai.philterd.entitydb.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.search.SearchIndex;

@Component
public class ContextClosingListener implements ApplicationListener<ContextClosedEvent>{

	@Autowired
	private QueueConsumer queueConsumer;
	
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Override
	public void onApplicationEvent(ContextClosedEvent arg0) {
		
		// Gracefully stop things.
		queueConsumer.shutdown();
		searchIndex.close();	
		auditLogger.close();
		
	}
	
}