This guide shows how to get EntityDB up and running quickly.

## Requirements

You need the JDK 1.8 and Maven 3. (See the [System Requirements](https://github.com/mtnfog/entitydb/wiki/System-Requirements) for the requirements for a production system.)

## Steps

### Building EntityDB

First, clone the repository:

`git clone https://github.com/mtnfog/entitydb.git`

Change to the cloned directory:

`cd entitydb`

Now build EntityDB:

`mvn clean install`

If you're in a hurry and want to skip tests:

`mvn clean install -DskipTests=true`

### Running EntityDb

When the build is complete, change the directory:

`cd entitydb-app/target`

Run EntityDB.

`java -jar entitydb.jar`

EntityDB will start up. By default, EntityDB's REST API listens on port 8080. You can now connect to EntityDB through one of the open source drivers, `cURL`, or your own client implementation. Edit the `entitydb.properties` to change the components such as the database, queue, or search engine. By default, internal implementations of each component are used.

### Ingesting an Entity

You can ingest an entity:

```
ACL="user:group:1"
curl -vvvv -X POST -H "Content-Type: application/json" -H "Authorization: asdf1234" --data @body.json "http://localhost:8080/api/entity?context=c&documentId=$
```

Where `body.json` is:

```
[{"text":"George Washington","confidence":0.7883777879039289,"span":"[3, 5)","type":"person","enrichments":{},"languageCode":"en"}]
```

When the entity to ingest is received by EntityDB, the entity is placed onto the ingest queue. When the queue is processed (in just a couple of seconds) the entity will persisted to the entity store and subsequently indexed into the search engine.

### Querying the Stored Entities

You can query the stored entities using [EQL](https://github.com/mtnfog/entitydb/wiki/EQL) (`select * from entities`):

`curl -vvvv -H "Authorization: asdf1234" "http://localhost:8080/api/eql?query=select+%2A+from+entities"`

This returns the response:

```
{
    "entities": [
        {
            "acl": {
                "groups": [
                    "group"
                ],
                "users": [
                    "user"
                ],
                "world": 1
            },
            "confidence": 0.7883777879039289,
            "enrichments": {},
            "entityId": "211b03b71c6b42e2495d6065ba9e6b2484b8b9a931b2dc424d50f565d8d8f6ca",
            "extractionDate": 1474675422964,
            "languageCode": "en",
            "text": "George Washington",
            "transactionId": 0,
            "type": "person"
        }
    ],
    "queryId": "57503fd0-dcec-4270-8d95-0964ca5a8b31"
}
```

### Summary

Congratulations! You have just built EntityDB, started it, ingested an entity, and executed a query!

### More Sample Scripts

Additional sample `cURL` scripts are located under the project's [`scripts/`](https://github.com/mtnfog/entitydb/tree/master/scripts) directory.
