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

package ai.philterd.entitydb.model.queue;

/**
 * A base class for messages that are queued. All classes that
 * inherit {@link QueueConsumer} and {@link QueuePublisher} should
 * use the classes extending {@link QueueMessage} in their implementations.
 * 
 * @author Philterd, LLC
 *
 */
public abstract class QueueMessage {
	
	/**
	 * Used to determine how long a message stays in a queue before
	 * being processed. This value is reported by a {@link MetricReporter}.
	 */
	private long timestamp = System.currentTimeMillis();
	
	/**
	 * Gets the timestamp of when this message was created.
	 * @return The timestamp of when this message was created.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
}