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
package ai.idylnlp.eql.pig;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import com.google.gson.Gson;

import ai.idylnlp.eql.filters.EqlFilters;
import ai.idylnlp.model.entity.Entity;

/**
 * Pig UDF that executes an EQL statement on an entity.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EqlFilterFunc extends EvalFunc<String> {

	private String eql;
	private Gson gson;

	public EqlFilterFunc(String eql) {

		this.eql = eql;
		gson = new Gson();

	}

	@Override
	public String exec(Tuple input) throws IOException {

		if (input == null || input.size() == 0) {

			return null;

		} else {
			
			final String entityJson = input.get(0).toString();

			Entity entity = gson.fromJson(entityJson, Entity.class);

			if(EqlFilters.isMatch(entity, eql)) {
				
				return entityJson;
						
			} else {
				
				return "";
				
			}

		}
		
	}


}
