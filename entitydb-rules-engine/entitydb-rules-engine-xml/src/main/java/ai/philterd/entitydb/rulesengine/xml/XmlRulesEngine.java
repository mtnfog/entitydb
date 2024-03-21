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
package ai.philterd.entitydb.rulesengine.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ai.philterd.entitydb.eql.filters.EqlFilters;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.QueryGenerationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.integrations.aws.DynamoDBIntegration;
import ai.philterd.entitydb.integrations.aws.KinesisFirehoseIntegration;
import ai.philterd.entitydb.integrations.aws.KinesisIntegration;
import ai.philterd.entitydb.integrations.aws.SesIntegration;
import ai.philterd.entitydb.integrations.aws.SnsIntegration;
import ai.philterd.entitydb.integrations.aws.SqsIntegration;
import ai.philterd.entitydb.model.rulesengine.Condition;
import ai.philterd.entitydb.model.rulesengine.DynamoDBRuleAction;
import ai.philterd.entitydb.model.rulesengine.EntityCondition;
import ai.philterd.entitydb.model.rulesengine.EntityMetadataCondition;
import ai.philterd.entitydb.model.rulesengine.EqlCondition;
import ai.philterd.entitydb.model.rulesengine.KinesisFirehoseRuleAction;
import ai.philterd.entitydb.model.rulesengine.KinesisStreamRuleAction;
import ai.philterd.entitydb.model.rulesengine.Rule;
import ai.philterd.entitydb.model.rulesengine.RuleAction;
import ai.philterd.entitydb.model.rulesengine.RuleEvaluationResult;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;
import ai.philterd.entitydb.model.rulesengine.RulesEngineException;
import ai.philterd.entitydb.model.rulesengine.SesRuleAction;
import ai.philterd.entitydb.model.rulesengine.SnsRuleAction;
import ai.philterd.entitydb.model.rulesengine.SqsRuleAction;

/**
 * An implementation of {@link RulesEngine} where the rules
 * are defined in XML files.
 * 
 * @author Philterd, LLC
 *
 */
public class XmlRulesEngine implements RulesEngine {

	private static final Logger LOGGER = LogManager.getLogger(XmlRulesEngine.class);
	
	private JAXBContext jaxb;
	private List<Rule> rules;
	

	@Override
	public List<String> getRules() {
		
		List<String> ruleNames = new ArrayList<String>();
		
		for(Rule rule : rules) {
			
			ruleNames.add(rule.getName());
			
		}
		
		return ruleNames;
		
	}
	
	/**
	 * Creates a new XML rules engine.
	 * @param rulesPath The full path to the directory containing the rules.
	 * @throws RulesEngineException Thrown if the directory cannot be accessed.
	 */
	public XmlRulesEngine(String rulesPath) throws RulesEngineException {
		
		LOGGER.info("Loading XML rules from {}.", rulesPath);
			
		rules = new ArrayList<Rule>();
		
		File file = new File(rulesPath);
		Collection<File> ruleFiles = FileUtils.listFiles(file, new String[]{"xml"}, true);
		
		for(File ruleFile : ruleFiles) {
			
			try {
			
				String ruleXml = FileUtils.readFileToString(ruleFile);
				
				Rule rule = read(ruleXml);
				
				rules.add(rule);
			
			} catch (IOException ex) {
				
				throw new RulesEngineException("Unable to read XML rule " + ruleFile + ".", ex);
				
			}
			
		}
		
		try {
		
			jaxb = JAXBContext.newInstance(Rule.class, EntityCondition.class, SesRuleAction.class);
		
		} catch (JAXBException ex) {
			
			throw new RulesEngineException(ex);
			
		}
		
	}
	
	/**
	 * Creates a new XML rules engine with a single rule.
	 * @param rule The {@link Rule rule}.
	 * @throws RulesEngineException Thrown if the rule engine cannot be initialized.
	 */
	public XmlRulesEngine(Rule rule) throws RulesEngineException {
				
		rules = new ArrayList<Rule>();
		rules.add(rule);
			
		try {
		
			jaxb = JAXBContext.newInstance(Rule.class, EntityCondition.class, SesRuleAction.class);
		
		} catch (JAXBException ex) {
			
			throw new RulesEngineException(ex);
			
		}
		
	}

