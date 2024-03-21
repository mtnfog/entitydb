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
package ai.philterd.entitydb.configuration;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * User-configurable properties available through
 * the <code>entitydb.properties</code> file.
 * 
 * @author Philterd, LLC
 *
 */
@Sources("file:entitydb.properties")
public interface EntityDbProperties extends Config {
	
	String INTERNAL = "internal";
	
	String MYSQL = "mysql";
	String DYNAMODB = "dynamodb";
	String MONGODB = "mongodb";
	
	String ELASTICSEARCH = "elasticsearch";
	
	String ACTIVEMQ = "activemq";
	String SQS = "sqs";
	
	String INFLUXDB = "influxdb";
	String CLOUDWATCH = "cloudwatch";
	
	/**
	 * Gets the ID of the system.
	 * @return The ID of the system.
	 */
	@Key("system.id")
	String getSystemId();
	
	/**
	 * Gets if the indexer is enabled.
	 * @return <code>true</code> if the indexer is enabled.
	 */
	@Key("indexer.enabled")
	@DefaultValue("true")
	boolean isIndexerEnabled();
	
	/**
	 * Gets the maximum number of entities to index
	 * in a batch.
	 * @return The batch size.
	 */
	@Key("indexer.batch.size")
	@DefaultValue("25")
	int getIndexerBatchSize();
	
	/**
	 * Gets the provider of the user manager.
	 * @return The provider of the user manager. If not specified
	 * in the properties then <code>internal</code> will be returned.
	 */
	@Key("user.manager")
	@DefaultValue("internal")
	String getUserManager();
	
	/**
	 * Gets if audit is enabled.
	 * @return <code>true</code> if audit is enabled.
	 */
	@Key("audit.enabled")
	@DefaultValue("true")
	boolean isAuditEnabled();
	
	/**
	 * Gets the destination for the audit events.
	 * @return The destination for the audit events. If not specified
	 * in the properties then <code>tempfile</code> will be returned.
	 */
	@Key("audit.logger")
    @DefaultValue("tempfile")
    String getAuditLogger();
	
	/**
	 * Gets the provider of the entity store.
	 * @return The provider of the entity store. If not specified
	 * in the properties then <code>internal</code> will be returned.
	 */
	@Key("entitystore")
    @DefaultValue("internal")
    String getDatabase();
	
	/**
	 * Gets the hostname of the MongoDB server.
	 * @return The hostname of the MongoDB server.
	 */
	@Key("mongodb.host")
    String getMongoDBHost();
	
	/**
	 * Gets the port of the MongoDB server.
	 * @return The port of the MongoDB server. If not specified in
	 * the properties then <code>27017</code> will be returned.
	 */
	@Key("mongodb.port")
	@DefaultValue("27017")
    int getMongoDBPort();
	
	/**
	 * Gets the name of the MongoDB database.
	 * @return The name of the MongoDB database.
	 */
	@Key("mongodb.database")
    String getMongoDBDatabase();
	
	/**
	 * Gets the name of the MongoDB collection.
	 * @return The name of the MongoDB collection.
	 */
	@Key("mongodb.collection")
    String getMongoDBCollection();
	
	/**
	 * Gets the MongoDB username.
	 * @return The MongoDB username.
	 */
	@Key("mongodb.username")
    String getMongoDBUsername();
	
	/**
	 * Gets the MongoDB password.
	 * @return The MongoDB password.
	 */
	@Key("mongodb.password")
    String getMongoDBPassword();
	
	/**
	 * Gets the Cassandra hostname.
	 * @return The Cassandra hostname.
	 */
	@Key("cassandra.host")
    String getCassandraHost();
	
	/**
	 * Gets the Cassandra port.
	 * @return The Cassandra port.
	 */
	@Key("cassandra.port")
    int getCassandraPort();
	
	/**
	 * Gets the Cassandra keyspace.
	 * @return The Cassandra keyspace. If not specified in the properties
	 * then <code>entitydb</code> will be returned.
	 */
	@Key("cassandra.keyspace")
	@DefaultValue("entitydb")
    String getCassandraKeyspace();
	
	/**
	 * Gets the DynamoDB endpoint.
	 * @return The DynamoDB endpoint.
	 */
	@Key("dynamodb.endpoint")
    String getDynamoDBEndpoint();
	
	/**
	 * Gets the name of the DynamoDB table.
	 * @return The name of the DynamoDB table.
	 */
	@Key("dynamodb.table")
    String getDynamoDBTable();
	
