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
package ai.philterd.test.entitydb.store.rdbms;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.entitystore.rdbms.RdbmsEntityStore;
import ai.philterd.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.testing.AbstractEntityStoreTest;
import com.mtnfog.entitydb.eql.model.EntityOrder;
import com.mtnfog.entitydb.eql.model.EntityQuery;

public class RdbmsEntityStoreTest extends AbstractEntityStoreTest<RdbmsStoredEntity> {

	private final String jdbcDriver = "org.hsqldb.jdbcDriver";
	private final String jdbcDialect = "org.hibernate.dialect.HSQLDialect";
	private final String jdbcUsername = "sa";
	private final String jdbcPassword = "";
	private final String schemaExport = "create-drop";
	
	/*
	private String jdbcDriver = "com.mysql.jdbc.Driver";
	private String jdbcDialect = "org.hibernate.dialect.MySQLInnoDBDialect";
	private String jdbcUsername = "root";
	private String jdbcPassword = "root";
	private String schemaExport = "validate";
	*/
	
	private String getJdbcUrl() {
		
		// Use a random database name for each test.
		String databaseName = RandomStringUtils.randomAlphabetic(10);
		
		return "jdbc:hsqldb:mem:" + databaseName;
		
		// To use a MariaDB / MySQL database for the tests:
		// return "jdbc:mariadb://localhost/entitydb";
		
	}
	
	public EntityStore<RdbmsStoredEntity> getEntityStore() {
		return new RdbmsEntityStore(getJdbcUrl(), jdbcDriver, jdbcUsername, jdbcPassword, jdbcDialect, schemaExport);
	}
	
	@Test
	public void queryWildcardEntities() throws EntityStoreException {

		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		
		Entity entity2 = new Entity();
		entity2.setText("United States");
		entity2.setConfidence(85);
		entity2.setType("place");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setEntityOrder(EntityOrder.TEXT);
		entityQuery.setText("George*");
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(1, queryResult.getEntities().size());
		
	}
	
}