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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.test.entitydb.queues;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.audit.FileAuditLogger;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import com.mtnfog.entitydb.metrics.DefaultMetricReporter;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.test.entity.utils.EntityUtils;

public abstract class AbstractQueueTest {

	protected QueueConsumer consumer;
	protected QueuePublisher publisher;
	
	protected EntityStore<RdbmsStoredEntity> entityStore;
	protected List<RulesEngine> rulesEngines;
	protected AuditLogger auditLogger;
	protected ConcurrentLinkedQueue<IndexedEntity> indexerCache;
	
	protected MetricReporter metricReporter;
		
	private final String jdbcDriver = "org.hsqldb.jdbcDriver";
	private final String jdbcDialect = "org.hibernate.dialect.HSQLDialect";
	private final String jdbcUsername = "sa";
	private final String jdbcPassword = "";
	private final String schemaExport = "create-drop";
	
	@Before
	public void before() throws Exception {
		
		entityStore = new RdbmsEntityStore(getJdbcUrl(), jdbcDriver, jdbcUsername, jdbcPassword, jdbcDialect, schemaExport);
		rulesEngines = new LinkedList<RulesEngine>();
		auditLogger = new FileAuditLogger("junit");
		
		metricReporter = new DefaultMetricReporter();
		indexerCache = new ConcurrentLinkedQueue<IndexedEntity>();
		
	}
	
	@After
	public void after() throws Exception {
		
		consumer.shutdown();
		entityStore.close();
		auditLogger.close();
		
	}
	
	@Test
	public void queueAndConsumeIngest() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
		// Stored random number of entities.
		
		Collection<Entity> entities = EntityUtils.createRandomPersonEntities();
		String acl = "::1";
		String apiKey = "asdf1234";
		
		publisher.queueIngest(entities, acl, apiKey);
		
		consumer.consume();
		
		assertEquals(entities.size(), entityStore.getEntityCount());
		
	}
	
	@Test
	public void queueAndConsumeDuplicateIngest() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
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
	
	@Test
	public void queueAndConsumeAclUpdate() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
		// Stored random number of entities.
		
		Collection<Entity> entities = EntityUtils.createRandomPersonEntities();
		final String acl = "::1";
		final String apiKey = "asdf1234";
		final String originalEntityId = EntityIdGenerator.generateEntityId(entities.iterator().next(), acl);
		
		publisher.queueIngest(entities, acl, apiKey);
		
		consumer.consume();
		
		assertEquals(entities.size(), entityStore.getEntityCount());
		
		RdbmsStoredEntity storedEntity = entityStore.getEntityById(originalEntityId);
		
		assertEquals(acl, storedEntity.getAcl());
		
		// Make an update to an entity's ACL.
		
		final String newAcl = "::0";
		final String entityId = EntityIdGenerator.generateEntityId(entities.iterator().next(), newAcl);
		
		publisher.queueUpdateAcl(originalEntityId, newAcl, apiKey);
		
		consumer.consume();
		
		// Verify that the entity's ACL was updated.
		
		RdbmsStoredEntity updatedEntity = entityStore.getEntityById(entityId);
		
		assertNotNull(updatedEntity);
		assertEquals(newAcl, updatedEntity.getAcl());
		
		// Verify the original entity is no longer visible.
		storedEntity = entityStore.getEntityById(originalEntityId);
		
		assertNotNull(storedEntity);
		assertEquals(0, storedEntity.getVisible());
		
	}
	
	private String getJdbcUrl() {
		
		// Use a random database name for each test.
		String databaseName = RandomStringUtils.randomAlphabetic(10);
		
		return "jdbc:hsqldb:mem:" + databaseName;
		
	}

}
