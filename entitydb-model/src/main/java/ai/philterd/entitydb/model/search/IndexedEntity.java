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
package ai.philterd.entitydb.model.search;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.security.Acl;

import io.searchbox.annotations.JestId;
import io.searchbox.annotations.JestVersion;

/**
 * An entity indexed in Elasticsearch. It is the same as an
 * {@link Entity entity} from which it is created but with
 * additional <code>entityId</code> property.
 * 
 * The <code>entityId</code> is
 * annotated to be set to the document's ID. See:
 * http://stackoverflow.com/a/33662542
 * 
 * @author Philterd, LLC
 *
 */
public class IndexedEntity extends Entity {

	private static final long serialVersionUID = 7914241851081226141L;
	
	@JestId
	private String entityId;
	
	@JestVersion
	private Long documentVersion;
	
	private long transactionId;
	private Acl acl;
	
	public IndexedEntity(String entityId) {
		
		this.entityId = entityId;
		
	}
	
	public IndexedEntity() {
		
	}
	
	public static IndexedEntity fromEntity(Entity entity, String entityId, String acl) throws MalformedAclException {
		
		return fromEntity(entity, entityId, new Acl(acl));
		
	}
	
	public static IndexedEntity fromEntity(Entity entity, String entityId, Acl acl) {
		
		IndexedEntity indexedEntity = new IndexedEntity(entityId);
		indexedEntity.setEntityId(entityId);
		indexedEntity.setContext(entity.getContext());
		indexedEntity.setDocumentId(entity.getDocumentId());
		indexedEntity.setText(entity.getText());
		indexedEntity.setConfidence(entity.getConfidence());
		indexedEntity.setLanguageCode(entity.getLanguageCode());
		indexedEntity.setUri(entity.getUri());
		indexedEntity.setMetadata(entity.getMetadata());
		indexedEntity.setType(entity.getType());
		indexedEntity.setSpan(entity.getSpan());
		indexedEntity.setAcl(acl);
		
		return indexedEntity;
		
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Long getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(Long documentVersion) {
		this.documentVersion = documentVersion;
	}

	public Acl getAcl() {
		return acl;
	}

	public void setAcl(Acl acl) {
		this.acl = acl;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

}