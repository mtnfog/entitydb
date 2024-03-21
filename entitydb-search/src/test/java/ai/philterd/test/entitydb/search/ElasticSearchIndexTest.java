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
package ai.philterd.test.entitydb.search;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.search.ElasticSearchIndex;
import ai.philterd.entitydb.search.EmbeddedElasticsearchServer;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.model.EntityQuery;
import com.mtnfog.test.entity.utils.RandomEntityUtils;

public class ElasticSearchIndexTest {
	
	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchIndexTest.class);
	
	private static final String ELASTICSEARCH_HOST = "http://localhost:9200";
	//private static final String ELASTICSEARCH_HOST = "http://search-base-elasticse-1n4w19rokqngi-v2h45ltxpsvziussef7afzipbm.us-east-1.es.amazonaws.com";
	
	private EmbeddedElasticsearchServer server;
	
	@Before
	public void before() throws IOException {
	
		server = new EmbeddedElasticsearchServer();
		server.start();
		
	}
	
	@After
	public void after() {
		
		server.close();
		
	}
	
	@Test
	public void index() throws IOException, URISyntaxException, MalformedAclException {
		
		Entity entity = RandomEntityUtils.createRandomPersonEntity();
		entity.setContext("context");
		entity.setDocumentId("document");
		
		IndexedEntity indexedEntity = IndexedEntity.fromEntity(entity, UUID.randomUUID().toString(), "user:group:1");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		boolean result = elasticSearchIndex.index(indexedEntity);
		
		assertTrue(result);
		
	}
	
	@Test
	public void getEntity1() throws IOException, URISyntaxException, MalformedAclException, InterruptedException {
		
		Entity entity = RandomEntityUtils.createRandomPersonEntity();
		entity.setContext("context");
		entity.setDocumentId("document");
		
		IndexedEntity indexedEntity = IndexedEntity.fromEntity(entity, UUID.randomUUID().toString(), "user:group:1");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		boolean result = elasticSearchIndex.index(indexedEntity);
						
		assertTrue(result);
		
		final String entityId = indexedEntity.getEntityId();
		
		IndexedEntity retrievedEntity = elasticSearchIndex.getEntity(entityId);
		
		assertNotNull(retrievedEntity);
		assertEquals(indexedEntity.getEntityId(), retrievedEntity.getEntityId());
		assertEquals(indexedEntity.getText(), retrievedEntity.getText());
		
	}
	
	@Test
	public void queryForIndexedEntities1() throws Exception {
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("john");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "user:group:1");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "user:group:1");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities where text = \"john\" and type = \"place\"";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
				
		assertNotNull(indexedEntities);
		
		for(IndexedEntity indexedEntity : indexedEntities) {
			
			LOGGER.info(indexedEntity.toString());
			
		}
		
		assertEquals(1, indexedEntities.size());
		
	}
	
	@Test
	public void queryForIndexedEntities2() throws Exception {
		
		// Only one entity is visible to the user.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "user:group:1");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:notgroup:0");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		for(IndexedEntity indexedEntity : indexedEntities) {			
			LOGGER.info(indexedEntity.toString());			
		}
		
		assertEquals(1, indexedEntities.size());
		assertEquals("john", indexedEntities.get(0).getText());
		
	}
	
	@Test
	public void queryForIndexedEntities3() throws Exception {
		
		// Neither entity is visible to the user.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "notuser:notgroup:0");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:notgroup:0");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		assertEquals(0, indexedEntities.size());
		
	}
	
	@Test
	public void queryForIndexedEntities4() throws Exception {
		
		// Entities' user/groups don't match but they're visible to the world.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "notuser:notgroup:1");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:notgroup:1");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		assertEquals(2, indexedEntities.size());
		
	}
	
	@Test
	public void queryForIndexedEntities5() throws Exception {
		
		// One entity has a group that matches the user's groups.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "notuser:g1:1");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:notgroup:0");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		assertEquals(1, indexedEntities.size());
		
	}
	
	@Test
	public void queryForIndexedEntities6() throws Exception {
		
		// UserEntity has multiple groups.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "notuser:g1:1");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:notgroup:0");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		groups.add("g2");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		assertEquals(1, indexedEntities.size());
		
	}
	
	@Test
	public void queryForIndexedEntities7() throws Exception {
		
		// UserEntity and entities have multiple groups.
		
		Entity entity1 = RandomEntityUtils.createRandomPersonEntity();
		entity1.setText("john");
		entity1.setType("person");
		
		Entity entity2 = RandomEntityUtils.createRandomPersonEntity();
		entity2.setText("morgantown");
		entity2.setType("place");
		
		IndexedEntity indexedEntity1 = IndexedEntity.fromEntity(entity1, UUID.randomUUID().toString(), "notuser:g5,g6,g7:0");
		IndexedEntity indexedEntity2 = IndexedEntity.fromEntity(entity2, UUID.randomUUID().toString(), "notuser:g1,g2,g3:0");
		
		ElasticSearchIndex elasticSearchIndex = new ElasticSearchIndex(ELASTICSEARCH_HOST);
		assertTrue(elasticSearchIndex.index(indexedEntity1));
		assertTrue(elasticSearchIndex.index(indexedEntity2));
					
		// Let things be indexed.
		Thread.sleep(1500);
		
		String eql = "select * from entities";
		EntityQuery entityQuery = Eql.generate(eql);
		
		Set<String> groups = new HashSet<String>();
		groups.add("g1");
		groups.add("g2");
		
		User user = new User(1, "user", "user@test-fake.com", "555-555-5555", "apikey", groups);
		
		List<IndexedEntity> indexedEntities = elasticSearchIndex.queryForIndexedEntities(entityQuery, user);
		
		assertEquals(1, indexedEntities.size());
		
	}

}