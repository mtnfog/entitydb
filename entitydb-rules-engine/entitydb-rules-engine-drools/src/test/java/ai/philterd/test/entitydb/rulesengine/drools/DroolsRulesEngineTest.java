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
package ai.philterd.test.entitydb.rulesengine.drools;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.rulesengine.RuleEvaluationResult;
import ai.philterd.entitydb.model.rulesengine.RulesEngineException;
import ai.philterd.entitydb.rulesengine.drools.DroolsRulesEngine;

public class DroolsRulesEngineTest {

	@BeforeClass
	public static void checkOs() throws Exception {
	
		boolean isLinux = (System.getProperty("os.name").toLowerCase().contains("linux"));
		
		Assume.assumeTrue(isLinux);
		
	}
	
	@Test
	public void ruleTest1() throws RulesEngineException {
		
		final String rulesDirectory = new File("src/test/resources/").getAbsolutePath();
		
		DroolsRulesEngine runner = new DroolsRulesEngine(rulesDirectory);
		
		Entity entity = new Entity("George Washington");		
		entity.setContext("context");
		entity.setDocumentId("documentId");
						
		RuleEvaluationResult result = runner.evaluate(entity);
		
		assertTrue(result.isMatch());

	}
	
	@Test
	public void ruleTest2() throws RulesEngineException {
		
		final String rulesDirectory = new File("src/test/resources/").getAbsolutePath();
		
		DroolsRulesEngine runner = new DroolsRulesEngine(rulesDirectory);
		
		Entity entity = new Entity("George Washington");		
		entity.setContext("context");
		entity.setDocumentId("documentId");
						
		RuleEvaluationResult result = runner.evaluate(entity);
		
		assertTrue(result.isMatch());		
		assertEquals("::1", result.getAcl());
		
	}
	
	@Test
	public void ruleTest3() throws RulesEngineException {
		
		final String rulesDirectory = new File("src/test/resources/").getAbsolutePath();
		
		DroolsRulesEngine runner = new DroolsRulesEngine(rulesDirectory);

		Entity entity = new Entity("Not George Washington");		
		entity.setContext("context");
		entity.setDocumentId("documentId");
								
		RuleEvaluationResult result = runner.evaluate(entity);
		
		assertFalse(result.isMatch());

	}

}