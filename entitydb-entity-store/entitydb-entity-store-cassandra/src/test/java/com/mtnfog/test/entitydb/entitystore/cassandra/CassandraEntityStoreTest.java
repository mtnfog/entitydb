/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
package com.mtnfog.test.entitydb.entitystore.cassandra;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.datastax.driver.core.Session;
import com.mtnfog.entitydb.entitystore.cassandra.CassandraEntityStore;
import com.mtnfog.entitydb.entitystore.cassandra.model.CassandraStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.testing.AbstractEntityStoreTest;

public class CassandraEntityStoreTest extends AbstractEntityStoreTest<CassandraStoredEntity> {

	private static final String KEYSPACE = "entitydb";
	
	@Rule
	public CassandraCQLUnit embeddedCassandra = new CassandraCQLUnit(new ClassPathCQLDataSet("entitydb.cql", KEYSPACE), null, 120000, 120000);
		
	@Override
	public EntityStore<CassandraStoredEntity> getEntityStore() throws EntityStoreException {

		Session session = embeddedCassandra.getSession();
		CassandraEntityStore cassandraEntityStore = new CassandraEntityStore(session, KEYSPACE);
				
		return cassandraEntityStore;
		
	}
	
	@Override
	@Test
	@Ignore
	public void getContexts() throws EntityStoreException {
		// CassandraEntityStore doesn't support this yet.
	}
	
}