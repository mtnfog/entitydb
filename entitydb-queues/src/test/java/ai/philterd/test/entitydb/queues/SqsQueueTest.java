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

import org.elasticmq.NodeAddress;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.After;
import org.junit.Before;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import ai.philterd.entitydb.queues.consumers.SqsQueueConsumer;
import ai.philterd.entitydb.queues.publishers.SqsQueuePublisher;

public class SqsQueueTest extends AbstractQueueTest {

	private SQSRestServer sqs;
	
	@Before
	public void before() throws Exception {
		
		super.before();
		
		sqs = SQSRestServerBuilder.withServerAddress(new NodeAddress("http", "localhost", 9324, "")).start();
		sqs.waitUntilStarted();
		
		final String endpoint = "http://localhost:9324";
		
		AmazonSQSClient client = new AmazonSQSClient();
		client.setEndpoint(endpoint);
		
		CreateQueueResult result = client.createQueue("entitydb");
				
		final int visibilityTimeout = 10;
			
		consumer = new SqsQueueConsumer(entityStore, rulesEngines, auditLogger, metricReporter, endpoint, result.getQueueUrl(), "a", "s", visibilityTimeout, indexerCache);
		publisher = new SqsQueuePublisher(result.getQueueUrl(), endpoint, "a", "s", metricReporter);				
		
	}
	
	@After
	public void after() throws Exception {
		
		super.after();
		
		sqs.stopAndWait();
		
	}
	
}