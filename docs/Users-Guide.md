## Overview of EntityDB

EntityDB is an application integrated with other applications and services to provide a database for entities (people, places, and things). EntityDB was built complement Idyl E3's entity extraction abilities. EntityDB supports entity querying through the [Entity Query Language (EQL)](https://github.com/mtnfog/entitydb/wiki/EQL).

### Installing and Running EntityDB

If installing EntityDB from a Debian package, execute the following command:

`sudo dpkg -i entitydb.deb`

The EntityDB service can then be controlled using the `service` command:

`sudo service entitydb [command]`

If only running EntityDB from its JAR file, execute the following commands:

```
sudo mkdir /usr/share/entitydb
sudo cp entitydb.jar /usr/share/entitydb
```

EntityDB can started by executing the JAR file:

```
cd /usr/share/entitydb
java -jar entitydb.jar
```

### Quick Start

Install and start EntityDB as described in the above section. Without modifying any original configuration, EntityDB will use all internal services for the database, search, caching, and queue. This configuration is not recommended for a prodution system but it allows for evaluating EntityDB and testing your actual configuration.

The EntityDB API will be available at `http://localhost:8080/`. An entity can be indexed using the following script:

```
#!/bin/sh
CONTEXT="book"
DOCUMENTID="page"
ACL="user:group:1"
curl -X POST -H "Authorization: asdf1234" --data @body.json "http://localhost:8080/api/entity?context=$CONTEXT&documentId=$DOCUMENTID&acl=$ACL"
```

Where the content of `entity.json` is:

```
[{"text":"George Washington","confidence":0.7883777879039289,"span":"[3, 5)","type":"person","enrichments":{},"languageCode":"en"}]
```

When the script is run, an entity will be submitted to EntityDB for ingestion. EntityDB will queue the entity and then process the queue and ingest the entity. The entity's ACL specifies that the entity is visible to the user `user`, any user belonging to the group `group`, or the world. (The last part of the ACL is the world flag - `1` indicates visible, `0` is not visible.) If the entity has been successfully received and queued for ingest you will receive an `HTTP 200 OK` response. This response does not mean that the entity was ingested but instead means that the entity has been queued for ingest. You can monitor EntityDB's queue to track the ingest process.

You can, of course, use cURL for all your entity ingestion. However, it may be easier to use one of the the EntityDB API client packages available through our website.

### Highly-Available Deployment

EntityDB can scale horizontally for increased API performance and queue processing. This can be achieved simply by configuring an identical EntityDB installation on a separate virtual machine. When on AWS, this is best achieved by making an AMI of the EntityDB instance and using it to launch the additional instances via an autoscaling group. Parameters for the external components such as the database and Elasticsearch cluster can be modified as needed without affecting EntityDB.

## The Entity Model

All entities stored in EntityDB follow the entity model. This model specifies what properties are stored for each entity. How each entity is persisted in each database may vary (relational vs. NoSQL). Understanding the entity model provides insight into how EntityDB works.

| Property | Description | Example |
| --- | --- | --- |
| Entity ID | An ID that uniquely identifies the entity. | |
| Text | The text of the entity. | George Washington|
| Confidence | The confidence of the entity between 0 and 1. | 0.76 |
| Type | The type of the entity. | person |
| URI | A URI that uniquely identifies the entity, if available. | |
| Context | The context under which the entity was extracted. | |
| Document ID | The document ID under which the entity was extracted. | |
| Extaction Date | The timestamp the entity was extracted. | |
| Language | The language of the entity. | en |
| ACL | The entity's ACL | user:group:1 |
| Enrichments | Key-value pairs of entity enrichments if available. | | |

The Entity Model is open source and available at https://github.com/mtnfog/entity-model. The EntityDB API clients depend on the Entity Model project for submitting entities to EntityDB's API.

### Contexts and Document IDs

Entities are organized by contexts and document IDs. These values are assigned by the EntityDB client (such as Idyl E3) during ingest and they refer to the text from which the entity was extracted. A context is intended to be analogous to a collection, such as an encyclopedia. The document ID is intended to provide the location of the entity inside the context, such as a chapter or page number. You are free to assign the context and document ID as you wish but they are required values so they cannot be empty.

