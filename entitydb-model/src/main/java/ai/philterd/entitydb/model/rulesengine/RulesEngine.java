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
package ai.philterd.entitydb.model.rulesengine;

import java.util.List;

import com.mtnfog.entity.Entity;

/**
 * Interface for rules engines.
 * 
 * @author Philterd, LLC
 *
 */
public interface RulesEngine {

	/**
	 * Evaluate the rule(s) against the entities.
	 * @param entity The {@link Entity entity}.
	 * @return A {RuleEvaluationResult result}.
	 */
	public RuleEvaluationResult evaluate(Entity entity);
			
	/**
	 * Reads a rule.
	 * @param rule The text of the rule.
	 * @return A configured {@link Rule rule}.
	 * @throws RulesEngineException Thrown if the rule cannot be read successfully.
	 */
	public Rule read(String rule) throws RulesEngineException;
	
	/**
	 * Gets the rules.
	 * @return A list of the rule names.
	 */
	public List<String> getRules();
	
}