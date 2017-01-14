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
package com.mtnfog.entitydb.eql.model;

/**
 * Specifies the field to order by in an
 * {@link EntityQuery query}.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public enum EntityOrder {

	/**
	 * Sort by the database-assigned ID.
	 */
	ID("id"),
	
	/**
	 * Sort by the entity text.
	 */
	TEXT("text"),
	
	/**
	 * Sort by the entity confidence.
	 */
	CONFIDENCE("confidence"),
	
	/**
	 * Sort by the type of the entity.
	 */
	TYPE("type"),
	
	/**
	 * Sort by the entity's extraction date.
	 */
	EXTRACTION_DATE("extractionDate");
	
	private String property;
	
	private EntityOrder(String property) {
		
		this.property = property;
		
	}
	
	/**
	 * Gets the name of the property to sort by.
	 * @return The name of the property.
	 */
	public String getProperty() {
		return property;
	}
	
}