	/**
	 * Gets the access key for DynamoDB.
	 * @return The access key for DynamoDB.
	 */
	@Key("dynamodb.accesskey")
    String getDynamoDBAccessKey();
	
	/**
	 * Gets the secret key for DynamoDB.
	 * @return The secret key for DynamoDB.
	 */
	@Key("dynamodb.secretkey")
    String getDynamoDBSecretKey();
	
	/**
	 * Gets the MySQL JDBC URL.
	 * @return The MySQL JDBC URL.
	 */
	@Key("mysql.jdbc.url")
    String getMySqlJdbcURL();
	
	/**
	 * Gets the MySQL username.
	 * @return The MySQL username.
	 */
	@Key("mysql.username")
    String getMySqlUsername();
	
	/**
	 * Gets the MySQL password.
	 * @return The MySQL password.
	 */
	@Key("mysql.password")
    String getMySqlPassword();
	
	/**
	 * Gets the cache provider.
	 * @return The cache provider. If not specified in the properties
	 * then <code>internal</code> will be returned.
	 */
	@Key("cache")
	@DefaultValue("internal")
    String getCache();
	
	/**
	 * Gets the memcached hosts.
	 * @return The memcached hosts.
	 */
	@Key("cache.memcached.hosts")
    String getMemcachedHosts();
		
	/**
	 * Gets the cache TTL.
	 * @return The cache TTL. If not specified in the properties
	 * then <code>3600</code> will be returned.
	 */
	@Key("cache.ttl")
	@DefaultValue("3600")
	int getCacheTtl();
	
	/**
	 * Gets the queue provider.
	 * @return The queue provider. If not specified in the properties
	 * then <code>internal</code> will be returned.
	 */
	@Key("queue.provider")
	@DefaultValue("internal")
    String getQueueProvider();
		
	/**
	 * Gets the SQS queue URL.
	 * @return The SQS queue URL.
	 */
	@Key("queue.sqs.queue.url")
    String getSqsQueueUrl();
	
	/**
	 * Gets the SQS endpoint.
	 * @return The SQS endpoint. If not specified in the properties
	 * then the endpoint for us-east-1 (<code>https://sqs.us-east-1.amazonaws.com</code>)
	 * will be returned.
	 */
	@Key("queue.sqs.endpoint")
    String getSqsEndpoint();
	
	/**
	 * Gets the SQS access key.
	 * @return The SQS access key.
	 */
	@Key("queue.sqs.accesskey")
    String getSqsAccessKey();
	
	/**
	 * Gets the SQS secret key.
	 * @return The SQS secret key.
	 */
	@Key("queue.sqs.secretkey")
    String getSqsSecretKey();
	
	/**
	 * Gets the SQS queue visibility timeout.
	 * @return The SQS queue visibility timeout. If not specified in the
	 * properties then <code>60</code> will be returned.
	 */
	@Key("queue.sqs.visibility.timeout")
    int getSqsVisibilityTimeout();
	
	/**
	 * Gets the ActiveMQ broker URL.
	 * @return The ActiveMQ broker URL. If not specified in the
	 * properties then <code>vm://localhost?broker.persistent=true</code>
	 * will be returned.
	 */
	@Key("queue.activemq.broker.url")
	@DefaultValue("vm://localhost?broker.persistent=true")
    String getActiveMQBrokerUrl();
	
	/**
	 * Gets the name of the ActiveMQ queue.
	 * @return The name of the ActiveMQ queue. If not specified in the
	 * properties then <code>entitydb</code> will be returned.
	 */
	@Key("queue.activemq.queue.name")
	@DefaultValue("entities")
    String getActiveMQQueueName();
	
	/**
	 * Gets the timeout for the ActiveMQ queue connections.
	 * @return The timeout for the ActiveMQ queue connections. If not specified
	 * in the settings then <code>100</code> will be returned.
	 */
	@Key("queue.activemq.timeout")
	@DefaultValue("100")
    int getActiveMQBrokerTimeout();
	
	/**
	 * Gets if the rules engine is enabled.
	 * @return <code>true</code> if the rules engine is enabled. If not
	 * specified in the settings then <code>false</code> will be returned.
	 */
	@Key("rules.engine.enabled")
	@DefaultValue("false")
    boolean isRulesEngineEnabled();

	/**
	 * Gets the directory containing the rules.
	 * @return The directory containing the rules.
	 */
	@Key("rules.directory")
    String getRulesDirectory();
	
	/**
	 * Gets the search index provider.
	 * @return The search index provider. If not specified in the settings
	 * then <code>internal</code> will be returned.
	 */
	@Key("search.index.provider")
	@DefaultValue("internal")
	String getSearchIndexProvider();
	
