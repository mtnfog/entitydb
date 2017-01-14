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
package com.mtnfog.test.entitydb.rulesengine.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.rulesengine.Condition;
import com.mtnfog.entitydb.model.rulesengine.EntityCondition;
import com.mtnfog.entitydb.model.rulesengine.EntityMetadataCondition;
import com.mtnfog.entitydb.model.rulesengine.EqlCondition;
import com.mtnfog.entitydb.model.rulesengine.Rule;
import com.mtnfog.entitydb.model.rulesengine.RuleAction;
import com.mtnfog.entitydb.model.rulesengine.RuleEvaluationResult;
import com.mtnfog.entitydb.model.rulesengine.RulesEngineException;
import com.mtnfog.entitydb.model.rulesengine.SesRuleAction;
import com.mtnfog.entitydb.rulesengine.xml.XmlRulesEngine;

public class XmlRulesEngineTest {
	
	private static final Logger LOGGER = LogManager.getLogger(XmlRulesEngineTest.class);
	
	@Test
	public void matchTest1() throws RulesEngineException {
		
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
	public void matchTest2() throws RulesEngineException {
		
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
	public void matchTest3() throws RulesEngineException {
		
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
	public void matchTest4() throws RulesEngineException {
		
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
	public void matchTest5() throws RulesEngineException {
		
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
	public void matchTest6() throws RulesEngineException {
		
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
	public void matchTest7() throws RulesEngineException {
		
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
	public void matchTest8() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.TYPE, "person"));
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
				
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
	public void matchTest9() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "^g.*$", EntityCondition.MATCHES));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
				
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
	public void matchTest10() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.CONTEXT, "^con.*$", EntityCondition.MATCHES));
		
		List<RuleAction> list = new ArrayList<RuleAction>();		
		
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
	public void matchTest11() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EqlCondition("select * from entities"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
		
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
	public void matchTest12() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityMetadataCondition("age", "50"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Map<String, String> metadata = new HashMap<String, String>();
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
	public void matchTest13() throws RulesEngineException {
		
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityMetadataCondition("spouse", "^mar.*$", "matches"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
		
		Rule rule = new Rule();
		rule.setConditions(conditions);
		rule.setActions(list);	
		
		Map<String, String> metadata = new HashMap<String, String>();
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
	
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new EntityCondition(EntityCondition.TEXT, "george"));
		
		List<RuleAction> list = new ArrayList<RuleAction>();
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
		
			JAXBContext jaxb = JAXBContext.newInstance(new Class[] {Rule.class, EntityCondition.class, SesRuleAction.class});
			
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