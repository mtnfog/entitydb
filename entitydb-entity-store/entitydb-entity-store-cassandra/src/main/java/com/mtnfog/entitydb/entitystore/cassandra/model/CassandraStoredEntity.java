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
package com.mtnfog.entitydb.entitystore.cassandra.model;

import java.util.Map;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.entitystore.AbstractStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EnrichmentSanitizer;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.security.Acl;

public class CassandraStoredEntity extends AbstractStoredEntity {

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
	
	public static CassandraStoredEntity fromEntity(Entity entity, String acl) {
		
		CassandraStoredEntity cassandraStoredEntity = new CassandraStoredEntity();
		
		cassandraStoredEntity.setId(EntityIdGenerator.generateEntityId(entity, acl).toString());
		cassandraStoredEntity.setText(entity.getText());
		cassandraStoredEntity.setType(entity.getType());
		cassandraStoredEntity.setConfidence(entity.getConfidence());
		cassandraStoredEntity.setExtractionDate(System.currentTimeMillis());
		cassandraStoredEntity.setContext(entity.getContext());
		cassandraStoredEntity.setDocumentId(entity.getDocumentId());
		cassandraStoredEntity.setUri(entity.getUri());
		cassandraStoredEntity.setLanguage(entity.getLanguageCode());
		cassandraStoredEntity.setAcl(acl);
		cassandraStoredEntity.setEnrichments(EnrichmentSanitizer.sanitizeEnrichments(entity.getEnrichments()));
		
		return cassandraStoredEntity;
		
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
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public long getExtractionDate() {
		return extractionDate;
	}

	public void setExtractionDate(long extractionDate) {
		this.extractionDate = extractionDate;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, String> getEnrichments() {
		return enrichments;
	}

	public void setEnrichments(Map<String, String> enrichments) {
		this.enrichments = enrichments;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getAcl() {
		return acl;
	}

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