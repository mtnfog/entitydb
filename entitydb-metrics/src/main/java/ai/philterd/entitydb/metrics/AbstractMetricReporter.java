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
package ai.philterd.entitydb.metrics;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import ai.philterd.entitydb.model.metrics.Unit;

/**
 * Base class for metric reporters.
 * 
 * @author Philterd, LLC
 *
 */
public abstract class AbstractMetricReporter {

	/**
	 * Gets an AWS CloudWatch {@link StandardUnit} given a {@link Unit}.
	 * @param unit A {@link Unit unit}.
	 * @return An AWS ClouWatch {@link StandardUnit} that corresponds to
	 * the given {@link Unit unit}. If there is no corresponding {@link StandardUnit}
	 * then <code>StandardUnit.None</code> will be returned.
	 */
	public StandardUnit getStandardUnit(Unit unit) {
		
		if(unit == Unit.MILLISECONDS) {
			
			return StandardUnit.Milliseconds;
			
		} else if(unit == Unit.COUNT) {
			
			return StandardUnit.Count;
			
		} else {
			
			return StandardUnit.None;
			
		}
		
	}
	
}