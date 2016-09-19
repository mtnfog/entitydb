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
package com.mtnfog.entitydb.eql.model;

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