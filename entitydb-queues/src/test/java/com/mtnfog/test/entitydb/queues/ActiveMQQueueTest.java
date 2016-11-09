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

import java.net.URI;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.mtnfog.entitydb.queues.consumers.ActiveMQQueueConsumer;
import com.mtnfog.entitydb.queues.publishers.ActiveMQQueuePublisher;

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