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
package ai.philterd.entitydb.model.eql;

/**
 * A confidence range for a query.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class ConfidenceRange {

	private double minimum = 0.0;
	private double maximum = 100.0;
	
	@Override
	public String toString() {
		return "Minimum: " + minimum + "; Maximum: " + maximum;
	}
	
	/**
	 * Creates a new confidence range with the
	 * minimum value set to 0.0 and the maximum
	 * value set to 100.0.
	 */
	public ConfidenceRange() {
		
	}
	
	/**
	 * Creates a new confidence range using a single value.
	 * @param confidence The confidence value.
	 */
	public ConfidenceRange(double confidence) {
		
		this.minimum = confidence;
		this.maximum = confidence;
		
	}
	
	/**
	 * Creates a new confidence range. The provided
	 * minimum and maximum values are inclusive.
	 * @param minimum The minimum value.
	 * @param maximum The maximum value.
	 */
	public ConfidenceRange(double minimum, double maximum) {
		
		this.minimum = minimum;
		this.maximum = maximum;
		
	}
	
	/**
	 * Gets the minimum value. When used in a query this value
	 * is used as "greater than or equal to" this value.
	 * @return The minimum value.
	 */
	public double getMinimum() {
		return minimum;
	}
	
	/**
	 * Gets the maximum value. When used in a query this value
	 * is used as "less than or equal to" this value.
	 * @return The maximum value.
	 */
	public double getMaximum() {
		return maximum;
	}
	
}
