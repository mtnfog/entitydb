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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Used to query on entity metadata in an entity store.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EntityMetadataFilter {

	private String name;
	private String value;
	private boolean isCaseSensitive = false;
	private String comparator = "equals";
	
	/**
	 * Creates a new {@link StoredEntityMetadata}.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public EntityMetadataFilter(String name, String value) {
		
		this.name = name;
		this.value = value;
		
	}
	
	/**
	 * Creates a new {@link StoredEntityMetadata}.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 * @param isCaseSensitive Set to true to do a case-sensitive query.
	 */
	public EntityMetadataFilter(String name, String value, boolean isCaseSensitive) {
		
		this.name = name;
		this.value = value;
		this.isCaseSensitive = isCaseSensitive;
		
	}
	
	/**
	 * Creates a new {@link EntityMetadataFilter}.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 * @param isCaseSensitive Set to true to do a case-sensitive query.
	 * @param comparator How to do the comparison.
	 */
	public EntityMetadataFilter(String name, String value, boolean isCaseSensitive, String comparator) {
		
		this.name = name;
		this.value = value;
		this.isCaseSensitive = isCaseSensitive;
		this.comparator = comparator;
		
	}
		    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public String toString() {
    	return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * Gets the name of the attribute.
	 * @return The name of the attribute.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the attribute.
	 * @param name The name of the attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the value of the attribute.
	 * @return The value of the attribute.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the attribute.
	 * @param value The value of the attribute.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets if the query on metadata should be case-sensitive. Not all
	 * entity stores will be able to respect this value. Refer to the
	 * entity store implementation documentation to see if the entity store
	 * supports case-sensitive querying.
	 * @return True if the query on metadata should be case-sensitive.
	 */
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	/**
	 * Sets if the query on metadata should be case-sensitive. Not all
	 * entity stores will be able to respect this value. Refer to the
	 * entity store implementation documentation to see if the entity store
	 * supports case-sensitive querying.
	 * @param isCaseSensitive True if the query on metadata should be case-sensitive.
	 */
	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}

	/**
	 * Gets the comparator for the query.
	 * @return The comparator for the query.
	 */
	public String getComparator() {
		return comparator;
	}

	/**
	 * Sets the comparator for the query.
	 * @param comparator THe comparator for the query.
	 */
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}
	
}