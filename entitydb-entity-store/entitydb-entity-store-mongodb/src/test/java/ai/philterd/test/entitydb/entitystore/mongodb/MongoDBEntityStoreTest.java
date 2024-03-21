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
package ai.philterd.test.entitydb.entitystore.mongodb;

import com.github.fakemongo.Fongo;
import ai.philterd.entitydb.entitystore.mongodb.MongoDBEntityStore;
import ai.philterd.entitydb.entitystore.mongodb.model.MongoDBStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.testing.AbstractEntityStoreTest;

public class MongoDBEntityStoreTest extends AbstractEntityStoreTest<MongoDBStoredEntity> {

	// private final String MONGODB_HOST = "";	
	// private final int MONGODB_PORT = 43962;
	// private final String MONGODB_USERNAME = "";
	// private final String MONGODB_PASSWORD = "";
	private final String DATABASE_NAME = "entitydb";
	private final String COLLECTION_NAME = "entities";	
	private final String CONTEXT = "context";
		
	public EntityStore<MongoDBStoredEntity> getEntityStore() throws EntityStoreException {

		// Using Fongo:
		Fongo fongo = new Fongo("mongo server 1");
		MongoDBEntityStore mongoDBEntityStore = new MongoDBEntityStore(fongo.getMongo(), DATABASE_NAME, COLLECTION_NAME);
				
		// Using a real MongoDB:
		// mongoDBEntityStore = new MongoDBEntityStore(MONGODB_HOST, MONGODB_PORT, MONGODB_USERNAME, MONGODB_PASSWORD, DATABASE_NAME, COLLECTION_NAME);
				
		// Delete everything from the collection.
		mongoDBEntityStore.deleteContext(CONTEXT);
		
		return mongoDBEntityStore;
				
	}
	
}