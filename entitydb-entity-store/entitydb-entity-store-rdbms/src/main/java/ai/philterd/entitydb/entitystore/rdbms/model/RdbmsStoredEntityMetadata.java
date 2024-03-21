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
package ai.philterd.entitydb.entitystore.rdbms.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * An attribute for a stored entity. An instance of this class
 * corresponds to a fact of an {@link EnrichedEntity}.
 * 
 * @author Philterd, LLC
 *
 */
public class RdbmsStoredEntityMetadata {

	private int id;
	private String name;
	private String value;
	private RdbmsStoredEntity entity;
	
	/**
	 * Creates a new {@link RdbmsStoredEntityMetadata}.
	 */
	public RdbmsStoredEntityMetadata() {
		
	}
	
	/**
	 * Creates a new {@link RdbmsStoredEntityMetadata}.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public RdbmsStoredEntityMetadata(String name, String value) {
		
		this.name = name;
		this.value = value;
		
	}
	
	/**
	 * Creates a new {@link RdbmsStoredEntityMetadata}.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 * @param storedEntity The owning {@link StoredEntity entity}.
	 */
	public RdbmsStoredEntityMetadata(String name, String value, RdbmsStoredEntity storedEntity) {
		
		this.name = name;
		this.value = value;
		this.entity = storedEntity;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean equals(Object o) {
		
        if(!(o instanceof RdbmsStoredEntityMetadata)) {
            return false;
        }
        
        RdbmsStoredEntityMetadata storedEntityMetadata = (RdbmsStoredEntityMetadata) o;
      
        EqualsBuilder builder = new EqualsBuilder();
        
        builder.append(name, storedEntityMetadata.getName());
        builder.append(value, storedEntityMetadata.getValue());
        
        return builder.isEquals();
        
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public int hashCode() {
    	
        HashCodeBuilder builder = new HashCodeBuilder();
        
        builder.append(name);
        builder.append(value);
        
        return builder.hashCode();
        
    }
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public String toString() {
    	return ReflectionToStringBuilder.toString(this);
	}
	
    /**
     * Gets the database-assigned ID.
     * @return The database-signed ID.
     */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the database-assigned ID.
	 * @param id The database-assigned ID.
	 */
	public void setId(int id) {
		this.id = id;
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
	 * Gets the associated stored entity.
	 * @return The {@link StoredEntity entity}.
	 */
	public RdbmsStoredEntity getEntity() {
		return entity;
	}

	/**
	 * Sets the associated stored entity.
	 * @param storedEntity The {@link StoredEntity entity}.
	 */
	public void setEntity(RdbmsStoredEntity storedEntity) {
		this.entity = storedEntity;
	}
	
}