/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.model.rulesengine;

import java.util.List;

import com.mtnfog.entity.Entity;

/**
 * Interface for rules engines.
 * 
 * @author Mountain Fog, Inc.
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