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

import java.util.HashMap;
import java.util.Map;

/**
 * Sanitizer for entity metadata.
 * 
 * @author Philterd, LLC
 *
 */
public class MetadataSanitizer {

	/**
	 * Removes all non-alphanumeric characters from the key.
	 * @param key The key.
	 * @return The key with all non-alphanumeric characters removed.
	 */
	public static String sanitizeKey(String key) {
		
		return key.replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
		
	}
	
	/**
	 * Sanitized the metadata names prior to insertion into the database.
	 * @param metadata The metadata to sanitized.
	 * @return The sanitized metadata.
	 */
	public static Map<String, String> sanitizeMetadata(Map<String, String> metadata) {
		
		// It is a good idea (and required for MongoDB) to 
		// sanitize the metadata (field) names prior to storing.
		// The metadata value does not need sanitized.
		
		Map<String, String> sanitizedMetadata = new HashMap<String, String>();
		
		for(String key : metadata.keySet()) {
			
			sanitizedMetadata.put(sanitizeKey(key), metadata.get(key));
			
		}
		
		return sanitizedMetadata;
		
	}
	
}