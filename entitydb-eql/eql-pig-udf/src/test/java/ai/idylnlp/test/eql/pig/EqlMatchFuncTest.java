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
package ai.idylnlp.test.eql.pig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.pig.FilterFunc;
import org.apache.pig.data.DefaultTuple;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import ai.idylnlp.eql.pig.EqlMatchFunc;
import ai.idylnlp.model.entity.Entity;

public class EqlMatchFuncTest {

	private static final String SELECT_ALL = "select * from entities";
	private Gson gson;
	
	@Before
	public void before() {
		
		gson = new Gson();
		
	}
	
	@Test
	public void classExtendsFilterFunc() {
		assertTrue(new EqlMatchFunc(SELECT_ALL) instanceof FilterFunc);
	}

	@Test
	public void filter1() throws IOException {
		
		Entity entity = new Entity("George Washington");
		String jsonEntity = gson.toJson(entity);
		
		DefaultTuple input = new DefaultTuple();
		input.append(jsonEntity);

		assertEquals(true, new EqlMatchFunc(SELECT_ALL).exec(input));
		
	}

}