	@Override
	public RuleEvaluationResult evaluate(Entity entity) throws QueryGenerationException {
		
		boolean executeActions = true;
		
		for(Rule rule : rules) {
		
			for(Condition condition : rule.getConditions()) {
				
				boolean conditionalMatch = false;
				
				if(condition instanceof EqlCondition) {
					
					EqlCondition eqlCondition = (EqlCondition) condition;
					
					conditionalMatch = EqlFilters.isMatch(entity, eqlCondition.getEql());
					
				} else if(condition instanceof EntityMetadataCondition) {
					
					EntityMetadataCondition entityMetadataCondition = (EntityMetadataCondition) condition;
					
					if(entity.getMetadata().containsKey(entityMetadataCondition.getMetadata())) {
						
						String value = entity.getMetadata().get(entityMetadataCondition.getMetadata());
						
						if(entityMetadataCondition.getTest().equalsIgnoreCase(EntityMetadataCondition.MATCHES)) {
							
							if(value.matches(entityMetadataCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
							
						} if(entityMetadataCondition.getTest().equalsIgnoreCase(EntityMetadataCondition.EQUALS)) {
						
							if(value.equalsIgnoreCase(entityMetadataCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
							
						}						
						
					}
					
				} else if(condition instanceof EntityCondition) {								
					
					EntityCondition xmlEntityCondition = (EntityCondition) condition;
					
					if(EntityCondition.TEXT.equalsIgnoreCase(xmlEntityCondition.getMatch())) {
						
						if(EntityCondition.EQUALS.equalsIgnoreCase(xmlEntityCondition.getTest())) {
							
							if(entity.getText().equalsIgnoreCase(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
						
						} else if(EntityCondition.MATCHES.equalsIgnoreCase(xmlEntityCondition.getTest())) {
													
							if(entity.getText().matches(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
							
						}
						
					} else if(EntityCondition.TYPE.equalsIgnoreCase(xmlEntityCondition.getMatch())) {
						
						if(entity.getType().equalsIgnoreCase(xmlEntityCondition.getValue())) {
							
							conditionalMatch = true;
							
						}
	
					} else if(EntityCondition.CONFIDENCE.equalsIgnoreCase(xmlEntityCondition.getMatch())) {
						
						int matchConfidence = Integer.valueOf(xmlEntityCondition.getValue());
						
						if(EntityCondition.EQUALS.equalsIgnoreCase(xmlEntityCondition.getTest()) && entity.getConfidence() == matchConfidence) {
							
							conditionalMatch = true;
							
						} else if(EntityCondition.LESS_THAN.equalsIgnoreCase(xmlEntityCondition.getTest()) && entity.getConfidence() < matchConfidence) {
							
							conditionalMatch = true;
							
						} if(EntityCondition.LESS_THAN_OR_EQUAL.equalsIgnoreCase(xmlEntityCondition.getTest()) && entity.getConfidence() <= matchConfidence) {
							
							conditionalMatch = true;
							
						} if(EntityCondition.GREATER_THAN.equalsIgnoreCase(xmlEntityCondition.getTest()) && entity.getConfidence() > matchConfidence) {
							
							conditionalMatch = true;
							
						} if(EntityCondition.GREATER_THAN_OR_EQUAL.equalsIgnoreCase(xmlEntityCondition.getTest()) && entity.getConfidence() >= matchConfidence) {
							
							conditionalMatch = true;
							
						}
						
					} else if(EntityCondition.CONTEXT.equalsIgnoreCase(xmlEntityCondition.getMatch())) {
						
						if(EntityCondition.EQUALS.equalsIgnoreCase(xmlEntityCondition.getTest())) {
							
							if(entity.getContext().equalsIgnoreCase(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
						
						} else if(EntityCondition.MATCHES.equalsIgnoreCase(xmlEntityCondition.getTest())) {
													
							if(entity.getContext().matches(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
							
						}
						
					} else if(EntityCondition.DOCUMENTID.equalsIgnoreCase(xmlEntityCondition.getMatch())) {
						
						if(EntityCondition.EQUALS.equalsIgnoreCase(xmlEntityCondition.getTest())) {
							
							if(entity.getDocumentId().equalsIgnoreCase(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
						
						} else if(EntityCondition.MATCHES.equalsIgnoreCase(xmlEntityCondition.getTest())) {
													
							if(entity.getDocumentId().matches(xmlEntityCondition.getValue())) {
								
								conditionalMatch = true;
								
							}
							
						}
	
					}
					
					if(!conditionalMatch) {
						
						// All conditions much evaluate to true (AND'd).
						// If one is false then the evaluation is false.
						executeActions = false;
						
					}
					
				}
				
			}
		
			if(executeActions) {	
				
				for(RuleAction action : rule.getActions()) {
					
					if(action instanceof SesRuleAction) {
						
						SesRuleAction sesAction = (SesRuleAction) action;
	
						SesIntegration sesIntegration = null;
						
						if(StringUtils.isEmpty(sesAction.getAccessKey())) {
						
							sesIntegration = new SesIntegration(sesAction.getTo(), sesAction.getFrom(), sesAction.getSubject(), sesAction.getEndpoint());
						
						} else {
							
							sesIntegration = new SesIntegration(sesAction.getTo(), sesAction.getFrom(), sesAction.getSubject(), sesAction.getEndpoint(), sesAction.getAccessKey(), sesAction.getSecretKey());
							
						}
						
						sesIntegration.process(entity);
						
					} else if(action instanceof SnsRuleAction) {
						
						SnsRuleAction snsAction = (SnsRuleAction) action;
	
						SnsIntegration snsIntegration = null;
						
						if(StringUtils.isEmpty(snsAction.getAccessKey())) {
						
							snsIntegration = new SnsIntegration(snsAction.getTopicArn(), snsAction.getSubject(), snsAction.getEndpoint());
						
						} else {
							
							snsIntegration = new SnsIntegration(snsAction.getTopicArn(), snsAction.getSubject(), snsAction.getEndpoint(), snsAction.getAccessKey(), snsAction.getSecretKey());
							
						}
						
						snsIntegration.process(entity);
										
					} else if(action instanceof SqsRuleAction) {
						
						SqsRuleAction sqsAction = (SqsRuleAction) action;
	
						SqsIntegration sqsIntegration = null;
						
						if(StringUtils.isEmpty(sqsAction.getAccessKey())) {
						
							sqsIntegration = new SqsIntegration(sqsAction.getQueueUrl(), sqsAction.getDelaySeconds(), sqsAction.getEndpoint());
						
						} else {
							
							sqsIntegration = new SqsIntegration(sqsAction.getQueueUrl(), sqsAction.getDelaySeconds(), sqsAction.getEndpoint(), sqsAction.getAccessKey(), sqsAction.getSecretKey());
							
						}
						
						sqsIntegration.process(entity);
										
					} else if(action instanceof KinesisStreamRuleAction) {
						
						KinesisStreamRuleAction kinesisAction = (KinesisStreamRuleAction) action;
	
						KinesisIntegration kinesisIntegration = null;
						
						if(StringUtils.isEmpty(kinesisAction.getAccessKey())) {
						
							kinesisIntegration = new KinesisIntegration(kinesisAction.getStreamName(), kinesisAction.getEndpoint());
						
						} else {
							
							kinesisIntegration = new KinesisIntegration(kinesisAction.getStreamName(), kinesisAction.getEndpoint(), kinesisAction.getAccessKey(), kinesisAction.getSecretKey());
							
						}
						
						kinesisIntegration.process(entity);
										
					} else if(action instanceof KinesisFirehoseRuleAction) {
						
						KinesisFirehoseRuleAction kinesisFirehoseAction = (KinesisFirehoseRuleAction) action;
	
						KinesisFirehoseIntegration kinesisFirehoseIntegration = null;
						
						if(StringUtils.isEmpty(kinesisFirehoseAction.getAccessKey())) {
						
							kinesisFirehoseIntegration = new KinesisFirehoseIntegration(kinesisFirehoseAction.getStreamName(), kinesisFirehoseAction.getEndpoint());
						
						} else {
							
							kinesisFirehoseIntegration = new KinesisFirehoseIntegration(kinesisFirehoseAction.getStreamName(), kinesisFirehoseAction.getEndpoint(), kinesisFirehoseAction.getAccessKey(), kinesisFirehoseAction.getSecretKey());
							
						}
						
						kinesisFirehoseIntegration.process(entity);
										
					} else if(action instanceof DynamoDBRuleAction) {
						
						DynamoDBRuleAction dynamoDBRuleAction = (DynamoDBRuleAction) action;
	
						DynamoDBIntegration dynamoDBIntegration = null;
						
						if(StringUtils.isEmpty(dynamoDBRuleAction.getAccessKey())) {
						
							dynamoDBIntegration = new DynamoDBIntegration(dynamoDBRuleAction.getTableName(), dynamoDBRuleAction.getEndpoint());
						
						} else {
							
							dynamoDBIntegration = new DynamoDBIntegration(dynamoDBRuleAction.getTableName(), dynamoDBRuleAction.getEndpoint(), dynamoDBRuleAction.getAccessKey(), dynamoDBRuleAction.getSecretKey());
							
						}
						
						dynamoDBIntegration.process(entity);
										
					}
					
				}
				
			}
			
		}
		
		return new RuleEvaluationResult(executeActions);
		
	}
	

	@Override
	public Rule read(String rule) throws RulesEngineException {
				
		try {
		
			Unmarshaller jaxbUnmarshaller = jaxb.createUnmarshaller();
			
			StringReader reader = new StringReader(rule);
			
			Rule xmlRule = (Rule) jaxbUnmarshaller.unmarshal(reader);
			
			return xmlRule;
		
		} catch (JAXBException ex) {
			
			throw new RulesEngineException(ex);
			
		}
		
	}
	
}