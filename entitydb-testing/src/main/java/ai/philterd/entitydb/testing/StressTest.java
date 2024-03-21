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
package ai.philterd.entitydb.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StressTest {
	
	private static final Logger LOGGER = LogManager.getLogger(StressTest.class);
	
	public static void main(String[] args) {		
		
		final String host = args[0];
		final String port = args[1];
		final int count = Integer.valueOf(args[2]);
		
		final String entityDBEndpoint = String.format("http://%s:%s/", host, port);
		
		String apiKey = "asdf1234";
		
		StressTest stressTest = new StressTest();
		stressTest.run(entityDBEndpoint, apiKey, count);
		
	}
	
	private StressTest() {
		
	}
	
	public void run(String endpoint, String apiKey, int count) {
		
		/*EntityDbClient client = new EntityDbClient(endpoint, apiKey);
		
		String context = "context1";
		String documentId = "documentId1";
		String acl = "user:group:1";
		
		int sent = 0;
		
		while(sent < count) {
			
			// Make a random number of entities.
			int randomEntityCount = RandomUtils.nextInt(1, 50);
			
			sent += randomEntityCount;
			
			LOGGER.info("Sending {} entities to EntityDB. Total sent = {}", randomEntityCount, sent);
			
			Collection<Entity> entities = RandomEntityUtils.createRandomPersonEntities(randomEntityCount);						
			
			Response response = client.store(acl, entities);
			
			assert(response.getStatus() == 201);
			
		}		*/
		
	}
	
}