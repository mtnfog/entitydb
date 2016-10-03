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
package com.mtnfog.entitydb.model.entitystore;

import com.mtnfog.entity.Entity;

/**
 * Generates entity IDs.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EntityIdGenerator {
	
	private EntityIdGenerator() {
		// This is a utility class.
	}
	
	public static String generateEntityId(Entity entity, String acl) {
		
		return generateEntityId(entity.getText(), entity.getConfidence(), entity.getLanguageCode(), entity.getContext(), entity.getDocumentId(), acl);
		
	}
	
	public static String generateEntityId(String entityText, double confidence, String entityLanguage, String context, String documentId, String acl) {
		
		String encodedEntity = String.format("%s:%s:%s:%s:%s:%s", entityText, confidence, entityLanguage, context, documentId, acl);
		
		String entityId = org.apache.commons.codec.digest.DigestUtils.sha256Hex(encodedEntity);   
		
		return entityId;
		
	}
	
}