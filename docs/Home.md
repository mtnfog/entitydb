[[images/entitydb.png]]

1. <a href="#what">What is EntityDB?</a>
1. <a href="#problem">What problems does EntityDB solve?</a>
1. <a href="#principles">What design principles underlie EntityDB?</a>
1. <a href="#contribute">How can I contribute to EntityDB?</a>

<a name="what" />
## What is EntityDB?

EntityDB manages the storing of entities (persons, places, and things) for purposes of querying, analysis, and archival.

<a name="problem" />
## What problems does EntityDB solve?

To make a system to store entities, there must be a data store, the ability to ingest entities, to index entities for fast queries, a means for querying the entities, along with security and audit controls. EntityDB provides these capabilities. Its REST API allows for ingesting and querying entities, and the underlying data store provides entity persistence. The [Entity Query Language (EQL)](https://github.com/mtnfog/entitydb/wiki/EQL) provides a unified language for
querying stored entities no matter what database is used for the data store.

<a name="principles" />
## What design principles underlie EntityDB?

We want EntityDB to meet its goals and give users choices for deployment flexibility. Each component of EntityDB can be swapped for a different implementation. The selectable components are:

* Queue - Entities are queued during ingest before being persisted to prevent entities from being lost. You can choose to use an AWS SQS queue or an ActiveMQ queue. A memory-based internal queue is available for development and testing purposes.
* Data Store - Entities are persisted into an underlying database. You can choose to use MySQL, DynamoDB, MongoDB, or Cassandra as the database. Each database provides different advantages based on your use-case. A memory-based internal data store is available for development and testing.
* Search Index - As entities are ingested they are indexed in a search engine. Currently the only choice for the search engine is Elasticsearch but additional implementations can be created by implementing the `SearchIndex` interface.

The architecture of EntityDB showing these components is below.

[[images/architecture.png]]

<a name="contribute" />
## How can I contribute to EntityDB?

Some ways you can contribute to EntityDB are by:

* Making code changes and additions.
* Editing this wiki and documentation
* Submitting issues you encounter when running EntityDB.
* Testing EntityDB and providing your results.

We welcome EntityDB-related discussions on our [group](https://groups.google.com/forum/#!forum/entitydb) or through [GitHub Issues](https://github.com/mtnfog/entitydb/issues).
