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
package ai.philterd.entitydb.eql.pig;

import java.io.IOException;

import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;

import com.google.gson.Gson;

/**
 * Pig UDF that executes an EQL statement on an entity.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EqlMatchFunc extends FilterFunc {

	private String eql;
	private Gson gson;

	public EqlMatchFunc(String eql) {

		this.eql = eql;
		gson = new Gson();

	}

	@Override
	public Boolean exec(Tuple input) throws IOException {

		if (input == null || input.size() == 0) {

			return null;

		} else {

			Entity entity = gson.fromJson(input.get(0).toString(), Entity.class);

			return EqlFilters.isMatch(entity, eql);

		}

	}

}