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
package ai.philterd.entitydb.eql.model;

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
