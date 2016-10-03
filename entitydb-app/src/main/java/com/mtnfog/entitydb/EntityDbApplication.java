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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import com.mtnfog.entitydb.audit.FileAuditLogger;
import com.mtnfog.entitydb.audit.FluentdAuditLogger;
import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import com.mtnfog.entitydb.entitystore.rdbms.RdbmsEntityStore;
import com.mtnfog.entitydb.model.audit.AuditLogger;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;
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
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.rulesengine.RulesEngineException;

// Auto-configuration for MongoDB: http://stackoverflow.com/a/34415014/1428388
// Auto-configuration for Jackson: http://www.leveluplunch.com/java/tutorials/023-configure-integrate-gson-spring-boot/

@SpringBootApplication(exclude = { JacksonAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@PropertySource(value = {"file:entitydb.properties"}, ignoreResourceNotFound = false)
public class EntityDbApplication extends SpringBootServletInitializer {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityDbApplication.class);
		
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
			
	public static void main(String[] args) throws Exception {
						
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
		
			if("mysql".equalsIgnoreCase(entitydb)) {
								
				entityStore = RdbmsEntityStore.createMySQL5EntityStore(properties.getMySqlJdbURL(), properties.getMySqlUsername(), properties.getMySqlPassword(), "validate");
				
			} else if("dynamodb".equalsIgnoreCase(entitydb)) {
				
				if(StringUtils.isNotEmpty(properties.getDynamoDBAccessKey())) {
								
					entityStore = new DynamoDBEntityStore(properties.getDynamoDBAccessKey(), properties.getDynamoDBSecretKey(), properties.getDynamoDBEndpoint(), properties.getDynamoDBTable());			
					
				} else {
					
					entityStore = new DynamoDBEntityStore(properties.getDynamoDBEndpoint(), properties.getDynamoDBTable());
					
				}
				
			} else if("internal".equalsIgnoreCase(entitydb)) {
				
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
			
			/*settings = 
				 FileUtils.readFileToString(
				  new File("mapping.json"));*/
		
			if("internal".equalsIgnoreCase(properties.getSearchIndexProvider())) {
				
				EmbeddedElasticsearchServer s = new EmbeddedElasticsearchServer();
				
				LOGGER.warn("Using the internal search index is not recommended for production systems.");
							
				searchIndex = new ElasticSearchIndex("http://localhost:9200/");
				
			} else if("elasticsearch".equalsIgnoreCase(properties.getSearchIndexProvider())) {
			
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
			
			LOGGER.error("Unable to configure Elasticsearch client.");
			
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
		
		if("sqs".equalsIgnoreCase(queue)) {
						
			LOGGER.info("Using SQS queue: {}", properties.getSqsQueueUrl());
			
			if(StringUtils.isNotEmpty(properties.getSqsAccessKey())) {
			
				queuePublisher = new SqsQueuePublisher(properties.getSqsQueueUrl(), properties.getSqsEndpoint(), properties.getSqsAccessKey(), properties.getSqsSecretKey());
				
			} else {
				
				queuePublisher = new SqsQueuePublisher(properties.getSqsQueueUrl(), properties.getSqsEndpoint());
				
			}
			
		} else if("activemq".equalsIgnoreCase(queue)) {
			
			LOGGER.info("Using ActiveMQ queue.");
						
			try {
			
				queuePublisher = new ActiveMQQueuePublisher(properties.getActiveMQBrokerUrl(), properties.getActiveMQQueueName());
				
			} catch (Exception ex) {
				
				LOGGER.error("Unable to initialize ActiveMQ queue publisher.");
				
			}
			
		} else if("internal".equalsIgnoreCase(queue)) {
			
			LOGGER.info("Using internal queue.");
			
			queuePublisher = new InternalQueuePublisher();
			
		} else {
			
			LOGGER.warn("Invalid queue: {}", queue);
			
		}
		
		return queuePublisher;
				
	}
		
	@Bean
	public QueueConsumer getQueueConsumer() {
		
		QueueConsumer queueConsumer = null;
		
		String queue = properties.getQueueProvider();
								
		if("sqs".equalsIgnoreCase(queue)) {
									
			LOGGER.info("Using SQS queue {}.", properties.getSqsQueueUrl());
			
			if(StringUtils.isNotEmpty(properties.getSqsAccessKey())) {
			
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getSqsAccessKey(), properties.getSqsSecretKey(), properties.getQueueConsumerSleep(), properties.getSqsVisibilityTimeout());
				
			} else {
				
				queueConsumer = new SqsQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), properties.getSqsEndpoint(), properties.getSqsQueueUrl(), properties.getQueueConsumerSleep(), properties.getSqsVisibilityTimeout());
				
			}
			
		} else if("activemq".equalsIgnoreCase(queue)) {
			
			LOGGER.info("Using ActiveMQ queue.");
						
			try {
			
				queueConsumer = new ActiveMQQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), properties.getActiveMQBrokerUrl(), properties.getActiveMQQueueName(), properties.getActiveMQBrokerTimeout());
				
			} catch (Exception ex) {
				
				LOGGER.error("Unable to initialize ActiveMQ queue consumer.");
				
			}
			
		} else if("internal".equalsIgnoreCase(queue)) {
			
			LOGGER.info("Using internal queue.");
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), properties.getQueueConsumerSleep());
			
		} else {
			
			LOGGER.warn("Invalid queue: {}", queue);
			LOGGER.warn("Defaulting to the internal queue.");
			
			queueConsumer = new InternalQueueConsumer(getEntityStore(), getRulesEngines(), getAuditLogger(), properties.getQueueConsumerSleep());
			
		}
		
		return queueConsumer;			
		
	}
			
	@Bean
	public HttpMessageConverters customConverters() {

		Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

		GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
		messageConverters.add(gsonHttpMessageConverter);

		return new HttpMessageConverters(true, messageConverters);

	}

}