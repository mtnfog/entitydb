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
 * Constants used by the queues.
 * 
 * @author Philterd, LLC
 *
 */
public class QueueConstants {
	
	/**
	 * The action to be taken on the queue message.
	 */
	public static final String ACTION = "action";	
	
	/**
	 * An entity is to be ingested.
	 */
	public static final String ACTION_INGEST = "ingest";
	
	/**
	 * An entity's ACL is to be updated.
	 */
	public static final String ACTION_UPDATE_ACL = "updateAcl";	
	
	private QueueConstants() {
		// This is a utility class.
	}
	
}