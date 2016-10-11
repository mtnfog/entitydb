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
package com.mtnfog.test.entitydb.queues;

import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.mtnfog.entitydb.queues.consumers.SqsQueueConsumer;
import com.mtnfog.entitydb.queues.publishers.SqsQueuePublisher;

@Ignore
public class SqsQueueTest extends AbstractQueueTest {
	
	// https://github.com/adamw/elasticmq
	
	private SQSRestServer sqs;
	
	@Before
	public void before() throws Exception {
		
		super.before();
		
		sqs = SQSRestServerBuilder.withPort(9325).withInterface("localhost").start();
		
		final String endpoint = "http://localhost:9324";
		
		// TODO: What should the queueUrl be?
		final String queueUrl = "http://localhost:9324/queue";
		final int visibilityTimeout = 10;
		
		consumer = new SqsQueueConsumer(entityStore, rulesEngines, auditLogger, entityQueryService, endpoint, queueUrl, sleepSeconds, visibilityTimeout);
		publisher = new SqsQueuePublisher(queueUrl, endpoint);				
		
	}
	
	@After
	public void after() throws Exception {
		
		super.after();
		
		sqs.stopAndWait();
		
	}
	
}