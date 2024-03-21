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



package ai.philterd.entitydb.model.status;

import java.util.Date;

/**
 * The status of EntityDB.
 * @author Philterd, LLC
 *
 */
public class Status {

	private long indexed;
	private long stored;
	private long timestamp;
	
	/**
	 * Creates a new status.
	 * @param indexed The number of indexed entities.
	 * @param stored The number of stored entities.
	 */
	public Status(long indexed, long stored) {
		this.indexed = indexed;
		this.stored = stored;
		this.timestamp = new Date().getTime();
	}
	
	public long getIndexed() {
		return indexed;
	}
	
	public long getStored() {
		return stored;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
}