A good context and document ID combination should allow someone to locate the source of the entity without difficulty.

### The Underlying Database, a.k.a the "Entity Store"

EntityDB itself is not a database. Instead, EntityDB provides you with a choice of underlying databases to serve as entity stores. This flexibility allows you to choose the database that is optimal for your architecture and scenario. The supported databases are:

* Apache Cassandra
* MongoDB
* MySQL
* AWS DynamoDB
* An in-memory database useful for testing.

Each database has different strengths and abilities and each is fully supported by EntityDB. By using an external database you can continue to use your favorite data analysis tools. If you anticipate having enough entities to constitute "big-data" then Apache Cassandra is the recommended database.

### Entity Ingest

When entities are received through the API for ingest, entities are placed onto a queue for reliability and distributed processing. The supported queues are:

* AWS SQS
* Apache ActiveMQ
* An in-memory queue useful for testing.

In the background, the queue is consumed and the entities are stored in the entity store and indexed for search. Because successful entity ingestion is highly critical, an entity that cannot be stored and indexed successfully will be removed from the entity store and the index and the entity will remain on the queue. This is to prevent the entity store and the search index from becoming inconsistent. It is recommended that you configure a dead-letter queue in order to analyze entities that cannot be processed and to ensure that no entities are lost.

During the ingest each entity is assigned an ID that uniquely identifies the entity. An entity ID is a SHA256 hash and it is a fingerprint of an entity. Ingested entities are immutable.

Once the entity has been successfully stored and indexed, the entity will be processed by the rules engine.

### User Management

Users and groups are handled by the user manager. Users have API keys that are used to authenticate the user with the API and provide traceability between entities and users in the audit log.

### Audit

