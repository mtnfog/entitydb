/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.rulesengine.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.eql.filters.EqlFilters;
import com.mtnfog.entitydb.integrations.aws.DynamoDBIntegration;
import com.mtnfog.entitydb.integrations.aws.KinesisFirehoseIntegration;
import com.mtnfog.entitydb.integrations.aws.KinesisIntegration;
import com.mtnfog.entitydb.integrations.aws.SesIntegration;
import com.mtnfog.entitydb.integrations.aws.SnsIntegration;
import com.mtnfog.entitydb.integrations.aws.SqsIntegration;
import com.mtnfog.entitydb.model.rulesengine.Condition;
import com.mtnfog.entitydb.model.rulesengine.DynamoDBRuleAction;
import com.mtnfog.entitydb.model.rulesengine.EntityCondition;
import com.mtnfog.entitydb.model.rulesengine.EntityMetadataCondition;
import com.mtnfog.entitydb.model.rulesengine.EqlCondition;
import com.mtnfog.entitydb.model.rulesengine.KinesisFirehoseRuleAction;
import com.mtnfog.entitydb.model.rulesengine.KinesisStreamRuleAction;
import com.mtnfog.entitydb.model.rulesengine.Rule;
import com.mtnfog.entitydb.model.rulesengine.RuleAction;
import com.mtnfog.entitydb.model.rulesengine.RuleEvaluationResult;
import com.mtnfog.entitydb.model.rulesengine.RulesEngine;
import com.mtnfog.entitydb.model.rulesengine.RulesEngineException;
import com.mtnfog.entitydb.model.rulesengine.SesRuleAction;
import com.mtnfog.entitydb.model.rulesengine.SnsRuleAction;
import com.mtnfog.entitydb.model.rulesengine.SqsRuleAction;

/**
 * An implementation of {@link RulesEngine} where the rules
 * are defined in XML files.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class XmlRulesEngine implements RulesEngine {

	private static final Logger LOGGER = LogManager.getLogger(XmlRulesEngine.class);
	
	private JAXBContext jaxb;
	private List<Rule> rules;
	
	/**
	 * {@inheritDoc}
	 */
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
		
			jaxb = JAXBContext.newInstance(new Class[] {Rule.class, EntityCondition.class, SesRuleAction.class});
		
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
		
			jaxb = JAXBContext.newInstance(new Class[] {Rule.class, EntityCondition.class, SesRuleAction.class});
		
		} catch (JAXBException ex) {
			
			throw new RulesEngineException(ex);
			
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuleEvaluationResult evaluate(Entity entity) {
		
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
					
					if(conditionalMatch == false) {
						
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
	
	/**
	 * {@inheritDoc}
	 */
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