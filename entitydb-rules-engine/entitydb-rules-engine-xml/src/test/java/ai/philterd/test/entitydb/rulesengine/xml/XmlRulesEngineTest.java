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
package ai.philterd.test.entitydb.rulesengine.xml;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.rulesengine.Condition;
import ai.philterd.entitydb.model.rulesengine.EntityCondition;
import ai.philterd.entitydb.model.rulesengine.EntityMetadataCondition;
import ai.philterd.entitydb.model.rulesengine.EqlCondition;
import ai.philterd.entitydb.model.rulesengine.Rule;
import ai.philterd.entitydb.model.rulesengine.RuleAction;
import ai.philterd.entitydb.model.rulesengine.RuleEvaluationResult;
import ai.philterd.entitydb.model.rulesengine.RulesEngineException;
import ai.philterd.entitydb.model.rulesengine.SesRuleAction;
import ai.philterd.entitydb.rulesengine.xml.XmlRulesEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XmlRulesEngineTest {
	
	private static final Logger LOGGER = LogManager.getLogger(XmlRulesEngineTest.class);
	
	@Test
	public void matchTest1() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest2() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertFalse(result.isMatch());				
		
	}
	
	@Test
	public void matchTest3() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.CONFIDENCE, "50"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setConfidence(50);
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest4() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.CONFIDENCE, "50", EntityCondition.LESS_THAN));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setConfidence(25);
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest5() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.CONFIDENCE, "50", EntityCondition.GREATER_THAN));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
				
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setConfidence(75);
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest6() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition("type", "person"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setConfidence(75);
		entity.setType("person");
		entity.setContext("context");
		entity.setDocumentId("document");
		
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest7() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition("type", "place"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("abe");
		entity.setConfidence(75);
		entity.setType("person");
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertFalse(result.isMatch());				
		
	}
	
	@Test
	public void matchTest8() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityCondition(EntityCondition.TYPE, "person"));
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<>();
				
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setContext("context");
		entity.setDocumentId("document");
		
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest9() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "^g.*$", EntityCondition.MATCHES));
		
		List<RuleAction> list = new ArrayList<>();
				
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest10() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityCondition(EntityCondition.CONTEXT, "^con.*$", EntityCondition.MATCHES));
		
		List<RuleAction> list = new ArrayList<>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setContext("context");
		entity.setContext("context");
		entity.setDocumentId("document");
		
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest11() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EqlCondition("select * from entities"));
		
		List<RuleAction> list = new ArrayList<>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest12() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityMetadataCondition("age", "50"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Map<String, String> metadata = new HashMap<>();
		metadata.put("age", "50");
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setMetadata(metadata);
		entity.setContext("context");
		entity.setDocumentId("document");
				
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void matchTest13() throws RulesEngineException, IOException {
		
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityMetadataCondition("spouse", "^mar.*$", "matches"));
		
		List<RuleAction> list = new ArrayList<>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Map<String, String> metadata = new HashMap<>();
		metadata.put("spouse", "martha");
		
		Entity entity = new Entity();
		entity.setText("george");
		entity.setType("person");
		entity.setMetadata(metadata);
		entity.setContext("context");
		entity.setDocumentId("document");

		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		RuleEvaluationResult result = XmlRulesEngine.evaluate(entity);
		
		assertTrue(result.isMatch());				
		
	}
	
	@Test
	public void readWriteRuleTest() throws Exception {
	
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<>();
		list.add(new SesRuleAction());
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		XmlRulesEngine XmlRulesEngine = new XmlRulesEngine(rule);
		String xml = generate(rule);
		
		LOGGER.info(xml);
		
		Rule readRule = XmlRulesEngine.read(xml);
		
		assertEquals(1, readRule.getConditions().size());
		assertEquals(1, readRule.getActions().size());
		
		assertTrue(readRule.getActions().get(0) instanceof SesRuleAction);
		
	}
	
	private String generate(Rule rule) throws RulesEngineException {
		
		try {
		
			JAXBContext jaxb = JAXBContext.newInstance(Rule.class, EntityCondition.class, SesRuleAction.class);
			
			Marshaller jaxbMarshaller = jaxb.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			StringWriter writer = new StringWriter();
			
			jaxbMarshaller.marshal(rule, writer);		
			
			writer.close();
			
			return writer.toString();
		
		} catch (IOException | JAXBException ex) {
			
			throw new RulesEngineException(ex);
			
		}
		
	}
		
}