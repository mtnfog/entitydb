/*******************************************************************************
 * Copyright 2024 Philterd, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package ai.philterd.entitydb.test.eql.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ai.philterd.entitydb.eql.filters.EqlFilters;
import ai.philterd.entitydb.eql.filters.DateComparison;
import ai.philterd.entitydb.model.entity.Entity;

public class EqlFiltersTest {

	@Test
	public void isMatchTest0() throws Exception {
		
		Entity entity = new Entity("George Washington", 0.5, "person", "[0, 2)", "context", "docid");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertTrue(isMatch);
		
	}
	
	@Test
	public void isMatchTest1() throws Exception {
		
		Entity entity = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities where text = \"entity1\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertTrue(isMatch);
		
	}
	
	@Test
	public void isMatchTest2() throws Exception {
		
		Entity entity = new Entity("entity2", 0.5, "person", "[0, 2)", "context", "docid");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities where text = \"entity1\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertFalse(isMatch);
		
	}
	
	@Test
	public void isMatchTest3() throws Exception {
		
		Entity entity1 = new Entity("entity2", 0.5, "person", "[0, 2)", "context", "docid");
		Entity entity2 = new Entity("blah", 0.5, "person", "[0, 2)", "context", "docid");
		
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(entity1);
		entities.add(entity2);
		
		String eqlStatement = "select * from entities where text = \"entity1\" and text != \"blah\"";
		
		Collection<Entity> matchedEntities = EqlFilters.filterEntities(entities, eqlStatement);
		
		assertEquals(0, matchedEntities.size());
		
	}
	
	@Test
	public void isMatchTest4() throws Exception {
		
		Entity entity = new Entity("entity", 0.5, "person", "[0, 2)", "context", "docid");
		
		String eqlStatement = "select * from entities where text = \"entity\" and type = \"place\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertFalse(isMatch);
		
	}
	
	@Test
	public void filterEntitiesText() throws Exception {
		
		Collection<Entity> entities = new ArrayList<>();
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.5, "person", "[0, 2)", "context", "docid");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.5, "person", "[0, 2)", "context", "docid");
		entity3.setContext("context");
		entity3.setDocumentId("documentid");
		
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
			
		List<String> eqlStatements = new ArrayList<>();
		eqlStatements.add("select * from entities where text = \"entity2\"");
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, eqlStatements);
			
		assertEquals(1, filteredEntities.size());
		assertEquals("entity2", filteredEntities.iterator().next().getText());
		
	}
	
	@Test
	public void filterEntitiesConfidence() throws Exception {
		
		Collection<Entity> entities = new ArrayList<>();
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)", "context", "docid");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)", "context", "docid");
		entity3.setContext("context");
		entity3.setDocumentId("documentid");
		
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		
		List<String> eqlStatements = new ArrayList<>();
		eqlStatements.add("select * from entities where confidence = 50");
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, eqlStatements);
			
		assertEquals(1, filteredEntities.size());
		assertEquals("entity1", filteredEntities.iterator().next().getText());
		
	}
	
	@Test
	public void filterEntitiesConfidenceBetween() throws Exception {
		
		Collection<Entity> entities = new ArrayList<>();
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)", "context", "docid");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)", "context", "docid");
		entity3.setContext("context");
		entity3.setDocumentId("documentid");
		
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		
		List<String> eqlStatements = new ArrayList<>();
		eqlStatements.add("select * from entities where confidence between 45 and 55");
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, eqlStatements);
			
		assertEquals(1, filteredEntities.size());
		assertEquals("entity1", filteredEntities.iterator().next().getText());
		
	}
	
	@Test
	public void filterEntitiesContext() throws Exception {
		
		Collection<Entity> entities = new ArrayList<>();
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)", "context", "docid");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)", "context", "docid");
		entity3.setContext("context");
		entity3.setDocumentId("documentid");
		
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
		
		List<String> eqlStatements = new ArrayList<>();
		eqlStatements.add("select * from entities where context = \"test\"");
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, eqlStatements);
			
		assertEquals(0, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter1() {
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("time", "1464027942965");
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)", "context", "docid");
		entity.setMetadata(metadata);
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(), DateComparison.BEFORE);
		
		assertEquals(1, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter2() {
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("time", "1464027942965");
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)", "context", "docid");
		entity.setMetadata(metadata);
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(), DateComparison.AFTER);
		
		assertEquals(0, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter3() {
		
		// Person entities should not be filtered out by a date filter.
		
		Entity entity = new Entity("entity1", 0.5, "person", "[0, 2)", "context", "docid");
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(), DateComparison.BEFORE);
		
		assertEquals(1, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter4() {
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("time", "1464027942965");
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)", "context", "docid");
		entity.setMetadata(metadata);
		
		Map<String, String> metadata2 = new HashMap<String, String>();
		metadata2.put("time", "1464020000000");
		
		Entity entity2 = new Entity("entity1", 0.5, "date", "[0, 2)", "context", "docid");
		entity2.setMetadata(metadata2);
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(Long.valueOf("1464027942910")), 30);
		
		assertEquals(1, filteredEntities.size());
		
	}
	
}
