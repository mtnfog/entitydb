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
