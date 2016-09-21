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
package com.mtnfog.entitydb.entitystore.mongodb.model;

import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mtnfog.entitydb.model.entitystore.AbstractStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EnrichmentSanitizer;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entity.Entity;

/**
 * Entity able to be persisted to MongoDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class MongoDBStoredEntity extends AbstractStoredEntity implements Bson {
	
	private String id;
	private String text;
	private String type;
	private String context;
	private String documentId;
	private double confidence;
	private long extractionDate;
	private String uri;
	private String language;
	private String acl;
	private int visible = 1;
	private long timestamp = System.currentTimeMillis();
	private Map<String, String> enrichments;
	private long indexed = 0;
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<MongoDBStoredEntity>(this, codecRegistry.get(MongoDBStoredEntity.class));
	}
	
	
	/**
	 * Creates a new {@link MongoDBStoredEntity}.
	 * @param entity The {@link Entity} to be stored.
	 * @param context The context under which this entity was extracted.
	 * @param documentId The ID of the document containing this entity.
	 * @param acl The entity's ACL.
	 * @return A {@link MongoDBStoredEntity}.
	 */
	public static MongoDBStoredEntity fromEntity(Entity entity, String acl) {
		
		MongoDBStoredEntity storedEntity = new MongoDBStoredEntity();
		storedEntity.setId(EntityIdGenerator.generateEntityId(entity, acl));
		storedEntity.setText(entity.getText());
		storedEntity.setType(entity.getType());
		storedEntity.setConfidence(entity.getConfidence());
		storedEntity.setExtractionDate(System.currentTimeMillis());
		storedEntity.setContext(entity.getContext());
		storedEntity.setDocumentId(entity.getDocumentId());
		storedEntity.setUri(entity.getUri());
		storedEntity.setLanguage(entity.getLanguageCode());
		storedEntity.setAcl(acl);
		storedEntity.setEnrichments(EnrichmentSanitizer.sanitizeEnrichments(entity.getEnrichments()));
				
		return storedEntity;
		
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
		indexedEntity.setEnrichments(getEnrichments());
		
		return indexedEntity;
		
	}
	
	@Override
	public String toString() {
		
		return ToStringBuilder.reflectionToString(this);
		
	}
			
	/**
	 * Gets the ID value.
	 * @return The ID value.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the ID value.
	 * @param id The ID value.
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
	 * Gets the entity enrichments.
	 * @return The entity enrichments.
	 */
	public Map<String, String> getEnrichments() {
		return enrichments;
	}

	/**
	 * Sets the entity enrichments.
	 * @param enrichments The entity enrichments.
	 */
	public void setEnrichments(Map<String, String> enrichments) {
		this.enrichments = enrichments;
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

}