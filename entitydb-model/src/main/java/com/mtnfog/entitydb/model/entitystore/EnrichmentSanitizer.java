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

import java.util.HashMap;
import java.util.Map;

/**
 * Sanitizer for entity enrichments.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EnrichmentSanitizer {

	/**
	 * Removes all non-alphanumeric characters from the key.
	 * @param key The key.
	 * @return The key with all non-alphanumeric characters removed.
	 */
	public static String sanitizeKey(String key) {
		
		return key.replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
		
	}
	
	/**
	 * Sanitized the enrichment names prior to insertion into the database.
	 * @param enrichments The enrichments to sanitized.
	 * @return The sanitized enrichments.
	 */
	public static Map<String, String> sanitizeEnrichments(Map<String, String> enrichments) {
		
		// As noted in IDYLSDK-419 it is a good idea (and required for MongoDB) to 
		// sanitize the enrichment (field) names prior to storing.
		// The enrichment value does not need sanitized.
		
		Map<String, String> sanitizedEnrichments = new HashMap<String, String>();
		
		for(String key : enrichments.keySet()) {
			
			sanitizedEnrichments.put(sanitizeKey(key), enrichments.get(key));
			
		}
		
		return sanitizedEnrichments;
		
	}
	
}