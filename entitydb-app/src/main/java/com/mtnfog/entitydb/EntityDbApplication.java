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
package com.mtnfog.entitydb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import com.mtnfog.entitydb.audit.FileAuditLogger;
import com.mtnfog.entitydb.audit.FluentdAuditLogger;
import com.mtnfog.entitydb.caching.memcached.MemcachedCache;
import com.mtnfog.entitydb.caching.memcached.MemcachedCacheManager;
import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import com.mtnfog.entitydb.entitystore.mongodb.MongoDBEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.metrics.CloudWatchMetricReporter;
import com.mtnfog.entitydb.metrics.DefaultMetricReporter;
import com.mtnfog.entitydb.metrics.InfluxDbMetricReporter;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.queues.consumers.ActiveMQQueueConsumer;
import com.mtnfog.entitydb.queues.consumers.InternalQueueConsumer;
import com.mtnfog.entitydb.queues.consumers.SqsQueueConsumer;
import com.mtnfog.entitydb.queues.publishers.ActiveMQQueuePublisher;
import com.mtnfog.entitydb.queues.publishers.InternalQueuePublisher;
import com.mtnfog.entitydb.queues.publishers.SqsQueuePublisher;
import com.mtnfog.entitydb.rulesengine.drools.DroolsRulesEngine;
import com.mtnfog.entitydb.rulesengine.xml.XmlRulesEngine;
import com.mtnfog.entitydb.search.ElasticSearchIndex;
import com.mtnfog.entitydb.search.EmbeddedElasticsearchServer;
import com.mtnfog.entitydb.search.indexer.ElasticSearchIndexer;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;

import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.rulesengine.RulesEngineException;

// Auto-configuration for MongoDB: http://stackoverflow.com/a/34415014/1428388
// Auto-configuration for Jackson: http://www.leveluplunch.com/java/tutorials/023-configure-integrate-gson-spring-boot/

