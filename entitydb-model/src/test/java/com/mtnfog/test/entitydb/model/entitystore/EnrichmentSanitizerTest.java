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
package com.mtnfog.test.entitydb.model.entitystore;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.entitystore.MetadataSanitizer;
import com.mtnfog.test.entity.utils.EntityUtils;

public class EnrichmentSanitizerTest {
	
	private static final Logger LOGGER = LogManager.getLogger(EnrichmentSanitizerTest.class);

	@Test
	public void sanitizeEnrichmentsTest() {
		
		Map<String, String> enrichments = new HashMap<String, String>();
		enrichments.put("te st", "value");
		
		Map<String, String> sanitizedEnrichments = MetadataSanitizer.sanitizeMetadata(enrichments);
		
		for(String k : sanitizedEnrichments.keySet()) {
			
			assertFalse(k.contains(" "));
			assertEquals("te_st", k);
		}
		
	}
	
	@Test
	public void sanitizeRandomEnrichmentsTest() {
		
		for(int x=0; x<10; x++) {
		
			Entity e1 = EntityUtils.createRandomPersonEntity();
			
			Map<String, String> sanitizedEnrichments = MetadataSanitizer.sanitizeMetadata(e1.getMetadata());
			
			for(String k : sanitizedEnrichments.keySet()) {
				
				LOGGER.info("Verifying sanitized entity: {}", k);
				
				assertFalse(k.contains(" "));
				assertFalse(k.contains("."));
				assertFalse(k.contains("!"));
				assertFalse(k.contains("@"));
				assertFalse(k.contains("#"));
				assertFalse(k.contains("$"));
				assertFalse(k.contains("%"));
				assertFalse(k.contains("^"));
				assertFalse(k.contains("*"));
				assertFalse(k.contains("("));
				assertFalse(k.contains(")"));
				assertFalse(k.contains("{"));
				assertFalse(k.contains("}"));
				assertFalse(k.contains("["));
				assertFalse(k.contains("]"));
				
			}
		
		}
		
	}

}