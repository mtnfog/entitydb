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
package ai.philterd.test.entitydb.model.entitystore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.entitystore.MetadataSanitizer;
import com.mtnfog.test.entity.utils.RandomEntityUtils;

public class MetadataSanitizerTest {
	
	private static final Logger LOGGER = LogManager.getLogger(MetadataSanitizerTest.class);

	@Test
	public void sanitizeMetadataTest() {
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("te st", "value");
		
		Map<String, String> sanitizedMetadata = MetadataSanitizer.sanitizeMetadata(metadata);
		
		for(String k : sanitizedMetadata.keySet()) {
			
			assertFalse(k.contains(" "));
			assertEquals("te_st", k);
		}
		
	}
	
	@Test
	public void sanitizeRandomMetadataTest() {
		
		for(int x=0; x<10; x++) {
		
			Entity e1 = RandomEntityUtils.createRandomPersonEntity();
			
			Map<String, String> sanitizedMetadata = MetadataSanitizer.sanitizeMetadata(e1.getMetadata());
			
			for(String k : sanitizedMetadata.keySet()) {
				
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