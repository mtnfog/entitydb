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
package com.mtnfog.test.entitydb.queues.publishers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.metrics.DefaultMetricReporter;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.queues.publishers.SqsQueuePublisher;
import com.mtnfog.test.entity.utils.EntityUtils;

public class SqsQueuePublisherIT {

	private static final Logger LOGGER = LogManager.getLogger(SqsQueuePublisherIT.class);
	
	private final String ENDPOINT = "sqs.us-east-1.amazonaws.com";
	
	private MetricReporter metricReporter;
	
	@Before
	public void before() {
		
		metricReporter = new DefaultMetricReporter();
		
	}
	
	@Test
	public void json() {
		
		Entity entity = EntityUtils.createRandomPersonEntity();
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Gson gson = new Gson();
		String json = gson.toJson(entities);
		
		LOGGER.info(json);
		
	}
	
	@Test
	public void store1() throws MalformedAclException, EntityPublisherException {
		
		Collection<Entity> entities = new LinkedList<Entity>();
		entities.add(EntityUtils.createRandomPersonEntity());
		entities.add(EntityUtils.createRandomPersonEntity());
		
		String[] users = {"user1", "user2"};
		String[] groups = {"group1", "group2"};
		
		Acl acl = new Acl(users, groups, false);
		String apiKey = "apikey";
		
		SqsQueuePublisher sqsQueue = new SqsQueuePublisher("https://sqs.us-east-1.amazonaws.com/341239660749/entitydb-entities", ENDPOINT, metricReporter);
		sqsQueue.queueIngest(entities, acl.toString(), apiKey);
		
	}
	
	@Test(expected = MalformedAclException.class)
	public void store2() throws MalformedAclException, EntityPublisherException {
		
		Collection<Entity> entities = new LinkedList<Entity>();
		entities.add(EntityUtils.createRandomPersonEntity());
		entities.add(EntityUtils.createRandomPersonEntity());
		
		String acl = "1234:0";
		String apiKey = "apikey";
		
		SqsQueuePublisher sqsQueue = new SqsQueuePublisher("https://sqs.us-east-1.amazonaws.com/341239660749/entitydb-entities", ENDPOINT, metricReporter);
		sqsQueue.queueIngest(entities, acl, apiKey);
		
	}
	
}