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
package ai.philterd.entitydb.rulesengine.actions;

import java.util.Collection;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.integrations.aws.DynamoDBIntegration;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

public class DynamoDB extends DynamoDBIntegration implements Integration {
	
	public DynamoDB(String tableName, String endpoint) {
		super(tableName, endpoint);
	}
		
	public DynamoDB(String tableName, String endpoint, String accessKey, String secretKey) {
		super(tableName, endpoint, accessKey, secretKey);
	}
	
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
		super.process(entities);
	}
	
}