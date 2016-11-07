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
package com.mtnfog.test.entitydb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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

import com.mtnfog.entitydb.audit.FileAuditLogger;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import com.mtnfog.entitydb.metrics.DefaultMetricReporter;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.queues.consumers.InternalQueueConsumer;
import com.mtnfog.entitydb.queues.publishers.InternalQueuePublisher;
import com.mtnfog.entitydb.search.ElasticSearchIndex;
import com.mtnfog.entitydb.search.EmbeddedElasticsearchServer;
import com.mtnfog.entitydb.services.DefaultEntityQueryService;

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
	public void test() {

		QueryResult result = entityQueryService.eql("select * from entities", "1234", 0, 90);
		
		assertNotNull(result.getQueryId());
		assertEquals(0, result.getEntities().size());

	}

	@Test(expected = UnauthorizedException.class)
	public void test2() {

		entityQueryService.eql("select * from entities", "asdf", 0, 90);

	}

	public static String getSettings() throws IOException, URISyntaxException {
		
		return FileUtils.readFileToString(new File(DefaultEntityQueryServiceTest.class.getResource("/mapping.json").toURI()));
		
	}

	@Configuration
	@ComponentScan(basePackages = "com.mtnfog.entitydb")
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

			return new FileAuditLogger();

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

			return new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getEntityQueryService(), getMetricReporter(), 5, getIndexerCache());

		}

		@Bean
		public List<RulesEngine> getRulesEngines() {

			List<RulesEngine> rulesEngines = new LinkedList<RulesEngine>();

			return rulesEngines;

		}

	}

}