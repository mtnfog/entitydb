REGISTER eql-pig-udf.jar;
DEFINE EqlFilter ai.idylnlp.eql.pig.EqlFilterFunc('select * from entities');
DEFINE EqlMatch ai.idylnlp.eql.pig.EqlMatchFunc('select * from entities');

rawDS = load 'input' using PigStorage() as (entity:chararray);

filteredDS = foreach rawDS generate EqlFilter(entity);
STORE filteredDS INTO 'output-filtered' using PigStorage(';');

matchDS = foreach rawDS generate EqlMatch(entity);
STORE matchDS INTO 'output-match' using PigStorage(';');