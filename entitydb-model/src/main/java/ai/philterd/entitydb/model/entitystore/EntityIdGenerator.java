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
package ai.philterd.entitydb.model.entitystore;

import ai.philterd.entitydb.model.entity.Entity;

/**
 * Generates entity IDs.
 * 
 * @author Philterd, LLC
 *
 */
public class EntityIdGenerator {
	
	private EntityIdGenerator() {
		// This is a utility class.
	}
	
	/**
	 * Generates an entity ID. An entity ID is a fingerprint that uniquely identifies an entity.
	 * @param entity The {@link Entity entity}.
	 * @param acl The entity's ACL.
	 * @return The entity's ID.
	 */
	public static String generateEntityId(Entity entity, String acl) {
		
		return generateEntityId(entity.getText(), entity.getConfidence(), entity.getLanguageCode(), entity.getContext(), entity.getDocumentId(), acl);
		
	}
	
	/**
	 * Generates an entity ID. An entity ID is a fingerprint that uniquely identifies an entity.
	 * @param entityText The text of the entity.
	 * @param confidence The confidence of the entity.
	 * @param entityLanguage The language of the entity.
	 * @param context The context of the entity.
	 * @param documentId The document ID of the entity.
	 * @param acl The entity's ACL.
	 * @return The entity's ID.
	 */
	public static String generateEntityId(String entityText, double confidence, String entityLanguage, String context, String documentId, String acl) {
		
		String encodedEntity = String.format("%s:%s:%s:%s:%s:%s", entityText, confidence, entityLanguage, context, documentId, acl);
		
		String entityId = org.apache.commons.codec.digest.DigestUtils.sha256Hex(encodedEntity);   
		
		return entityId;
		
	}
	
}