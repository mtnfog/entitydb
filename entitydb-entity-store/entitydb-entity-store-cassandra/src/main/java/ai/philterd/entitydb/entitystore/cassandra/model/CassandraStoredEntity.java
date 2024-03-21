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
package ai.philterd.entitydb.entitystore.cassandra.model;

import java.util.Map;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.entitystore.AbstractStoredEntity;
import ai.philterd.entitydb.model.entitystore.MetadataSanitizer;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.security.Acl;

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
	private Map<String, String> metadata;
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
		cassandraStoredEntity.setMetadata(MetadataSanitizer.sanitizeMetadata(entity.getMetadata()));
		
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
		indexedEntity.setMetadata(getMetadata());
		
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

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
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