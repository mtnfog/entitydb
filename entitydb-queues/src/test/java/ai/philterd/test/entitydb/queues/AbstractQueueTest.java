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
package ai.philterd.test.entitydb.queues;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.audit.FileAuditLogger;
import ai.philterd.entitydb.entitystore.rdbms.RdbmsEntityStore;
import ai.philterd.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import ai.philterd.entitydb.metrics.DefaultMetricReporter;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityPublisherException;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.search.IndexedEntity;
import com.mtnfog.test.entity.utils.RandomEntityUtils;

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
		
		Collection<Entity> entities = RandomEntityUtils.createRandomPersonEntities();
		String acl = "::1";
		String apiKey = "asdf1234";
		
		publisher.queueIngest(entities, acl, apiKey);
		
		consumer.consume();
		
		assertEquals(entities.size(), entityStore.getEntityCount());
		
	}
	
	@Test
	public void queueAndConsumeDuplicateIngest() throws MalformedAclException, EntityPublisherException, EntityStoreException {
		
		// Duplicate entities so only 1 should be stored.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
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
		
		Collection<Entity> entities = RandomEntityUtils.createRandomPersonEntities();
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
