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
    timestamp bigint,
    indexed bigint
);


CREATE INDEX entities_indexed
   ON entities (indexed);