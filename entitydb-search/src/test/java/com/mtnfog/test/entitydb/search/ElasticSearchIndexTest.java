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
package com.mtnfog.test.entitydb.search;

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
import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.search.ElasticSearchIndex;
import com.mtnfog.entitydb.search.EmbeddedElasticsearchServer;
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