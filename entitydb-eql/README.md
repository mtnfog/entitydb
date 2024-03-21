# Idyl NLP Entity Query Language

The Entity Query Language, or EQL, provides a SQL-like syntax for querying entities. EQL provides a means of filtering entities that meet given conditions. This project includes a Pig UDF for using EQL in your Pig jobs.

## Syntax

The EQL query `select * from entities` will return all entities from the entity store.

A where clause can be added to only retrieve entities meeting some condition:

`select * from entities where text = 'George Washington'`

This query returns all entities having the text "George Washington." Other queryable fields are confidence, documentId, and context. Multiple fields can be combined with the and keyword.

`select * from entities where text = 'George Washington' and confidence > 50`

EQL does not support OR conditionals. Use multiple EQL queries to accomplish an OR condition. EQL queries can be executed through the Idyl E3 API when the entity store is enabled.

### Example queries

To find or filter entities with a given text:

`select * from entities where text = "George Washington"`

To find or filter entities with a given text in a specific context:

`select * from entities where text = "George Washington" and context = "book1"`

#### Queryable Fields

Now that you see it's a lot like SQL, here are the queryable fields:

| Field | Description | Examples | Remarks |
| ----- | ----------- | -------- | ------- |
| `id` | The entity's ID. | | |
| `text` | The text of the entity. | "George Washington" | Supports wildcards `*` in the text but not as the first character. |
| `type` | The type of the entity. | "person" | |
| `confidence` | The confidence of the entity - integer values between 0 and 100, inclusive. | 50 | |
| `language` | The language of the entity. | en | |
| `context` | The entity's context. | | |
| `documentId` | The entity's document ID. | | |
| `uri` | The entity's URI. | | |

#### Paging

Paging can be achieved using the `limit` and `offset` keywords:

`select * from entities limit 10 offset 50`

This query returns the first 10 entities after the first 50 entities.

The `limit` and `offset` keywords can also be used independently. Note that by default the limit is 25. Use caution when setting large limits.
