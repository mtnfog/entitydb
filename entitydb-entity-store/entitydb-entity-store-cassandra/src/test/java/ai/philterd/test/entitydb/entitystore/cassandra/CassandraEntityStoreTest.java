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
package ai.philterd.test.entitydb.entitystore.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.datastax.driver.core.Session;
import ai.philterd.entitydb.entitystore.cassandra.CassandraEntityStore;
import ai.philterd.entitydb.entitystore.cassandra.model.CassandraStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.testing.AbstractEntityStoreTest;

public class CassandraEntityStoreTest extends AbstractEntityStoreTest<CassandraStoredEntity> {

	private static final String KEYSPACE = "entitydb";
	
	@Rule
	public CassandraCQLUnit embeddedCassandra = new CassandraCQLUnit(new ClassPathCQLDataSet("entitydb.cql", KEYSPACE), null, 120000, 120000);
		
	@Override
	public EntityStore<CassandraStoredEntity> getEntityStore() {

		final CqlSession session = embeddedCassandra.getSession();

        return new CassandraEntityStore(session, KEYSPACE);
		
	}
	
	@Override
	@Test
	@Ignore
	public void getContexts() throws EntityStoreException {
		// CassandraEntityStore doesn't support this yet.
	}
	
}