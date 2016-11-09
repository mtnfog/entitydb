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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.metrics;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.mtnfog.entitydb.model.metrics.Unit;

/**
 * Base class for metric reporters.
 * 
 * @author Mountain Fog, Inc.
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