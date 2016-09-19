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
package com.mtnfog.entitydb.eql.filters.comparisons;

import org.apache.commons.lang3.StringUtils;

public enum DateComparison {

	BEFORE("before"), AFTER("after");

	private String dateComparison;

	private DateComparison(String dateComparison) {

		this.dateComparison = dateComparison;

	}

	/**
	 * Gets the enumeration from a string value.
	 * @param dateComparison The value to look up.
	 * @return A {@link DateComparison comparison}.
	 */
	public static DateComparison fromValue(String dateComparison) {

		if(StringUtils.isNotEmpty(dateComparison)) {
			
			for (DateComparison d : DateComparison.values()) {
				
				if (dateComparison.equalsIgnoreCase(d.getDateComparison())) {
					return d;
				}
				
			}
			
		}

		throw new IllegalArgumentException("No date comparison found with value: " + dateComparison);

	}

	@Override
	public String toString() {

		return dateComparison;

	}

	public String getDateComparison() {

		return dateComparison;

	}

}