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
package com.mtnfog.test.entitydb.eql.filters;

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

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.eql.filters.EqlFilters;
import com.mtnfog.entitydb.eql.filters.comparisons.DateComparison;

public class EqlFiltersTest {

	@Test
	public void isMatchTest0() throws Exception {
		
		Entity entity = new Entity("George Washington", 0.5, "person", "[0, 2)");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertTrue(isMatch);
		
	}
	
	@Test
	public void isMatchTest1() throws Exception {
		
		Entity entity = new Entity("entity1", 0.5, "person", "[0, 2)");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities where text = \"entity1\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertTrue(isMatch);
		
	}
	
	@Test
	public void isMatchTest2() throws Exception {
		
		Entity entity = new Entity("entity2", 0.5, "person", "[0, 2)");
		
		entity.setContext("context");
		entity.setDocumentId("documentid");
		
		String eqlStatement = "select * from entities where text = \"entity1\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertFalse(isMatch);
		
	}
	
	@Test
	public void isMatchTest3() throws Exception {
		
		Entity entity1 = new Entity("entity2", 0.5, "person", "[0, 2)");
		Entity entity2 = new Entity("blah", 0.5, "person", "[0, 2)");
		
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(entity1);
		entities.add(entity2);
		
		String eqlStatement = "select * from entities where text = \"entity1\" and text != \"blah\"";
		
		Collection<Entity> matchedEntities = EqlFilters.filterEntities(entities, eqlStatement);
		
		assertEquals(0, matchedEntities.size());
		
	}
	
	@Test
	public void isMatchTest4() throws Exception {
		
		Entity entity = new Entity("entity", 0.5, "person", "[0, 2)");
		
		String eqlStatement = "select * from entities where text = \"entity\" and type = \"place\"";
		
		boolean isMatch = EqlFilters.isMatch(entity, eqlStatement);
		
		assertFalse(isMatch);
		
	}
	
	@Test
	public void filterEntitiesText() throws Exception {
		
		Collection<Entity> entities = new ArrayList<>();
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.5, "person", "[0, 2)");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.5, "person", "[0, 2)");
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
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)");
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
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)");
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
		
		Entity entity1 = new Entity("entity1", 0.5, "person", "[0, 2)");
		entity1.setContext("context");
		entity1.setDocumentId("documentid");
		
		Entity entity2 = new Entity("entity2", 0.6, "person", "[0, 2)");
		entity2.setContext("context");
		entity2.setDocumentId("documentid");
		
		Entity entity3 = new Entity("entity3", 0.7, "person", "[0, 2)");
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
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)");
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
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)");
		entity.setMetadata(metadata);
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(), DateComparison.AFTER);
		
		assertEquals(0, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter3() {
		
		// Person entities should not be filtered out by a date filter.
		
		Entity entity = new Entity("entity1", 0.5, "person", "[0, 2)");
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(), DateComparison.BEFORE);
		
		assertEquals(1, filteredEntities.size());
		
	}
	
	@Test
	public void datesFilter4() {
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("time", "1464027942965");
		
		Entity entity = new Entity("entity1", 0.5, "date", "[0, 2)");
		entity.setMetadata(metadata);
		
		Map<String, String> metadata2 = new HashMap<String, String>();
		metadata2.put("time", "1464020000000");
		
		Entity entity2 = new Entity("entity1", 0.5, "date", "[0, 2)");
		entity2.setMetadata(metadata2);
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		Collection<Entity> filteredEntities = EqlFilters.filterEntities(entities, new Date(Long.valueOf("1464027942910")), 30);
		
		assertEquals(1, filteredEntities.size());
		
	}
	
}