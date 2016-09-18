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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.configuration;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.apache.commons.lang3.StringUtils;

@Sources("file:entitydb.properties")
public interface EntityDbProperties extends Config {

	@Key("indexer.enabled")
	@DefaultValue("true")
	public boolean isIndexerEnabled();
	
	@Key("indexer.batch.size")
	@DefaultValue("25")
	public int getIndexerBatchSize();
	
	@Key("user.manager")
	@DefaultValue("internal")
	public String getUserManager();
	
	@Key("users")
	@DefaultValue(StringUtils.EMPTY)
	public String getUsers();	
	
	@Key("groups")
	@DefaultValue(StringUtils.EMPTY)
	public String getGroups();
	
	@Key("audit.enabled")
	@DefaultValue("true")
	public boolean isAuditEnabled();
	
	@Key("audit.logger")
    @DefaultValue("tempfile")
    public String getAuditLogger();
	
	@Key("audit.id")
	@DefaultValue("localhost")
	public String getAuditId();
	
	@Key("entitydb.database")
    @DefaultValue("internal")
    public String getDatabase();
	
	@Key("mongodb.host")
    public String getMongoDBHost();
	
	@Key("mongodb.port")
    public int getMongoDBPort();
	
	@Key("mongodb.database")
    public String getMongoDBDatabase();
	
	@Key("mongodb.collection")
    public String getMongoDBCollection();
	
	@Key("mongodb.username")
    public String getMongoDBUsername();
	
	@Key("mongodb.password")
    public String getMongoDBPassword();
	
	@Key("cassandra.host")
    public String getCassandraHost();
	
	@Key("cassandra.port")
    public int getCassandraPort();
	
	@Key("cassandra.keyspace")
    public String getCassandraKeyspace();
	
	@Key("dynamodb.endpoint")
    public String getDynamoDBEndpoint();
	
	@Key("dynamodb.table")
    public String getDynamoDBTable();
	
	@Key("dynamodb.accesskey")
    public String getDynamoDBAccessKey();
	
	@Key("dynamodb.secretkey")
    public String getDynamoDBSecretKey();
	
	@Key("mysql.jdbc.url")
    public String getMySqlJdbURL();
	
	@Key("mysql.username")
    public String getMySqlUsername();
	
	@Key("mysql.password")
    public String getMySqlPassword();
	
	@Key("cache")
	@DefaultValue("internal")
    public String getCache();
	
	@Key("cache.memcached.host")
    public String getMemcachedHost();
	
	@Key("cache.memcached.port")
    public int getMemcachedPor();
	
	@Key("queue.provider")
	@DefaultValue("internal")
    public String getQueueProvider();
	
	@Key("queue.consumer.sleep")
	@DefaultValue("60")
    public int getQueueConsumerSleep();
	
	@Key("queue.sqs.queue.url")
    public String getSqsQueueUrl();
	
	@Key("queue.sqs.endpoint")
    public String getSqsEndpoint();
	
	@Key("queue.sqs.accesskey")
    public String getSqsAccessKey();
	
	@Key("queue.sqs.secretkey")
    public String getSqsSecretKey();
	
	@Key("queue.sqs.visibility.timeout")
    public int getSqsVisibilityTimeout();
	
	@Key("queue.activemq.broker.url")
    public String getActiveMQBrokerUrl();
	
	@Key("queue.activemq.queue.name")
    public String getActiveMQQueueName();
	
	@Key("queue.activemq.timeout")
    public int getActiveMQBrokerTimeout();
	
	@Key("rules.engine.enabled")
    public boolean isRulesEngineEnabled();

	@Key("rules.directory")
    public String getRulesDirectory();
	
	@Key("search.index.provider")
	public String getSearchIndexProvider();
	
	@Key("elasticsearch.host")
    public String getElasticsearchHost();
	
	@Key("elasticsearch.username")
    public String getElasticsearchUsername();
	
	@Key("elasticsearch.password")
    public String getElasticsearchPassword();

	@Key("mask.entity.acl")
	@DefaultValue("false")
	public boolean isMaskEntityAcl();
	
}