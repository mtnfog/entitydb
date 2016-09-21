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
package com.mtnfog.test.entitydb.entitystore.mongodb;

import com.github.fakemongo.Fongo;
import com.mtnfog.entitydb.entitystore.mongodb.MongoDBEntityStore;
import com.mtnfog.entitydb.entitystore.mongodb.model.MongoDBStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.test.entitydb.entitystore.AbstractEntityStoreTest;

public class MongoDBEntityStoreTest extends AbstractEntityStoreTest<MongoDBStoredEntity> {

	// private final String MONGODB_HOST = "ds043962.mongolab.com";	
	// private final int MONGODB_PORT = 43962;
	// private final String MONGODB_USERNAME = "idyle3";
	// private final String MONGODB_PASSWORD = "idyle3";
	private final String DATABASE_NAME = "mtnfogweb";
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