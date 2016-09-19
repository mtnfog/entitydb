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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.test.entitydb.queues;

import org.junit.Before;

import com.mtnfog.entitydb.queues.consumers.InternalQueueConsumer;
import com.mtnfog.entitydb.queues.publishers.InternalQueuePublisher;

public class InternalQueueTest extends AbstractQueueTest {

	protected int sleepSeconds = 1;
	
	@Before
	public void before() throws Exception {
		
		super.before();
		
		consumer = new InternalQueueConsumer(entityStore, rulesEngines, auditLogger, sleepSeconds);
		publisher = new InternalQueuePublisher();				
		
	}
	
}