	/**
	 * Gets the hostname of Elasticsearch.
	 * @return The hostname of Elasticsearch.
	 */
	@Key("elasticsearch.host")
    String getElasticsearchHost();
	
	/**
	 * Gets the username for Elasticsearch.
	 * @return The username for Elasticsearch.
	 */
	@Key("elasticsearch.username")
    String getElasticsearchUsername();
	
	/**
	 * Gets the password for Elasticsearch.
	 * @return The password for Elasticsearch.
	 */
	@Key("elasticsearch.password")
    String getElasticsearchPassword();
	
	/**
	 * Gets the provider of the datastore.
	 * @return The provider of the datastore. If not provided 
	 * in the settings then <code>internal</code> will be returned.
	 */
	@Key("datastore")
	@DefaultValue("internal")
	String getDataStoreDatabase();
	
	/**
	 * Gets the JDBC URL for the datastore.
	 * @return The JDBC URL for the database. If not provided in
	 * the settings then <code>jdbc:mariadb://localhost/entitydb?useSSL=false</code>
	 * will be returned.
	 */
	@Key("datastore.jdbc.url")
	@DefaultValue("jdbc:mariadb://localhost/entitydb?useSSL=false")
	String getJdbcUrl();
	
	/**
	 * Gets the username for the datastore.
	 * @return The username for the datastore.
	 */
	@Key("datastore.username")
	String getDataStoreUsername();
	
	/**
	 * Gets the password for the datastore.
	 * @return The password for the datastore.
	 */
	@Key("datastore.password")
	String getDataStorePassword();
	
	/**
	 * Gets the provider for the metrics.
	 * @return The provider for the metrics. If not specified in the
	 * properties then <code>internal</code> will be returned.
	 */
	@Key("metrics.provider")
	@DefaultValue("internal")
	String getMetricsProvider();
	
	/**
	 * Gets the CloudWatch namespace. If not specified in the
	 * properties then <code>EntityDB</code> will be returned.
	 * @return The CloudWatch namespace.
	 */
	@Key("metrics.cloudwatch.namespace")
	@DefaultValue("EntityDB")
	String getCloudWatchNamespace();
	
	/**
	 * Gets the CloudWatch endpoint. If not specified in the
	 * properties then <code>https://monitoring.us-east-1.amazonaws.com</code>
	 * will be returned.
	 * @return The CloudWatch endpoint.
	 */
	@Key("metrics.cloudwatch.endpoint")
	@DefaultValue("https://monitoring.us-east-1.amazonaws.com")
	String getCloudWatchEndpoint();
	
	/**
	 * Gets the CloudWatch access key.
	 * @return The CloudWatch access key.
	 */
	@Key("metrics.cloudwatch.accesskey")
	String getCloudWatchAccessKey();
	
	/**
	 * Gets the CloudWatch secret key.
	 * @return The CloudWatch secret key.
	 */
	@Key("metrics.cloudwatch.secretkey")
	String getCloudWatchSecretKey();
	
	/**
	 * Gets the InfluxDB endpoint.
	 * @return The InfluxDB endpoint.
	 */
	@Key("metrics.influxdb.endpoint")
	String getInfluxDbEndpoint();
	
	/**
	 * Gets the InfluxDB database name.
	 * @return The InfluxDB database name.
	 */
	@Key("metrics.influxdb.database")
	String getInfluxDbDatabase();
	
	/**
	 * Gets the InfluxDB username.
	 * @return The InfluxDB username.
	 */
	@Key("metrics.influxdb.username")
	String getInfluxDbUsername();
	
	/**
	 * Gets the InfluxDB password.
	 * @return The InfluxDB password.
	 */
	@Key("metrics.influxdb.password")
	String getInfluxDbPassword();
		
	/**
	 * Gets if entity ACLs are masked when entities are returned
	 * through the API.
	 * @return <code>true</code> if entity ACLs are masked when entities
	 * are returned through the API. If not specified in the properties
	 * then <code>false</code> will be returned.
	 */
	@Key("mask.entity.acl")
	@DefaultValue("false")
	boolean isMaskEntityAcl();
	
	/**
	 * Gets if test data should be populated to the datastore when
	 * EntityDB starts.
	 * @return <code>true</code> if test data should be populated to the
	 * datastore when EntityDB starts. If not specified in the properties
	 * then <code>false</code> will be returned.
	 */
	@Key("populate.test.data")
	@DefaultValue("false")
	boolean isPopulateTestData();
	
}