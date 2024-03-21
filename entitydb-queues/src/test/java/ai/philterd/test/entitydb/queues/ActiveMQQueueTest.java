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
package ai.philterd.test.entitydb.queues;

import java.net.URI;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import ai.philterd.entitydb.queues.consumers.ActiveMQQueueConsumer;
import ai.philterd.entitydb.queues.publishers.ActiveMQQueuePublisher;

@Ignore
public class ActiveMQQueueTest extends AbstractQueueTest {
	
	private static final Logger LOGGER = LogManager.getLogger(ActiveMQQueueTest.class);

	private static final String BROKER_URL = "tcp://localhost:61616";
	private static final String QUEUE_NAME = "entities";
	private static final int TIMEOUT = 10000;
	
	private BrokerService broker;	
	
	@Before
	public void before() throws Exception {
		
		super.before();
		
		broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));		
        broker.start();
		
		consumer = new ActiveMQQueueConsumer(entityStore, rulesEngines, auditLogger, metricReporter, BROKER_URL, QUEUE_NAME, TIMEOUT, indexerCache);
		publisher = new ActiveMQQueuePublisher(BROKER_URL, QUEUE_NAME, metricReporter); 		
		        
	}
	
	@After
	public void after() throws Exception {
	
		super.after();
		
		broker.stop();
		
	}
		
}