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
package ai.philterd.test.entitydb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import ai.philterd.entitydb.search.EmbeddedElasticsearchServer;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ai.philterd.entitydb.audit.FileAuditLogger;
import ai.philterd.entitydb.entitystore.rdbms.RdbmsEntityStore;
import ai.philterd.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import ai.philterd.entitydb.metrics.DefaultMetricReporter;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.MalformedQueryException;
import ai.philterd.entitydb.model.exceptions.QueryExecutionException;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.model.services.EntityQueryService;
import ai.philterd.entitydb.queues.consumers.InternalQueueConsumer;
import ai.philterd.entitydb.queues.publishers.InternalQueuePublisher;
import ai.philterd.entitydb.search.ElasticSearchIndex;
import ai.philterd.entitydb.services.DefaultEntityQueryService;

@Ignore
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultEntityQueryServiceTest {

	@Autowired
	private DefaultEntityQueryService entityQueryService;
	
	private EmbeddedElasticsearchServer server;
	
	@Before
	public void before() throws IOException {
	
		server = new EmbeddedElasticsearchServer();
		
	}
	
	@After
	public void after() {
		
		server.close();
		
	}
	
	@Test
	public void test() throws MalformedQueryException, QueryExecutionException {

		QueryResult result = entityQueryService.eql("select * from entities", "1234", 0, 90);
		
		assertNotNull(result.getQueryId());
		assertEquals(0, result.getEntities().size());

	}

	public static String getSettings() throws IOException, URISyntaxException {
		
		return FileUtils.readFileToString(new File(DefaultEntityQueryServiceTest.class.getResource("/mapping.json").toURI()));
		
	}

	@Configuration
	@ComponentScan(basePackages = "ai.philterd.entitydb")
	static class ContextConfiguration {
		
		@Bean
		public MetricReporter getMetricReporter() {
			
			return new DefaultMetricReporter();
			
		}
		
		@Bean
		public EntityQueryService getEntityQueryService() {
			
			// TODO: Fix these tests.
			return null;
			
		}

		@Bean
		public AuditLogger getAuditLogger() throws IOException {

			return new FileAuditLogger("junit");

		}

		@Bean
		public SearchIndex getSearchIndex() throws IOException, URISyntaxException {

			return new ElasticSearchIndex("http://localhost:9200");

		}

		@Bean
		public QueuePublisher getQueuePublisher() {

			return new InternalQueuePublisher(getMetricReporter());

		}

		@Bean
		public EntityStore<RdbmsStoredEntity> getEntityStore() {

			return RdbmsEntityStore.createHypersonicEntityStore("jdbc:hsqldb:mem:entitydb", "sa", "", "create-drop");

		}
		
		@Bean
		public ConcurrentLinkedQueue<IndexedEntity> getIndexerCache() {

			return new ConcurrentLinkedQueue<IndexedEntity>();
			
		}

		@Bean
		public QueueConsumer getQueueConsumer() throws IOException, URISyntaxException {

			return new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), getIndexerCache());

		}

		@Bean
		public List<RulesEngine> getRulesEngines() {

			List<RulesEngine> rulesEngines = new LinkedList<RulesEngine>();

			return rulesEngines;

		}

	}

}