Operations involving entities are audited. The audit events are emitted to a [Fluentd](http://www.fluentd.org/) collector. Through Fluentd, you can configure the final destination for the audit logs, be it an external database or a text file. The audited event will contain the entity ID, a timestamp of the audited operation, and an identifier of any associated user. Note that the audited events only include events that occurred through EntityDB's API. Accessing entities directly through the underlying database will not generated audit events.

### Access Control

Each ingested entity has an associated access control list (ACL). This ACL specifies what users and groups can access the entity and if the entity is available to everyone. Once ingested, because entities are immutable, an entity's ACL cannot be changed. Changing an entity's ACL requires duplicating the entity, setting the ACL on the new entity, and setting the original entity as not visible. Immutable entities allow for consistency in audit logs and anywhere an entity may be referenced by its ID.

### Searching and Caching

Entity searching is powered by Elasticsearch. Received [EQL](#Entity Query Language (EQL)) queries are translated to Elasticsearch queries and executed.

### Rules Engine

The rules engine evaluates ingested entities against a set of user-defined rules. The rules can be provided as Drools rules or XML rules. Rules can be used for such actions as to send notifications upon a criteria match or to take a matching entity out of the workflow for external analysis.

#### Creating a New Rule

Rules are implemented either in XML or in the syntax of the Drools Business Rules Management System. There are many great resources freely available to help you become familiar with the Drools syntax. Idyl E3 includes easy ability to access the AWS services SQS, SNS, and SES from rules.

##### Rule Conditions

Each rule has one or more conditions that the entity must meet for the rule's actions to be executed. An EQL query can be used as a condition. (See an example rule using an EQL query.) The following properties of an entity are available for use in rule conditions. Each property can be used on its own or in combination with any other properties. You will see in the sample rules below how these properties are used to make conditions in a rule.

- Entity Text - The text of the entity, such as "George Washington".
- Entity Type - The type of the entity, such as "person" or "place".
- Entity Confidence - The confidence of the entity - a value between 0 and 100.
- Entity URI - A disambiguated entity will have a URI unique to it, for example "http://mtnfog.com/person/George_Washington".
- Entity Enrichments - A disambiguated entity may have zero or more enrichments in the form of name=value, such as age=50.
- Context - The context from which the entity was extracted.
- Document ID - The document ID from which the entity was extracted.

##### Rule Actions

If an entity meets the conditions given in the rule then the actions will be executed. A rule may have one or more actions. An action can be virtually anything - to send the entity in an email, put the entity onto a queue, or to raise some alarm.

Idyl E3 includes pre-built actions for interfacing with some Amazon Web Services technologies such as Simple Email Service, Simple Notification Service, Simple Queue Service, and Kinesis. You can always create your own actions through a little bit of programming.

#### Installing a New Rule

To install a new rule into Idyl E3, save the rule as a drl file (such as myrule.drl) and save the file under /usr/share/idyl-e3/rules/ and restart Idyl E3. The rule will be loaded and executed after each successful entity extraction.

Idyl E3 must also be restarted after editing an existing rule for the updated rule to be loaded.

#### Sample Rules

The sample rules described below are also available on our GitHub at https://github.com/mtnfog/idyl-e3-rules. Refer to the GitHub page for the most up to date sample rules.

##### Send an email when an entity having a certain text is detected.

This rule watches for entities having a certain text ("George Washington" in this example) and then sends the entity in an email message via AWS Simple Email Service (SES). This rule has a single condition (check the entity text) and a single action (send an email).

Note that if you are running Idyl E3 on AWS the rule can retrieve your SES credentials from the instance metadata service rather than specifying credentials in the rule by omitting the access key and secret key values in the integration constructor.

Drools implementation:

```
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entity.EntityExtractionAttributes;
import ai.philterd.idyl.sdk.rulesengine.drools.model.EntitiesHolder;
import ai.philterd.idyl.e3.rules.Ses;

// This is a sample rule that sends an email when an entity having the
// text "George Washington" is extracted. To use this rule, set your
// email address and AWS Simple Email Service access and secret keys.

// Refer to http://docs.aws.amazon.com/general/latest/gr/rande.html for
// AWS service endpoints per region.

rule "Email via AWS Simple Email Service Rule"

        when

                $att : EntityExtractionAttributes()

                $holder : EntitiesHolder()
                $entity : Entity(text == 'George Washington') from $holder.entities

        then

                String emailTo = "your@email.com";
                String emailFrom = "your@email.com";
                String subject = "Email from entity rule";
                String awsSesAccessKey = "your-access-key";
                String awsSesSecretKey = "your-secret-key";
                String awsSesEndpoint = "https://email-smtp.us-east-1.amazonaws.com";

                Ses integration = new Ses(
                        emailTo, emailFrom, subject, awsSesEndpoint, awsSesAccessKey, awsSesSecretKey
                );

                integration.process($entity, $att);

end
```

XML implementation:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!-- This rule will send an email when an entity with text "George Washington" is extracted. -->
<!-- This rule has a single condition and a single action but multiples of each are allowed. -->
<!-- Multiple conditions are "AND'd" and multiple actions will be executed in order. -->
<rule>
    <conditions>
        <condition xsi:type="entityCondition" match="text" value="George Washington" test="equals" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
    </conditions>
    <actions>
        <action xsi:type="sesRuleAction" accessKey="accesskey" secretKey="secretkey" endpoint="https://ses-endpoint..." to="to@youremail.com" from="from@youremail.com" subject="subject" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
    </actions>
</rule>
```

##### Send a notification when an entity having a certain text is detected.

This rule watches for entities having a certain text ("George Washington" in this example) and then sends the entity in an email message via AWS Simple Notification Service (SNS). By sending a notification to an SNS topic you can trigger other workflows or notifications. This rule has a single condition (check the entity text) and a single action (send a notification).

Note that if you are running Idyl E3 on AWS the rule can retrieve your SNS credentials from the instance metadata service rather than specifying credentials in the rule by omitting the access key and secret key values in the integration constructor.

##### Publish a message to a queue when an entity having a certain text is detected.

This rule watches for entities having a certain text ("George Washington�"  in this example) and then publishes a message containing the entity to a AWS Simple Queue Service (SQS) queue. This rule has a single condition (check the entity text) and a single action (publish a message to a queue).

Note that if you are running Idyl E3 on AWS the rule can retrieve your SQS credentials from the instance metadata service rather than specifying credentials in the rule by omitting the access key and secret key values in the integration constructor.

### Logging

When started from the console using the run script, event logs will be displayed on the console output. When started as a service, the event log is available at `/var/log/entitydb.log`.

## Configuring EntityDB

EntityDB is very configurable allows you to choose from many external applications and services. By default, all external applications and services are set to `internal` which means that all components are used locally or in-memory. This configuration is not for production use and instead provides a quick ability for you to run and test EntityDB. All settings set as `internal` should be changed prior to production use.

### Entity Store

| Setting | Description | Notes |
|---|---|---|
| entitydb.database | The type of database to use for the entity store. | Valid values are: `cassandra`, `dynamodb`, `mysql`, `mongodb`, and `internal`

Based on the value of `entitydb.database`, configure the connection to the database. The following tables detail the configuration options for the entity store.

#### Cassandra

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `cassandra.host`  | The host name or IP address of the Cassandra server. |   |   |
| `cassandra.port`  | The Cassandra port. |   |    |
| `cassandra.keyspace`  | The keyspace of the Cassandra database. |   |   | |

The CQL for creating the Cassandra tables is below. Note that the name of the Cassandra table cannot be changed.

```
CREATE TABLE entities (
    id text primary key,
    text text,
    confidence double,
    type text,
    uri text,
    language text,
    context text,
    documentid text,
    extractiondate bigint,
    acl text,
    enrichments map<text, text>,
    visible int,
    timestamp bigint
);
```
#### DynamoDB

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `dynamodb.endpoint`  | The endpoint of the DynamoDB service.  | https://dynamodb.us-east-1.amazonaws.com  |   |  
| `dynamodb.table`  | The name of the DynamoDB table.  |   | This table must already exist.  |  
| `dynamodb.accesskey`  | The DynamoDB access key. |   | Leave blank to get the credentials from the IAM role policy.  |  
| `dynamodb.secretkey`  | The DynamoDB secret key. |   |  Leave blank to get the credentials from the IAM role policy.  |  

The following columns will be created when the first entity is saved to the table:

- Column: `text`
- Column: `type`
- Column: `documentId`
- Column: `confidence`
- Column: `extractionDate`
- Column: `uri`
- Column: `enrichments`
- Column: `language`

#### MySQL

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `mysql.jdbc.url`  | The JDBC URL to the database.  |   | Example: jdbc:mysql://localhost/entitydb  |  
| `mysql.username`  | The MySQL username.  |   |   |  
| `mysql.password`  | The MySQL password.  |   |   |  |

To use MySQL as an entity store, the MySQL Connector/J jar must be on EntityDB's classpath.

The following SQL will create the required tables. The table names cannot be changed.

The tables in the database should be created as:

```
--
-- Database: `idyle3`
--

-- --------------------------------------------------------

--
-- Table structure for table `Entities`
--

CREATE TABLE IF NOT EXISTS `Entities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `probability` double NOT NULL,
  `context` varchar(255) DEFAULT NULL,
  `documentId` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `extractionDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `EntityEnrichments`
--

CREATE TABLE IF NOT EXISTS `EntityEnrichments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `entity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_entity` (`entity`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `EntityEnrichments`
--
ALTER TABLE `EntityEnrichments`
  ADD CONSTRAINT `fk_entity_id` FOREIGN KEY (`id`) REFERENCES `Entities` (`id`),
  ADD CONSTRAINT `fk_entity` FOREIGN KEY (`entity`) REFERENCES `Entities` (`id`);
```

#### MongoDB

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `mongodb.host`  | The host name or IP address of the MongoDB server.  |   |   |  
| `mongodb.port`  | The MongoDB port.  |   |   |  
| `mongodb.database`  | The name of the MongoDB database.  |   |   |  
| `mongodb.collection`  | The name of the collection in the MongoDB database.  |   |   |  
| `mongodb.username`  | The username.  |   | Leave blank to connect without authentication.  |  
| `mongodb.password`  | The password  |   | Leave blank to connect without authentication.  |   

#### Internal Entity Store

No configuration is required to use the internal entity store. The internal entity store is an in-memory database whose contents are not persisted across restarts. Use of the internal entity store is intended just for configuration testing. A warning in the log will be produced at startup if the internal entity store is being used.

### Entity Queue

General queue options that apply regardless of the queue provider:

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `queue.provider` | Specifies the provider of the entity queue. | | Valid values are `sqs`, `activemq`, and `internal`. |
| `queue.consumer.sleep`  | The amount of time in seconds that the queue consumer will sleep before rechecking the queue when the queue is empty.  |   |   |  
| `queue.consumer.threads`  | The number of queue consumer threads to execute.  |   |   |   |

The configuration options for the queues are as follows:

#### SQS

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `queue.sqs.queue.url`  | The JDBC URL to the database.  |   |   |  
| `queue.sqs.endpoint`  | The MySQL username.  |   |   |  
| `queue.sqs.accesskey`  | The MySQL password.  |   |   |  
| `queue.sqs.secretkey`  | The MySQL password.  |   |   |  
| `queue.sqs.visibility.timeout`  | The MySQL password.  |   |   |  |

#### ActiveMQ

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `queue.activemq.broker.url`  | The JDBC URL to the database.  |   |   |  
| `queue.activemq.queue.name`  | The MySQL username.  |   |   |  
| `queue.activemq.timeout`  | The MySQL password.  |   |   |  |

#### Internal Queue

No configuration is required to use the `internal` queue but its use is intended only for configuration testing. The internal queue provides no durability and is likely to lose entities. A warning in the log will be produced at startup if the internal queue is being used.

### Search Index

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `search.index.provider`  | The search index.  |   |  Valid values are `elasticsearch` and `internal`. |  

### Audit

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `audit.enabled` | Turns auditing on and off. | `true` or `false` |  |
| `audit.logger` | The audit logger.  |   |  Valid values are `fluentd` and `internal`. |  
| `audit.id` | An `id` identifying the EntityDB installation. | | Can be used to identify which EntityDB installation originated the audit event. |
If set to `internal` the audit events are written to a temporary system file. Use of `internal` audit logging is not recommended for productions systems and should only be used for configuration testing or in instances where audit logging is not required. The temporary file will be created each time EntityDB is started and the full path to the file will be available in EntityDB's log output. Audit events are written to the text file as tab-separated values with the format:

```
"Entity Id" [tab] "Timestamp" [tab] "User Id" [tab] "Audit Action"
```

#### Elasticsearch

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `elasticsearch.host`  | The Elasticsearch host name or IP address. |   |  |  
| `elasticsearch.username`  | The Elasticsearch user name.  |   | Leave blank if authentication is not required. |
| `elasticsearch.username`  | The Elasticsearch password.  |   | Leave blank if authentication is not required. | |

#### Internal Search Index

No configuration is required to use the `internal` search index but its use is intended only for configuration testing. The internal search index provides no durability and is likely to lose entities. A warning in the log will be produced at startup if the internal search index is being used.

### Cache

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `cache.provider` | The cache. |   |  Valid values are `memcached` and `internal`. |  

The configuration options for the caches are as follows:

#### Memcached

| Setting | Description  | Example Value  | Notes  |
|---|---|---|---|---|
| `cache.memcached.host`  | The Memcached host name or IP address.  |   |  |  
| `cache.memcached.port`  | The Memcached port.  |   |  |   |

#### Internal Cache

No configuration is required to use the `internal` cache but its use is intended only for configuration testing. Using the internal cache will negatively affect performance. A warning in the log will be produced at startup if the internal queue is being used.

### Rules Engine

| Setting | Description | Example Value | Notes |
| --- | --- | --- | --- |
| `rules.engine.enabled` | Turns the rules engine on and off. | `true` and `false` |
| `rules.directory` | The full path to the local directory containing the rules. | /usr/share/entitydb/rules |

## API

The interface for ingesting and querying entities is provided by a REST API.

### Endpoints

| Endpoint | Method(s) | Parameters | Description |
| --- | --- | --- | --- |
| `/api/entity` | `PUT` or `POST` | todo | Queues an entity for ingest. |
| `/api/entity/{entityId}/acl` | `PUT` or `POST` | todo | Modifies an entity's ACL. |
| `/api/eql` | `GET` | todo | Executes an [EQL](https://github.com/mtnfog/entitydb/wiki/EQL) query. Use the `show.entity.acl` if you do not want to reveal the entity's ACL to the client. When `show.entity.acl` is set to `false` the ACL field will be empty for all returned entities. |