/**
 * The EntityDB application. EntityDB uses Spring Boot and
 * builds as a runnable jar.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@SpringBootApplication(exclude = { JacksonAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@PropertySource(value = {"file:entitydb.properties"}, ignoreResourceNotFound = false)
@Configuration
public class EntityDbApplication extends SpringBootServletInitializer {		

	private static final Logger LOGGER = LogManager.getLogger(EntityDbApplication.class);
		
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
			
	@Autowired
	private EntityQueryService entityQueryService;
	
	/**
	 * The EntityDB main function.
	 * @param args Command line arguments. (None are required.)
	 */
	public static void main(String[] args) {
						
		// Start the REST service.
		SpringApplication.run(EntityDbApplication.class, args);

	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		return application.sources(EntityDbApplication.class);
		
	}
		
	@Bean
	public Indexer getIndexer() {
		
		return new ElasticSearchIndexer(getSearchIndex(), getEntityStore());
		
	}
		
	@Bean
	public AuditLogger getAuditLogger() {
		
		AuditLogger auditLogger = null;
		
		if(properties.isAuditEnabled()) {
		
			final String auditLoggerType = properties.getAuditLogger();
			
			try {
			
				if("tempfile".equalsIgnoreCase(auditLoggerType)) {
					
					auditLogger = new FileAuditLogger();
									
				} else if("fluentd".equalsIgnoreCase(auditLoggerType)) {
				
					auditLogger = new FluentdAuditLogger();
					
				} else {
					
					LOGGER.warn("Invalid value for audit logger.");
					auditLogger = new FileAuditLogger();
					
				}
				
			} catch (IOException ex) {
				
				LOGGER.error("Unable to initialize audit logger.", ex);
				
			}
			
		} else {
			
			LOGGER.info("Auditing is disabled. Audit events will be directed to a temporary file and discarded.");
			auditLogger = new FluentdAuditLogger();
			
		}
		
		return auditLogger;
		
	}
			
	@Bean
	public EntityStore<?> getEntityStore() {
		
		EntityStore<?> entityStore = null;
		
		final String entitydb = properties.getDatabase();
		
		LOGGER.info("Using database: {}", entitydb);
		
		try {
		
			if(StringUtils.equalsIgnoreCase(EntityDbProperties.MYSQL, entitydb)) {
								
				entityStore = RdbmsEntityStore.createMySQL5EntityStore(properties.getMySqlJdbcURL(), properties.getMySqlUsername(), properties.getMySqlPassword(), "validate");
				
			} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.DYNAMODB, entitydb)) {
				
				if(StringUtils.isNotEmpty(properties.getDynamoDBAccessKey())) {
								
					entityStore = new DynamoDBEntityStore(properties.getDynamoDBAccessKey(), properties.getDynamoDBSecretKey(), properties.getDynamoDBEndpoint(), properties.getDynamoDBTable());			
					
				} else {
					
					entityStore = new DynamoDBEntityStore(properties.getDynamoDBEndpoint(), properties.getDynamoDBTable());
					
				}
				
			} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.MONGODB, entitydb)) {
				
				final String mongoDbHost = properties.getMongoDBHost();
				final int mongoDbPort = properties.getMongoDBPort();
				final String mongoDbDatabase = properties.getMongoDBDatabase();
				final String mongoDbCollection = properties.getMongoDBCollection();
				final String mongoDbUsername = properties.getMongoDBUsername();
				final String mongoDbPassword = properties.getMongoDBPassword();
				
				if(StringUtils.isEmpty(mongoDbUsername)) {
				
					entityStore = new MongoDBEntityStore(mongoDbHost, mongoDbPort, mongoDbDatabase, mongoDbCollection);
					
				} else {
					
					entityStore = new MongoDBEntityStore(mongoDbHost, mongoDbPort, mongoDbUsername, mongoDbPassword, mongoDbDatabase, mongoDbCollection);
					
				}
				
			} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.INTERNAL, entitydb)) {
				
				LOGGER.warn("A temporary, internal entity store will be used. Its contents are not retained across restarts.");
				
				entityStore = RdbmsEntityStore.createHypersonicEntityStore("jdbc:hsqldb:mem:entitydb-entity-store", "sa", "", "create-drop");
				
			} else {
				
				LOGGER.warn("Invalid database selection: {}", entitydb);
				LOGGER.warn("A temporary, internal entity store will be used. Its contents are not retained across restarts.");
				
				// Use an internal database.
				entityStore = RdbmsEntityStore.createHypersonicEntityStore("jdbc:hsqldb:mem:entitydb", "sa", "", "create-drop");
				
			}
		
		} catch (Exception ex) {
			
			LOGGER.error("Unable to connect to entity store. Please check your credentials and connection information.", ex);
			
		}
		
		return entityStore;
		
	}
	
	@Bean
	public SearchIndex getSearchIndex() {
		
		SearchIndex searchIndex = null;
		
		try {
		
			if(StringUtils.equalsIgnoreCase(EntityDbProperties.INTERNAL, properties.getSearchIndexProvider())) {
				
				EmbeddedElasticsearchServer embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
				embeddedElasticsearchServer.start();
				
				LOGGER.warn("Using the internal search index is not recommended for production systems.");
							
				searchIndex = new ElasticSearchIndex("http://localhost:9200/");
				
			} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.ELASTICSEARCH, properties.getSearchIndexProvider())) {
			
				if(StringUtils.isEmpty(properties.getElasticsearchUsername())) {
					
					searchIndex = new ElasticSearchIndex(properties.getElasticsearchHost());
					
				} else {
					
					searchIndex = new ElasticSearchIndex(properties.getElasticsearchHost(), properties.getElasticsearchUsername(), properties.getElasticsearchPassword());
					
				}
				
			} else {
				
				LOGGER.warn("Invalid search index: {}", properties.getSearchIndexProvider());
				LOGGER.warn("Using the internal search index is not recommended for production systems.");
				
			}
			
		} catch (IOException ex) {
			
			LOGGER.error("Unable to configure Elasticsearch client.", ex);
			
		}
									
		return searchIndex;
		
	}
	
	@Bean
	public List<RulesEngine> getRulesEngines() {
		
		List<RulesEngine> rulesEngines = new LinkedList<RulesEngine>();
		
		if(properties.isRulesEngineEnabled()) {
		
			final String rulesDirectory = properties.getRulesDirectory();
				
			try {
				
				rulesEngines.add(new DroolsRulesEngine(rulesDirectory));			
				rulesEngines.add(new XmlRulesEngine(rulesDirectory));
				
			} catch (RulesEngineException ex) {
				
				LOGGER.error("Unable to initialize the rules engine.", ex);
				
			}
		
		} else {
			
			LOGGER.info("The rules engine is disabled.");
			
		}
		
		return rulesEngines;
		
	}
	
	@Bean
	public QueuePublisher getQueuePublisher() {
		
		QueuePublisher queuePublisher = null;
		
		String queue = properties.getQueueProvider();
		
		if(StringUtils.equalsIgnoreCase(EntityDbProperties.SQS, queue)) {
						
			LOGGER.info("Using SQS queue: {}", properties.getSqsQueueUrl());
			
			if(StringUtils.isNotEmpty(properties.getSqsAccessKey())) {
			
				queuePublisher = new SqsQueuePublisher(properties.getSqsQueueUrl(), properties.getSqsEndpoint(), properties.getSqsAccessKey(), properties.getSqsSecretKey(), getMetricReporter());
				
			} else {
				
				queuePublisher = new SqsQueuePublisher(properties.getSqsQueueUrl(), properties.getSqsEndpoint(), getMetricReporter());
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.ACTIVEMQ, queue)) {
			
			LOGGER.info("Using ActiveMQ queue.");
						
			try {
			
				queuePublisher = new ActiveMQQueuePublisher(properties.getActiveMQBrokerUrl(), properties.getActiveMQQueueName(), getMetricReporter());
				
			} catch (Exception ex) {
				
				LOGGER.error("Unable to initialize ActiveMQ queue publisher.");
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.INTERNAL, queue)) {
			
			LOGGER.info("Using internal queue.");
			
			queuePublisher = new InternalQueuePublisher(getMetricReporter());
			
		} else {
			
			LOGGER.warn("Invalid queue {}. Using internal queue.", queue);
			
			queuePublisher = new InternalQueuePublisher(getMetricReporter());
			
		}
		
		return queuePublisher;
				
	}
		
	@Bean
	public QueueConsumer getQueueConsumer() {
		
		QueueConsumer queueConsumer = null;
		
		String queue = properties.getQueueProvider();
								
		if(StringUtils.equalsIgnoreCase(EntityDbProperties.SQS, queue)) {
									
			LOGGER.info("Using SQS queue {}.", properties.getSqsQueueUrl());
			
			if(StringUtils.isNotEmpty(properties.getSqsAccessKey())) {
			
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), entityQueryService, getMetricReporter(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getSqsAccessKey(), properties.getSqsSecretKey(), properties.getQueueConsumerSleep(), properties.getSqsVisibilityTimeout());
				
			} else {
				
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), entityQueryService, getMetricReporter(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getQueueConsumerSleep(), properties.getSqsVisibilityTimeout());
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.ACTIVEMQ, queue)) {
			
			LOGGER.info("Using ActiveMQ queue.");
						
			try {
			
				queueConsumer = new ActiveMQQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), entityQueryService, getMetricReporter(), properties.getActiveMQBrokerUrl(), properties.getActiveMQQueueName(), properties.getActiveMQBrokerTimeout());
				
			} catch (Exception ex) {
				
				LOGGER.error("Unable to initialize ActiveMQ queue consumer.");
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.INTERNAL, queue)) {
			
			LOGGER.info("Using internal queue.");
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), entityQueryService, getMetricReporter(), properties.getQueueConsumerSleep());
			
		} else {
			
			LOGGER.warn("Invalid queue {}. Using the internal queue.", queue);
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), entityQueryService, getMetricReporter(), properties.getQueueConsumerSleep());
			
		}
		
		return queueConsumer;			
		
	}
	
	@Bean
	public MetricReporter getMetricReporter() {
		
		if(StringUtils.equalsIgnoreCase(EntityDbProperties.INFLUXDB, properties.getMetricsProvider())) {
			
			LOGGER.info("Using InfluxDB at {} and database {}.", properties.getInfluxDbDatabase(), properties.getInfluxDbDatabase());
			
			return new InfluxDbMetricReporter(properties.getInfluxDbEndpoint(), properties.getInfluxDbDatabase(), properties.getInfluxDbUsername(), properties.getInfluxDbPassword());
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.CLOUDWATCH, properties.getMetricsProvider())) {
									
			return new CloudWatchMetricReporter(properties.getSystemId(), properties.getCloudWatchNamespace(), 
					properties.getCloudWatchAccessKey(), properties.getCloudWatchSecretKey(), properties.getCloudWatchEndpoint());
			
			
		} else {
			
			return new DefaultMetricReporter();

		}
		
	}
			
	@Bean
	public HttpMessageConverters customConverters() {

		Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

		GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
		messageConverters.add(gsonHttpMessageConverter);

		return new HttpMessageConverters(true, messageConverters);

	}
	
	@Bean
	public CacheManager cacheManager() {
		
		LOGGER.info("Creating cache manager.");
		
		List<String> cacheNames = new LinkedList<String>();
		
		cacheNames.add("nonExpiredContinuousQueries");
		cacheNames.add("continuousQueriesByUser");
		cacheNames.add("general");
		
		CacheManager cacheManager = null;
		
		if(StringUtils.equalsIgnoreCase(properties.getCache(), "memcached")) {
			
			try {
				
				MemcachedClient memcachedClient = new MemcachedClient(
						new ConnectionFactoryBuilder()
							.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
							.build(),
						AddrUtil.getAddresses(properties.getMemcachedHosts()));
				
				LOGGER.info("Created Memcached client for {}.", properties.getMemcachedHosts());
				
				final Collection<MemcachedCache> caches = new ArrayList<MemcachedCache>();
				
				for(String cacheName : cacheNames) {
				
					caches.add(new MemcachedCache(memcachedClient, cacheName, properties.getCacheTtl()));
					
				}
				
				return new MemcachedCacheManager(caches);
			
			} catch (IOException ex) {
				
				LOGGER.error("Unable to create memcached client.", ex);
				
			}
			
		} else {
			
			LOGGER.info("Using internal cache.");
			
			SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
			
			List<ConcurrentMapCache> caches = new LinkedList<ConcurrentMapCache>();
			
			for(String cacheName : cacheNames) {
				
				caches.add(new ConcurrentMapCache(cacheName));
				
			}
			
			simpleCacheManager.setCaches(caches);
			simpleCacheManager.afterPropertiesSet();
			
			return cacheManager;

		}
		
		return null;
		
	}

}