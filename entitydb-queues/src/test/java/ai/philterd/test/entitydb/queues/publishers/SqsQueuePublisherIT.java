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
package ai.philterd.test.entitydb.queues.publishers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.metrics.DefaultMetricReporter;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.security.Acl;
import ai.philterd.entitydb.queues.publishers.SqsQueuePublisher;
import com.mtnfog.test.entity.utils.RandomEntityUtils;

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
		
		Entity entity = RandomEntityUtils.createRandomPersonEntity();
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Gson gson = new Gson();
		String json = gson.toJson(entities);
		
		LOGGER.info(json);
		
	}
	
	@Test
	public void store1() throws MalformedAclException, EntityPublisherException {
		
		Collection<Entity> entities = new LinkedList<Entity>();
		entities.add(RandomEntityUtils.createRandomPersonEntity());
		entities.add(RandomEntityUtils.createRandomPersonEntity());
		
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
		entities.add(RandomEntityUtils.createRandomPersonEntity());
		entities.add(RandomEntityUtils.createRandomPersonEntity());
		
		String acl = "1234:0";
		String apiKey = "apikey";
		
		SqsQueuePublisher sqsQueue = new SqsQueuePublisher("https://sqs.us-east-1.amazonaws.com/341239660749/entitydb-entities", ENDPOINT, metricReporter);
		sqsQueue.queueIngest(entities, acl, apiKey);
		
	}
	
}