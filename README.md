# EntityDB

EntityDB is an application that integrates several components to provide a unified means for storing and querying entities (people, places, and things). This project includes the Entity Query Language (EQL) which facilitates querying entities across various underlying databases through a single query language.

As EntityDB matures its goals are to provide a means for storing and querying entities that's scalable, efficient, and useful to the community.

For documentation on how to configure and use EntityDB refer to the [User Documentation](https://github.com/mtnfog/entitydb/blob/master/documentation.md). More information about EntityDB may be available on our website at http://www.mtnfog.com/products/entitydb/.

## Architecture

![Architecture](https://www.mtnfog.com/wp-content/uploads/2016/02/entitydb-architecture.png)

The architecture of EntityDB is shown in the block diagram above. Entities are stored in an underlying database. Supported databases are MySQL and DynamoDB. The enterprise version of EntityDB also supports MongoDB and Cassandra. Entities are indexed in Elasticsearch for fast querying. A cache stores recently ingested and accessed entities to improve performance.

The API is REST-based and communicates via JSON. Through the API entities can be ingested and queried through the Entity Query Language (EQL). The Rules Engine is executed for each ingested entity. Rules are user-defined and can be created to take a specific action on entities that are found to match one or more conditions.

Early versions of EntityDB included its own custom data store for entities. We decided later on to let databases do what databases do best and instead provide an abstraction layer for multiple databases that give users a choice. We may revisit this decision in the future if there is a use-case where the optimal solution is a custom data store.

## Contributing

Contributions via pull requests are absolutely always welcome. Before your pull request can be merged we ask you to complete a [Contributors License Agreement (CLA)](https://www.mtnfog.com/about/legal/contributor-license-agreement/). It is a simple form to protect both sides and should take less than five minutes of your time.

## License

Copyright © 2016 Mountain Fog, Inc.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
