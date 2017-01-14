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
package com.mtnfog.entitydb.entitystore.dynamodb.model;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.entitystore.dynamodb.DynamoDBEntityStore;
import com.mtnfog.entitydb.model.entitystore.AbstractStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.entitystore.MetadataSanitizer;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.security.Acl;

/**
 * Entity able to be persisted to AWS DynamoDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@DynamoDBTable(tableName=DynamoDBEntityStore.DEFAULT_TABLE_NAME)
public class DynamoDBStoredEntity extends AbstractStoredEntity {

	/**
	 * The id attribute.
	 */
	public static final String FIELD_ID = "id";
	
	/**
	 * The entity text attribute.
	 */
	public static final String FIELD_TEXT = "text";
	
	/**
	 * The entity type attribute.
	 */
	public static final String FIELD_TYPE = "type";
	
	/**
	 * The context attribute.
	 */
	public static final String FIELD_CONTEXT = "context";
	
	/**
	 * The document ID attribute.
	 */
	public static final String FIELD_DOCUMENT_ID = "documentId";
	
	/**
	 * The confidence attribute.
	 */
	public static final String FIELD_CONFIDENCE = "confidence";
	
	/**
	 * The extraction date attribute.
	 */
	public static final String FIELD_EXTRACTION_DATE = "extractionDate";
	
	/**
	 * The URI (disambiguated) attribute.
	 */
	public static final String FIELD_URI = "uri";
	
	/**
	 * The visible attribute.
	 */
	public static final String FIELD_VISIBLE = "visible";
	
	/**
	 * The timestamp attribute.
	 */
	public static final String FIELD_TIMESTAMP = "timestamp";
	
	/**
	 * The entity's language.
	 */
	public static final String FIELD_LANGUAGE = "language";
	
	/**
	 * The enrichments attribute.
	 */
	public static final String FIELD_METADATA = "metadata";
	
	/**
	 * The ACL attribute.
	 */
	public static final String FIELD_ACL = "acl";
	
	/**
	 * The indexed attribute.
	 */
	public static final String FIELD_INDEXED = "indexed";
	
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
	private Map<String, String> metadata;
	private long indexed = 0;
	
	/**
	 * Creates a new {@link StoredEntity}.
	 * @param entity The {@link Entity} to be stored.
	 * @param context The context under which this entity was extracted.
	 * @param documentId The ID of the document containing this entity.
	 * @param acl The entity's ACL.
	 * @return A {@link StoredEntity}.
	 */
	public static DynamoDBStoredEntity fromEntity(Entity entity, String acl) {
		
		DynamoDBStoredEntity storedEntity = new DynamoDBStoredEntity();
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
		storedEntity.setMetadata(MetadataSanitizer.sanitizeMetadata(entity.getMetadata()));
		
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
		indexedEntity.setMetadata(getMetadata());
		
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
	@Override
	@DynamoDBHashKey(attributeName=FIELD_ID)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_TEXT)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_TYPE)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_CONTEXT)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_CONFIDENCE)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_EXTRACTION_DATE)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_URI)
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
	 * Gets the entity getMetadata.
	 * @return The entity getMetadata.
	 */
	@DynamoDBAttribute(attributeName=FIELD_METADATA)
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Sets the entity metadata.
	 * @param metadata The entity metadata.
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Gets the document ID.
	 * @return The document ID.
	 */
	@Override
	@DynamoDBAttribute(attributeName=FIELD_DOCUMENT_ID)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_LANGUAGE)
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
	@Override
	@DynamoDBAttribute(attributeName=FIELD_ACL)
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
	
	@Override
	@DynamoDBAttribute(attributeName=FIELD_VISIBLE)
	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	@Override
	@DynamoDBAttribute(attributeName=FIELD_TIMESTAMP)
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	@DynamoDBIndexRangeKey(attributeName=FIELD_INDEXED, localSecondaryIndexName="indexedIndex")
	public long getIndexed() {
		return indexed;
	}

	public void setIndexed(long indexed) {
		this.indexed = indexed;
	}
		
}