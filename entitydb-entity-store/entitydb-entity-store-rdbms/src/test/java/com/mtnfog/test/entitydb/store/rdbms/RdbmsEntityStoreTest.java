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
package com.mtnfog.test.entitydb.store.rdbms;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.testing.AbstractEntityStoreTest;
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