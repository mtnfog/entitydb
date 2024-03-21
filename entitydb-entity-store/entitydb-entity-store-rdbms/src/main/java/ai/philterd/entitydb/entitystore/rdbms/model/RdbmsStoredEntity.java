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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.entitystore.AbstractStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.MetadataSanitizer;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.security.Acl;

/**
 * An entity stored by an implementation of {@link EntityStore}.
 * 
 * @author Philterd, LLC
 *
 */
public class RdbmsStoredEntity extends AbstractStoredEntity {

	private String id;
	private String text;
	private String type;
	private String context;
	private String documentId;
	private double confidence;
	private long extractionDate;
	private String language;
	private String acl;
	private String uri;
	private int visible = 1;
	private long timestamp = System.currentTimeMillis();
	private Set<RdbmsStoredEntityMetadata> metadata;
	private long indexed = 0;
	
	/**
	 * Creates a new {@link RdbmsStoredEntity}.
	 */
	public RdbmsStoredEntity() {
				
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndexedEntity toIndexedEntity() throws MalformedAclException {
				
		IndexedEntity indexedEntity = new IndexedEntity();

		indexedEntity.setEntityId(getId());
		indexedEntity.setText(getText());
		indexedEntity.setType(getType());
		indexedEntity.setContext(getContext());
		indexedEntity.setDocumentId(getDocumentId());
		indexedEntity.setConfidence(getConfidence());
		indexedEntity.setExtractionDate(getExtractionDate());
		indexedEntity.setUri(getUri());
		indexedEntity.setLanguageCode(getLanguage());
		indexedEntity.setAcl(new Acl(getAcl()));
		
		Map<String, String> metadata = new HashMap<String, String>();
		
		for(RdbmsStoredEntityMetadata m : getMetadata()) {
			
			metadata.put(m.getName(), m.getValue());
			
		}
		
		indexedEntity.setMetadata(metadata);
		
		return indexedEntity;
		
	}
	
	/**
	 * Creates a new {@link RdbmsStoredEntity}.
	 * @param entity The {@link Entity} to be stored.
	 * @param context The context under which this entity was extracted.
	 * @param documentId The document ID under which this entity was extracted.
	 * @param acl The entity's ACL.
	 * @return A {@link RdbmsStoredEntity}.
	 */
	public static RdbmsStoredEntity fromEntity(Entity entity, String acl) {
		
		RdbmsStoredEntity storedEntity = new RdbmsStoredEntity();
		storedEntity.setId(EntityIdGenerator.generateEntityId(entity, acl).toString());
		storedEntity.setText(entity.getText());
		storedEntity.setType(entity.getType());
		storedEntity.setConfidence(entity.getConfidence());
		storedEntity.setExtractionDate(System.currentTimeMillis());
		storedEntity.setContext(entity.getContext());
		storedEntity.setDocumentId(entity.getDocumentId());
		storedEntity.setUri(entity.getUri());
		storedEntity.setLanguage(entity.getLanguageCode());
		storedEntity.setAcl(acl);
		
		// Convert the metadata to StoredEntityMetadata.
		
		Set<RdbmsStoredEntityMetadata> metadata = new HashSet<RdbmsStoredEntityMetadata>();
		
		if(entity.getMetadata() != null) {
			
			Map<String, String> sanitizedMetadata = MetadataSanitizer.sanitizeMetadata(entity.getMetadata());
		
			for(String key : sanitizedMetadata.keySet()) {

				RdbmsStoredEntityMetadata m = new RdbmsStoredEntityMetadata();
				m.setName(key);
				m.setValue(sanitizedMetadata.get(key));
				m.setEntity(storedEntity);			
				
				metadata.add(m);
				
			}
		
		}
		
		storedEntity.setMetadata(metadata);

		return storedEntity;
		
	}
		
	@Override
    public boolean equals(Object o) {
		
        if(!(o instanceof RdbmsStoredEntity)) {
            return false;
        }
        
        RdbmsStoredEntity storedEntity = (RdbmsStoredEntity) o;
      
        EqualsBuilder builder = new EqualsBuilder();
        
        builder.append(text, storedEntity.getText());
        builder.append(type, storedEntity.getType());
        builder.append(context, storedEntity.getContext());
        builder.append(confidence, storedEntity.getConfidence());
        builder.append(extractionDate, storedEntity.getExtractionDate());
        builder.append(uri, storedEntity.getUri());
        builder.append(acl, storedEntity.getAcl());
        builder.append(language, storedEntity.getLanguage());
        builder.append(indexed, storedEntity.getIndexed());
        
        return builder.isEquals();
        
    }

    @Override
    public int hashCode() {
    	
        HashCodeBuilder builder = new HashCodeBuilder();
        
        builder.append(text);
        builder.append(type);
        builder.append(context);
        builder.append(confidence);
        builder.append(extractionDate);
        builder.append(uri);
        builder.append(acl);
        builder.append(language);
        builder.append(indexed);
        
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
	 * Gets the ID.
	 * @return The ID.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the database-assigned ID value.
	 * @param id The database-assigned ID value.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the text of the entity.
	 * @return The text of the entity.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of the entity.
	 * @param text The text of the entity.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the type of the entity.
	 * @return The type of the entity.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the entity.
	 * @param type The type of the entity.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the context of the entity.
	 * @return The context of the entity.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the context of the entity.
	 * @param context The context of the entity.
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Gets the confidence of the entity.
	 * @return The confidence of the entity.
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Sets the confidence of the entity.
	 * @param confidence The confidence of the entity.
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/**
	 * Gets the extraction date.
	 * @return The extraction date.
	 */
	public long getExtractionDate() {
		return extractionDate;
	}

	/**
	 * Sets the extraction date.
	 * @param extractionDate The extraction date.
	 */
	public void setExtractionDate(long extractionDate) {
		this.extractionDate = extractionDate;
	}

	/**
	 * Gets the entity URI.
	 * @return The entity URI.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the entity URI.
	 * @param uri The entity URI.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Gets the document ID.
	 * @return The document ID.
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * Sets the document ID.
	 * @param documentId The document ID.
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * Gets the entity's language.
	 * @return The entity's language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the entity's language.
	 * @param language The entity's language.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the entity's ACL.
	 * @return The entity's ACL.
	 */
	public String getAcl() {
		return acl;
	}

	/**
	 * Sets the entity's ACL.
	 * @param acl The entity's ACL.
	 */
	public void setAcl(String acl) {
		this.acl = acl;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getIndexed() {
		return indexed;
	}

	public void setIndexed(long indexed) {
		this.indexed = indexed;
	}

	public Set<RdbmsStoredEntityMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Set<RdbmsStoredEntityMetadata> metadata) {
		this.metadata = metadata;
	}
	
}