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
package com.mtnfog.entitydb.entitystore.rdbms.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * An attribute for a stored entity. An instance of this class
 * corresponds to a fact of an {@link EnrichedEntity}.
 * 
 * @author Mountain Fog, Inc.
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
        
        RdbmsStoredEntityMetadata storedEntityEnrichment = (RdbmsStoredEntityMetadata) o;
      
        EqualsBuilder builder = new EqualsBuilder();
        
        builder.append(name, storedEntityEnrichment.getName());
        builder.append(value, storedEntityEnrichment.getValue());
        
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