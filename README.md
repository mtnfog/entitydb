# EntityDB

EntityDB is an application that integrates several components to provide a unified means for storing and querying entities (people, places, and things). This project includes the Entity Query Language (EQL) which facilitates querying entities across various underlying databases through a single query language.

As EntityDB matures and gets closer to a 1.0 release, the project's goals are to provide a means for storing and querying entities that's scalable, efficient, and useful to the community.

For help using and configuring EntityDB please refer to the [wiki](https://github.com/philterd/entitydb/wiki), especially the [Quick Start Guide](https://github.com/mtnfog/entitydb/wiki/Quick-Start-Guide). More information about EntityDB may be available on our website at http://www.philterd.ai/products/entitydb/.

## Architectural Overview

Entities are stored in an underlying database. Supported databases are MySQL, MongoDB, Cassandra, and DynamoDB. Entities are indexed in Elasticsearch for fast querying. A cache stores recently ingested and accessed entities to improve performance. A separate database, the data store, manages data such as users, groups, queries, and other information.

## Features

The following are brief high-level descriptions of EntityDB's main features. Refer to the wiki for more detailed descriptions and information on how to configure and use the features.

### API

The [API](https://github.com/mtnfog/entitydb/wiki/API) is built on REST and JSON. The API allows for entity ingestion, status and health monitoring, and entity querying through the Entity Query Language (EQL).

### Entity Store

The entity store is the master dataset of entities. It is an immutable data store. EntityDB provides a choice of MySQL, MongoDB, Cassandra, and DynamoDB for the underlying entity store. You are free to choose the database that best satisfies your use-case requirements.

### Search Index

As entities are ingested they are indexed in a search engine. All queries are performed against the search engine. Currently, the only supported search index is Elasticsearch.

### Entity Access Control

Each ingested entity is assigned an ACL. The ACL determines the entity's visibility to users and groups of the system.

### Audit

Various actions that occur in EntityDB are outputted as audit events. Some of the audited events include entity ingests, entities returned through queries, and entity ACL modifications.

### Continuous Queries

Entities received through the API are evaluated by the continuous queries. A continuous query is an EQL query that generates a notification when an entity meets the query's conditions. Continuous queries can be used to receive notifications that an ingested entity satisfies some conditions. Continuous queries are designed to be fast and efficient and promote a low time-to-alert (TTA).

For example, the continuous query `select * from entities where text = 'George'` will generate a notification when an entity having the text "George" is ingested.

### Rules Engine

Similar to continuous queries, the rules engine is executed for each ingested entity. Rules are user-defined and can be created to take a specific action on entities that are found to match one or more conditions. However, unlike continuous queries, rules can contain complex logic and actions and are designed to be executed when time-to-alert is not critical.

### Metric Reporting

EntityDB can report metrics to AWS CloudWatch, InfluxDB, or the console. These metrics report values such as how long an entity is in the ingest queue before being ingested, how long continuous queries are taking to evaluate, and the counts of stored and indexed entities. These metrics provide a comprehensive overview of EntityDB's performance and statistics.

### Scalability

EntityDB is easily scaled horizontally since its components are all distributed. Simply stand up a new EntityDB instance to increase its throughput and performance. EntityDB's sample AWS CloudFormation creates an EntityDB autoscaling group behind an Elastic Load Balancer. The autoscaling group can be set to scale based on metrics such as the size of the ingest SQS queue, any of the EntityDB reported metrics, or any EC2 instance metrics.

## Building EntityDB

During EntityDB's build tests will be run. Some of the unit tests are more like integration tests and this is an area for improvement.

```
git clone https://github.com/philterd/entitydb.git
cd entitydb
mvn clean install
```

### Build Status

[![Build Status](https://travis-ci.org/mtnfog/entitydb.svg?branch=master)](https://travis-ci.org/mtnfog/entitydb)

## Running

Once successfully built, an `entitydb.jar` will be under `entitydb-app/target`. This is a runnable jar that can be started with `java -jar entitydb.jar`. By default, all components will use internal implementations but this can be changed in the `entitydb.properties`. See the [Documentation](https://github.com/mtnfog/entitydb/blob/master/documentation.md) for details on configuring the `entitydb.properties`.

## Deploying

Refer to the [wiki](https://github.com/mtnfog/entitydb/wiki/Deploying) for detailed deployment instructions. Scripts are included under the `scripts/packaging/` directory to create an AMI (Packer script), a Docker container, and a CloudFormation stack.

### Ingesting Entities

#### Via the REST API

Entities can be ingested through the API. Look under the `scripts/` directory for sample cURL scripts. Entities must be in the format defined in [entity-model](https://github.com/mtnfog/entity-model). Ingested entities are immutable.

#### Via the Internal API

When integrated directly with your application entities can be ingested through the queues bypassing the REST API. It is not recommended to ingest without queuing entities in order to prevent entity loss due to capacity or network issues.

## Client Drivers

* [EntityDB Java Driver](https://github.com/mtnfog/entitydb-java-driver)

Copyright © 2024 Philterd, LLC.

