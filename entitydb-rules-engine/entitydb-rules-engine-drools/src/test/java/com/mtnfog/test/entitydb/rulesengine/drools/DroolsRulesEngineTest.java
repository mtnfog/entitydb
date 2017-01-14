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
package com.mtnfog.test.entitydb.rulesengine.drools;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.rulesengine.RuleEvaluationResult;
import com.mtnfog.entitydb.model.rulesengine.RulesEngineException;
import com.mtnfog.entitydb.rulesengine.drools.DroolsRulesEngine;

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