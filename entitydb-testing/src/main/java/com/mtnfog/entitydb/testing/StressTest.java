/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
package com.mtnfog.entitydb.testing;

import java.util.Collection;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import retrofit.client.Response;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.driver.EntityDbClient;
import com.mtnfog.test.entity.utils.EntityUtils;

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
		
		EntityDbClient client = new EntityDbClient(endpoint, apiKey);
		
		String context = "context1";
		String documentId = "documentId1";
		String acl = "user:group:1";
		
		int sent = 0;
		
		while(sent < count) {
			
			// Make a random number of entities.
			int randomEntityCount = RandomUtils.nextInt(1, 50);
			
			sent += randomEntityCount;
			
			LOGGER.info("Sending {} entities to EntityDB. Total sent = {}", randomEntityCount, sent);
			
			Collection<Entity> entities = EntityUtils.createRandomPersonEntities(randomEntityCount);						
			
			Response response = client.store(acl, entities);
			
			assert(response.getStatus() == 201);
			
		}		
		
	}
	
}