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
package com.mtnfog.entitydb.model.metrics;

/**
 * A metric of the system.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class Metric {

	private String name;
	private long value;
	private Unit unit;
	
	/**
	 * Creates a new metric.
	 * @param name The name of the metric.
	 * @param value The value of the metric.
	 * @param unit The {@link Unit unit} of the metric.
	 */
	public Metric(String name, long value, Unit unit) {
		
		this.name = name;
		this.value = value;
		this.unit = unit;
		
	}
	
	/**
	 * Gets the metric's name.
	 * @return The metric's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the metric's value.
	 * @return The metric's value.
	 */
	public long getValue() {
		return value;
	}
	
	/**
	 * Gets the metric's {@link Unit unit}.
	 * @return The metric's {@link Unit unit}.
	 */
	public Unit getUnit() {
		return unit;
	}
	
}