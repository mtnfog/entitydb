grammar Eql;

@header {
 	package com.mtnfog.entitydb.eql.antlr;
}
 	
command:
	  select (sort)? (';')?
//	| graph (';')?
	;

select:
	  'select * from entities'
	| 'select * from entities' (option)*
	| 'select * from entities where' condition (WS 'and' condition)*
	| 'select * from entities where' condition (WS 'and' condition)* (option)*
	;
	
//graph:
//	| 'select graph depth' WS value=INTEGERS WS 'from entities where' WS GRAPH_FIELD (WS)? '=' (WS)? value=STRING
//	| 'select graph depth' WS value=INTEGERS WS 'from entities where' WS GRAPH_FIELD (WS)? '=' (WS)? value=STRING (WS 'and' condition)* (option)*
//	;

condition: 
	  WS NUMERIC_FIELD (WS)? operator=('='|'>'|'<'|'>='|'<=') (WS)? value=INTEGERS
	| WS NUMERIC_FIELD WS 'between' WS value1=INTEGERS WS 'and' WS value2=INTEGERS
	| WS STRING_FIELD (WS)? operator=('='|'!=') (WS)? value=STRING 
	| WS ENRICHMENT_FIELD WS value1=STRING (WS)? '=' (WS)? value2=STRING 
	;

option:
	  WS OPTION_FIELD WS value=INTEGERS
	| WS OPTION_FIELD WS value=INTEGERS
	;
	
sort:
	  WS 'order by' WS (NUMERIC_FIELD | STRING_FIELD) (WS SORT_ORDER_FIELD)?
	;

ENRICHMENT_FIELD: 'enrichment';
NUMERIC_FIELD: 'confidence';
OPTION_FIELD: 'limit' | 'offset' ;
STRING_FIELD: 'id' | 'context' | 'documentid' | 'text' | 'type' | 'uri' | 'language' ;
SORT_ORDER_FIELD: 'asc' | 'desc' ;
INTEGERS: ('0'..'9')+ ;

STRING: '"' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-' | '/' | ' ' | ':' | [.] )+ '"'
   {
     String s = getText();
     s = s.substring(1, s.length() - 1); // strip the leading and trailing quotes
     s = s.replace("\"\"", "\""); // replace all double quotes with single quotes
     setText(s);
   }
   ;

WS: (' ' | '\t')+;