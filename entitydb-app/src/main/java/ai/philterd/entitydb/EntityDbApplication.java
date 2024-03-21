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
package ai.philterd.entitydb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import ai.philterd.entitydb.audit.FileAuditLogger;
import ai.philterd.entitydb.audit.FluentdAuditLogger;
import ai.philterd.entitydb.caching.memcached.MemcachedCache;
import ai.philterd.entitydb.caching.memcached.MemcachedCacheManager;
import ai.philterd.entitydb.configuration.EntityDbProperties;
import ai.philterd.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import ai.philterd.entitydb.entitystore.mongodb.MongoDBEntityStore;
import ai.philterd.entitydb.entitystore.rdbms.RdbmsEntityStore;
import ai.philterd.entitydb.metrics.CloudWatchMetricReporter;
import ai.philterd.entitydb.metrics.DefaultMetricReporter;
import ai.philterd.entitydb.metrics.InfluxDbMetricReporter;
import ai.philterd.entitydb.metrics.utils.MetricUtils;
import ai.philterd.entitydb.model.audit.AuditLogger;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.queue.QueuePublisher;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.Indexer;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.queues.consumers.ActiveMQQueueConsumer;
import ai.philterd.entitydb.queues.consumers.InternalQueueConsumer;
import ai.philterd.entitydb.queues.consumers.SqsQueueConsumer;
import ai.philterd.entitydb.queues.publishers.ActiveMQQueuePublisher;
import ai.philterd.entitydb.queues.publishers.InternalQueuePublisher;
import ai.philterd.entitydb.queues.publishers.SqsQueuePublisher;
import ai.philterd.entitydb.rulesengine.drools.DroolsRulesEngine;
import ai.philterd.entitydb.rulesengine.xml.XmlRulesEngine;
import ai.philterd.entitydb.search.ElasticSearchIndex;
import ai.philterd.entitydb.search.EmbeddedElasticsearchServer;
import ai.philterd.entitydb.search.indexer.ElasticSearchIndexer;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;

import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.rulesengine.RulesEngineException;

// Auto-configuration for MongoDB: http://stackoverflow.com/a/34415014/1428388
// Auto-configuration for Jackson: http://www.leveluplunch.com/java/tutorials/023-configure-integrate-gson-spring-boot/

/**
 * The EntityDB application. EntityDB uses Spring Boot and
 * builds as a runnable jar.
 * 
 * @author Philterd, LLC
 *
 */
@SpringBootApplication(exclude = { JacksonAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@PropertySource(value = {"file:entitydb.properties"}, ignoreResourceNotFound = false)
@Configuration
public class EntityDbApplication extends SpringBootServletInitializer {		

	private static final Logger LOGGER = LogManager.getLogger(EntityDbApplication.class);
		
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
	
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
	
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolExecutor getThreadPoolExecutor() {
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		
		executor.setMaximumPoolSize(16);
		executor.setCorePoolSize(8);		
	    
		return executor;
		
	}
		
	@Bean
	public Indexer getIndexer() {
		
		return new ElasticSearchIndexer(getSearchIndex(), getEntityStore(), getIndexerCache(), properties.getIndexerBatchSize());
		
	}
		
	@Bean
	public AuditLogger getAuditLogger() {
		
		AuditLogger auditLogger = null;
		
		if(properties.isAuditEnabled()) {
		
			final String auditLoggerType = properties.getAuditLogger();
			
			try {
			
				if("tempfile".equalsIgnoreCase(auditLoggerType)) {
					
					auditLogger = new FileAuditLogger(getSystemId());
									
				} else if("fluentd".equalsIgnoreCase(auditLoggerType)) {
				
					auditLogger = new FluentdAuditLogger(getSystemId());
					
				} else {
					
					LOGGER.warn("Invalid value for audit logger.");
					auditLogger = new FileAuditLogger(getSystemId());
					
				}
				
			} catch (IOException ex) {
				
				LOGGER.error("Unable to initialize audit logger.", ex);
				
			}
			
		} else {
			
			LOGGER.info("Auditing is disabled. Audit events will be directed to a temporary file and discarded.");
			auditLogger = new FluentdAuditLogger(getSystemId());
			
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
			
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getSqsAccessKey(), properties.getSqsSecretKey(), properties.getSqsVisibilityTimeout(), getIndexerCache());
				
			} else {
				
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getSqsVisibilityTimeout(), getIndexerCache());
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.ACTIVEMQ, queue)) {
			
			LOGGER.info("Using ActiveMQ queue.");
						
			try {
			
				queueConsumer = new ActiveMQQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), properties.getActiveMQBrokerUrl(), properties.getActiveMQQueueName(), properties.getActiveMQBrokerTimeout(), getIndexerCache());
				
			} catch (Exception ex) {
				
				LOGGER.error("Unable to initialize ActiveMQ queue consumer.");
				
			}
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.INTERNAL, queue)) {
			
			LOGGER.info("Using internal queue.");
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), getIndexerCache());
			
		} else {
			
			LOGGER.warn("Invalid queue {}. Using the internal queue.", queue);
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), getMetricReporter(), getIndexerCache());
			
		}
		
		return queueConsumer;			
		
	}
		
	@Bean
	public MetricReporter getMetricReporter() {
	
		
		if(StringUtils.equalsIgnoreCase(EntityDbProperties.INFLUXDB, properties.getMetricsProvider())) {
			
			LOGGER.info("Using InfluxDB at {} and database {}.", properties.getInfluxDbDatabase(), properties.getInfluxDbDatabase());
			
			return new InfluxDbMetricReporter(properties.getInfluxDbEndpoint(), properties.getInfluxDbDatabase(), properties.getInfluxDbUsername(), properties.getInfluxDbPassword());
			
		} else if(StringUtils.equalsIgnoreCase(EntityDbProperties.CLOUDWATCH, properties.getMetricsProvider())) {
										
			if(StringUtils.isNotEmpty(properties.getCloudWatchAccessKey())) {
			
				return new CloudWatchMetricReporter(getSystemId(), properties.getCloudWatchNamespace(), 
					properties.getCloudWatchAccessKey(), properties.getCloudWatchSecretKey(), properties.getCloudWatchEndpoint());
			
			} else {
				
				return new CloudWatchMetricReporter(getSystemId(), properties.getCloudWatchNamespace(), properties.getCloudWatchEndpoint());
				
			}
			
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
	public ConcurrentLinkedQueue<IndexedEntity> getIndexerCache() {

		return new ConcurrentLinkedQueue<IndexedEntity>();
		
	}
	
	@Bean(destroyMethod="shutdown")
	public CacheManager cacheManager() {
		
		LOGGER.info("Creating cache manager.");
		
		List<String> cacheNames = new LinkedList<String>();
		
		cacheNames.add("nonExpiredContinuousQueries");
		cacheNames.add("continuousQueriesByUser");
		cacheNames.add("indexer");
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
	
	private String getSystemId() {
		
		String systemId = properties.getSystemId();
		
		if(StringUtils.isEmpty(systemId)) {
			
			systemId = MetricUtils.getSystemId();
			
		}
		
		return systemId;
		
	}

}