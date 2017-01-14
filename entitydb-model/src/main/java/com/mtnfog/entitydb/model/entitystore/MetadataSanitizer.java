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
package com.mtnfog.entitydb.model.entitystore;

import java.util.HashMap;
import java.util.Map;

/**
 * Sanitizer for entity metadata.
 * 
 * @author Mountain Fog, Inc.
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