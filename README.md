# EntityDB

For help using and configuring EntityDB please refer to the [wiki](https://github.com/mtnfog/entitydb/wiki), especially the [Quick Start Guide](https://github.com/mtnfog/entitydb/wiki/Quick-Start-Guide). More information about EntityDB may be available on our website at http://www.mtnfog.com/products/entitydb/.

EntityDB is an application that integrates several components to provide a unified means for storing and querying entities (people, places, and things). This project includes the Entity Query Language (EQL) which facilitates querying entities across various underlying databases through a single query language.

As EntityDB matures its goals are to provide a means for storing and querying entities that's scalable, efficient, and useful to the community.

## Architectural Overview

![Architecture](https://www.mtnfog.com/wp-content/uploads/2016/02/entitydb-architecture.png)

The architecture of EntityDB is shown in the block diagram above. Entities are stored in an underlying database. Supported databases are MySQL, MongoDB, Cassandra, and DynamoDB. Entities are indexed in Elasticsearch for fast querying. A cache stores recently ingested and accessed entities to improve performance.

The API is REST-based and communicates via JSON. Through the API entities can be ingested and queried through the Entity Query Language (EQL). Entities received through the API are placed onto a queue for ingestion.

The Rules Engine is executed for each ingested entity. Rules are user-defined and can be created to take a specific action on entities that are found to match one or more conditions.

Early versions of EntityDB included its own custom data store for entities. We decided later on to let databases do what databases do best and instead provide an abstraction layer for multiple databases that give users a choice. We may revisit this decision in the future if there is a use-case where the optimal solution is a custom data store.

## Building

During EntityDB's build tests will be run. Some of the unit tests are more like integration tests and this is an area for improvement.

```
git clone https://github.com/mtnfog/entitydb.git
cd entitydb
mvn clean install
```

### Build Status

[![Build Status](https://travis-ci.org/mtnfog/entitydb.svg?branch=master)](https://travis-ci.org/mtnfog/entitydb)

## Running

Once successfully built, an `entitydb.jar` will be under `entitydb-app/target`. This is a runnable jar that can be started with `java -jar entitydb.jar`. By default, all components will use internal implementations but this can be changed in the `entitydb.properties`. See the [Documentation](https://github.com/mtnfog/entitydb/blob/master/documentation.md) for details on configuring the `entitydb.properties`.

### Ingesting Entities

#### Via the REST API

Entities can be ingested through the API. Look under the `scripts/` directory for sample cURL scripts. Entities must be in the format defined in [entity-model](https://github.com/mtnfog/entity-model). Ingested entities are immutable.

#### Via Idyl E3

[Idyl E3 Entity Extraction Engine](http://www.mtnfog.com/?p=14) includes support for sending extracted entities to EntityDB.

#### Via the Internal API

When integrated directly with your application entities can be ingested through the queues bypassing the REST API. It is not recommended to ingest without queuing entities in order to prevent entity loss due to capacity or network issues.

## Client Drivers

* [EntityDB Java Driver](https://github.com/mtnfog/entitydb-java-driver)

## Contributing

Contributions via pull requests are absolutely always welcome. Before your pull request can be merged we ask you to complete a [Contributor Agreement](http://www.mtnfog.com/?p=1744). We encourage you to discuss your proposed contribution with us either through the [discussion groups](https://groups.google.com/forum/#!forum/entitydb), by creating an issue, or through email but only if the first two methods are unacceptable.

## License

Copyright © 2016 Mountain Fog, Inc.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

For proprietary licenses please contact support@mtnfog.com or visit http://www.mtnfog.com.
