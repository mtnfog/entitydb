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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtnfog.commons.caching.IdylCache;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.audit.FileAuditLogger;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.test.entity.utils.EntityUtils;

public abstract class AbstractQueueTest {

	protected QueueConsumer consumer;
	protected QueuePublisher publisher;
	
	protected EntityStore<RdbmsStoredEntity> entityStore;
	protected List<RulesEngine> rulesEngines;
	protected IdylCache idylCache;
	protected AuditLogger auditLogger;
		
	private final String jdbcDriver = "org.hsqldb.jdbcDriver";
	private final String jdbcDialect = "org.hibernate.dialect.HSQLDialect";
	private final String jdbcUsername = "sa";
	private final String jdbcPassword = "";
	private final String schemaExport = "create-drop";
	
	protected int sleepSeconds = 1;
	
	@Before
	public void before() throws Exception {
		
		entityStore = new RdbmsEntityStore(getJdbcUrl(), jdbcDriver, jdbcUsername, jdbcPassword, jdbcDialect, schemaExport);
		rulesEngines = new LinkedList<RulesEngine>();
		auditLogger = new FileAuditLogger();
		
	}
	
	@After
	public void after() throws Exception {
		
		consumer.shutdown();
		entityStore.close();
		auditLogger.close();
		
	}
	
	@Test
	public void test1() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
		// Stored random number of entities.
		
		Collection<Entity> entities = EntityUtils.createRandomPersonEntities();
		String acl = "::1";
		String apiKey = "asdf1234";
		
		publisher.queueIngest(entities, acl, apiKey);
		
		consumer.consume();
		
		assertEquals(entities.size(), entityStore.getEntityCount());
		
	}
	
	@Test
	public void test2() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
		// Duplicate entities so only 1 should be stored.
		
		Entity entity1 = EntityUtils.createRandomPersonEntity();
		String acl = "::1";
		String apiKey = "asdf1234";
		
		Collection<Entity> entities = new LinkedList<Entity>();
		entities.add(entity1);
		entities.add(entity1);
		
		publisher.queueIngest(entities, acl, apiKey);
		
		consumer.consume();
		
		assertEquals(1, entityStore.getEntityCount());
		
	}
	
	private String getJdbcUrl() {
		
		// Use a random database name for each test.
		String databaseName = RandomStringUtils.randomAlphabetic(10);
		
		return "jdbc:hsqldb:mem:" + databaseName;
		
	}

}
