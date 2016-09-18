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
package com.mtnfog.entitydb.model.search;

import io.searchbox.annotations.JestId;
import io.searchbox.annotations.JestVersion;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.security.Acl;

/**
 * An entity indexed in Elasticsearch. It is the same as an
 * {@link Entity entity} from which it is created but with
 * additional <code>entityId</code> property.
 * 
 * The <code>entityId</code> is
 * annotated to be set to the document's ID. See:
 * http://stackoverflow.com/a/33662542
 * 
 * @author Mountain Fog, Inc.
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
		indexedEntity.setEnrichments(entity.getEnrichments());
		indexedEntity.setType(entity.getType());
		indexedEntity.setLocations(entity.getLocations());
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