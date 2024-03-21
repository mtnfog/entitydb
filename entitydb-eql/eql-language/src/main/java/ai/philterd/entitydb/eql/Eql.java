/*
 * Copyright 2024 Philterd, LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/*
 * (C) Copyright 2017 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.philterd.entitydb.eql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.eql.antlr.EqlBaseListener;
import ai.philterd.entitydb.eql.antlr.EqlLexer;
import ai.philterd.entitydb.eql.antlr.EqlParser;
import ai.philterd.entitydb.eql.antlr.EqlParser.ConditionContext;
import ai.philterd.entitydb.eql.antlr.EqlParser.OptionContext;
import ai.philterd.entitydb.eql.exceptions.QueryGenerationException;
import ai.philterd.entitydb.eql.model.ConfidenceRange;
import ai.philterd.entitydb.eql.model.EntityMetadataFilter;
import ai.philterd.entitydb.eql.model.EntityOrder;
import ai.philterd.entitydb.eql.model.EntityQuery;
import ai.philterd.entitydb.eql.model.SortOrder;

/**
 * Entity Query Language (EQL) for querying entity stores.
 * EQL is a domain-specific language (DSL) that provides
 * a SQL-like interface for querying entity stores.
 * 
 * The purpose of EQL is to provide a generic interface that
 * can be used to query across all entity stores.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class Eql {
	
	private static final Logger LOGGER = LogManager.getLogger(Eql.class);

	private static final String CONTEXT = "context";
	private static final String DOCUMENTID = "documentid";
	private static final String TEXT = "text";
	private static final String CONFIDENCE = "confidence";	
	private static final String LANGUAGE = "language";
	private static final String LIMIT = "limit";
	private static final String OFFSET = "offset";
	private static final String URI = "uri";
	private static final String TYPE = "type";
	
	private Eql() {
		// This is a utility class.
	}
	
	/**
	 * Generates a {@link EntityQuery query} from an EQL query.
	 * @param eqlQuery An Entity Query Language query.
	 * @return A {@link EntityQuery query}.
	 */
	public static EntityQuery generate(String eqlQuery) throws QueryGenerationException {
		
		LOGGER.trace("Generating entity query from EQL statement: {}", eqlQuery);
		
		EntityQuery entityQuery = new EntityQuery();
		
		List<EntityMetadataFilter> entityMetadataFilters = new LinkedList<EntityMetadataFilter>();		
		
		try {
					
			InputStream stream = new ByteArrayInputStream(eqlQuery.getBytes(StandardCharsets.UTF_8));
	    	
	        EqlLexer lexer = new EqlLexer(new ANTLRInputStream(stream));
	        EqlParser parser = new EqlParser(new CommonTokenStream(lexer));
	        
	        parser.addErrorListener(new BaseErrorListener() {
	        	
	            @Override
	            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException ex) {
	                throw new IllegalStateException("Failed to parse EQL at line " + line + ". Message: " + msg + ".", ex);
	            }
	            
	        });

	        final AtomicReference<String> command = new AtomicReference<>();
	    	
	        parser.addParseListener(new EqlBaseListener() {	        	
	        	
	        	@Override
	            public void exitSort(EqlParser.SortContext ctx) {	            		         
	            	
	            	command.set(ctx.getText());
	            	
	            	if(ctx.NUMERIC_FIELD() != null) {
	            		
	            		entityQuery.setEntityOrder(EntityOrder.CONFIDENCE);
	            		
	            	} else {
	            		
	            		// It is a STRING_FIELD.
	            		
	            		if(ctx.STRING_FIELD().getText().equalsIgnoreCase("text")) {
	            			
	            			entityQuery.setEntityOrder(EntityOrder.TEXT);
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase("id")) {
	            			
	            			entityQuery.setEntityOrder(EntityOrder.ID);
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase("extractiondate")) {
	            			
	            			entityQuery.setEntityOrder(EntityOrder.EXTRACTION_DATE);
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase("type")) {
	            			
	            			entityQuery.setEntityOrder(EntityOrder.TYPE);
	            			
	            		}
	            		
	            	}	   	 
	            	
	            	if(ctx.SORT_ORDER_FIELD() != null) {
	            		
	            		if(ctx.SORT_ORDER_FIELD().getText().equalsIgnoreCase("asc")) {
	            			
	            			entityQuery.setSortOrder(SortOrder.ASC);
	            			
	            		} else if(ctx.SORT_ORDER_FIELD().getText().equalsIgnoreCase("desc")) {
	            			
	            			entityQuery.setSortOrder(SortOrder.DESC);
	            			
	            		}
	            		
	            	}
	            	
	            }
	        	
	            @Override
	            public void exitCommand(EqlParser.CommandContext ctx) {	            	
	            	
	            	//LOGGER.debug("Command: " + ctx.getText());
	            	
	            	command.set(ctx.getText());
	            	
	            }	           

	            @Override
	            public void exitOption(OptionContext ctx) {
	            	
	            	if(ctx.OPTION_FIELD() != null && !StringUtils.isEmpty(ctx.OPTION_FIELD().getText())) {	   
	            	
	            		if(ctx.OPTION_FIELD().getText().equalsIgnoreCase(LIMIT)) {	 
		   
		            		int limit = Integer.valueOf(ctx.INTEGERS().getText());
		            		
		            		entityQuery.setLimit(limit);
		 
		            	} else if(ctx.OPTION_FIELD().getText().equalsIgnoreCase(OFFSET)) {	 
		   
		            		int limit = Integer.valueOf(ctx.INTEGERS().getText());
		            		
		            		entityQuery.setOffset(limit);
		 
		            	}
		            	
	            	}
	            	
	            }

	            @Override
	            public void exitCondition(ConditionContext ctx) {            		            	
	            	
	            	if(ctx.NUMERIC_FIELD() != null && !StringUtils.isEmpty(ctx.NUMERIC_FIELD().getText())) {	            		            		
	            		
	            		if(ctx.NUMERIC_FIELD().getText().equalsIgnoreCase(CONFIDENCE)) {	            				            			
	            			
	            			if(ctx.INTEGERS().size() == 1) {
	           
	            				String operator = ctx.operator.getText();
	            				
	            				double confidence = Double.valueOf(ctx.INTEGERS(0).getText()) / 100;
	            				
	            				if(operator.equals("=")) {
	            				
		            				// For when "confidence = 50"		            				 		
			            			entityQuery.setConfidenceRange(new ConfidenceRange(confidence, confidence));
		            			
	            				} else if(operator.equals(">")) {
	            					
	            					// For when "confidence > 50"
	            					confidence = Math.min(1, confidence + 0.01);
			            			entityQuery.setConfidenceRange(new ConfidenceRange(confidence, 1.0));
	            					
	            				} else if(operator.equalsIgnoreCase("<")) {
	            					
	            					// For when "confidence < 50"
	            					confidence = Math.max(0, confidence - 0.01);
			            			entityQuery.setConfidenceRange(new ConfidenceRange(0.0, confidence));
	            					
	            				} else if(operator.equalsIgnoreCase("<=")) {
	            					
	            					// For when "confidence <= 50"	            
			            			entityQuery.setConfidenceRange(new ConfidenceRange(0.0, confidence));
	            					
	            				} else if(operator.equalsIgnoreCase(">=")) {
	            					
	            					// For when "confidence >= 50"
			            			entityQuery.setConfidenceRange(new ConfidenceRange(confidence, 1.0));
	            					
	            				}
	            				
	            			} else if(ctx.INTEGERS().size() == 2) {
	            			
	            				// For when "confidence between 10 and 50" is in the query.
	            				double minConfidence = Double.valueOf(ctx.INTEGERS(0).getText()) / 100;
	            				double maxConfidence = Double.valueOf(ctx.INTEGERS(1).getText()) / 100;
		            			entityQuery.setConfidenceRange(new ConfidenceRange(minConfidence, maxConfidence));
	            				
	            			}	            				            			
	            				            			
	            		}
	            		
	            	} else if(ctx.STRING_FIELD() != null) {
	            		
	            		//LOGGER.debug("Field: " + ctx.STRING_FIELD().getText());
	            		//LOGGER.debug("Value: " + ctx.STRING(0).getText());
	            		
	            		String operator = ctx.operator.getText();
	            		
	            		if(ctx.STRING_FIELD().getText().equalsIgnoreCase(CONTEXT)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setContext(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotContext(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(DOCUMENTID)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setDocumentId(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotDocumentId(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(TEXT)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setText(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotText(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(TYPE)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setType(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotType(ctx.STRING(0).getText());
	            			}
	            					            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(LANGUAGE)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setLanguageCode(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotLanguageCode(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(URI)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setUri(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setNotUri(ctx.STRING(0).getText());
	            			}
	            			
	            		}
	            		
	            	} else if(ctx.METADATA_FIELD() != null && !StringUtils.isEmpty(ctx.METADATA_FIELD().getText())) {	            			            			            			            		
	            				
            			String metadataName = ctx.STRING().get(0).getText();
            			String metadataValue = ctx.STRING().get(1).getText();	            			
 
            			entityMetadataFilters.add(new EntityMetadataFilter(metadataName, metadataValue));
            			
	            	}
	    
	            }

	        });
	        
	        entityQuery.setEntityMetadataFilters(entityMetadataFilters);
	        
	        // Parse the EQL.
	        parser.command();
	        
	        stream.close();
        
		} catch (IOException ex) {
			
			throw new QueryGenerationException("Unable to generate query.", ex);
			
		}
        
		return entityQuery;
		
	}
	
}
