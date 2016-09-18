/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.eql;

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

import com.mtnfog.eql.antlr.EqlBaseListener;
import com.mtnfog.eql.antlr.EqlLexer;
import com.mtnfog.eql.antlr.EqlParser;
import com.mtnfog.eql.antlr.EqlParser.ConditionContext;
import com.mtnfog.eql.antlr.EqlParser.OptionContext;
import com.mtnfog.eql.exceptions.QueryGenerationException;
import com.mtnfog.eql.model.ConfidenceRange;
import com.mtnfog.eql.model.EntityEnrichmentFilter;
import com.mtnfog.eql.model.EntityOrder;
import com.mtnfog.eql.model.EntityQuery;
import com.mtnfog.eql.model.SortOrder;

/**
 * Entity Query Language (EQL) for querying entity stores.
 * EQL is an Idyl SDK domain-specific language (DSL) that provides
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
	private static final String ENRICHMENT = "enrichment";
	
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
		
		List<EntityEnrichmentFilter> entityEnrichmentFilters = new LinkedList<EntityEnrichmentFilter>();		
		
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
	            				entityQuery.setText(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(TYPE)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setType(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setType(ctx.STRING(0).getText());
	            			}
	            					            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(LANGUAGE)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setLanguageCode(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setLanguageCode(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(URI)) {
	            			
	            			if(operator.equals("=")) {
	            				entityQuery.setUri(ctx.STRING(0).getText());
	            			} else {
	            				entityQuery.setUri(ctx.STRING(0).getText());
	            			}
	            			
	            		} else if(ctx.STRING_FIELD().getText().equalsIgnoreCase(URI)) {
	            			
	            			entityQuery.setUri(ctx.STRING(0).getText());
	            			
	            		}
	            		
	            	} else if(ctx.ENRICHMENT_FIELD() != null && !StringUtils.isEmpty(ctx.ENRICHMENT_FIELD().getText())) {	            			            			            			            		
	            				
            			String enrichmentName = ctx.STRING().get(0).getText();
            			String enrichmentValue = ctx.STRING().get(1).getText();	            			
 
            			entityEnrichmentFilters.add(new EntityEnrichmentFilter(enrichmentName, enrichmentValue));
            			
	            	}
	    
	            }

	        });
	        
	        entityQuery.setEntityEnrichmentFilters(entityEnrichmentFilters);
	        
	        // Parse the EQL.
	        parser.command();
	        
	        stream.close();
        
		} catch (IOException ex) {
			
			throw new QueryGenerationException("Unable to generate query.", ex);
			
		}
        
		return entityQuery;
		
	}
	
}