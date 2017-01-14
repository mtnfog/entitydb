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
package com.mtnfog.test.entitydb.eql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.model.EntityOrder;
import com.mtnfog.entitydb.eql.model.EntityQuery;
import com.mtnfog.entitydb.eql.model.SortOrder;

public class EqlTest {

	private static final Logger LOGGER = LogManager.getLogger(EqlTest.class);
	
	@Test
	public void semicolon1() throws Exception {
		
		String uri = "http://mtnfog.com/1.0/George_Washington";
		String query = String.format("select * from entities;", uri);
		LOGGER.debug("Query: {}", query);
		
		EntityQuery entityQuery = Eql.generate(query);
        
		assertNotNull(entityQuery);
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
             
	}
	
	@Test
	public void notContext() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context!=\"ctx\"");
        
		assertNotNull(entityQuery);
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertEquals("ctx", entityQuery.getNotContext());

	}
	
	@Test
	public void notContextAndContext() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context!=\"ctx\" and context=\"c\"");
        
		assertNotNull(entityQuery);
		assertEquals("c", entityQuery.getContext());
        assertEquals("ctx", entityQuery.getNotContext());

	}
	
	public void sort1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities order by text");
        
		assertNotNull(entityQuery);
        assertEquals(EntityOrder.TEXT, entityQuery.getEntityOrder());
        
	}
		
	public void sort2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities order by id asc");
        
		assertNotNull(entityQuery);
        assertEquals(EntityOrder.ID, entityQuery.getEntityOrder());
        assertEquals(SortOrder.ASC, entityQuery.getSortOrder());
        
	}
	
	@Test
	public void sort3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities order by id desc");
        
		assertNotNull(entityQuery);
        assertEquals(EntityOrder.ID, entityQuery.getEntityOrder());
        assertEquals(SortOrder.DESC, entityQuery.getSortOrder());
        
	}
	
	@Test
	public void sort4() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities");
        
		assertNotNull(entityQuery);
        assertEquals(EntityOrder.ID, entityQuery.getEntityOrder());
        assertEquals(SortOrder.DESC, entityQuery.getSortOrder());
        
	}
	
	@Test(expected = IllegalStateException.class)
	public void sort5() throws Exception {
		
		LOGGER.info("The space between the sort field and the semicolon is invalid.");
		
		Eql.generate("select * from entities order by text ;");
        
	}
	
	@Test(expected = IllegalStateException.class)
	public void sort6() throws Exception {
		
		LOGGER.info("Invalid sort order.");
		
		Eql.generate("select * from entities order by id asdf");
        
	}
	
	@Test
	public void query1() throws Exception {
		
		LOGGER.info("Testing lack of spaces around the equals sign");
		
		EntityQuery entityQuery = Eql.generate("select * from entities where type=\"Person\"");
        
		assertNotNull(entityQuery);
		assertEquals("person", entityQuery.getType());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void query2() throws Exception {
				
		EntityQuery entityQuery = Eql.generate("select * from entities where type=\"Person\" and language = \"en\"");
        
		assertNotNull(entityQuery);
		assertEquals("person", entityQuery.getType());
		assertEquals("en", entityQuery.getLanguageCode());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
			
	@Test
	public void type1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where type = \"Person\"");
        
		assertNotNull(entityQuery);
		assertEquals("person", entityQuery.getType());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void type2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where type = \"Place\"");
        
		assertNotNull(entityQuery);
		assertEquals("place", entityQuery.getType());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void empty() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities");
        
		assertNotNull(entityQuery);
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void limit1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities limit 20");
        
		assertNotNull(entityQuery);
		assertEquals(20, entityQuery.getLimit());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void limit2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities limit 1500");
        
		assertNotNull(entityQuery);
		assertEquals(1500, entityQuery.getLimit());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void limit3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where metadata \"birth_date\" = \"10-20-1945\" limit 3");
        
		assertNotNull(entityQuery);
		assertEquals(3, entityQuery.getLimit());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        assertEquals(1, entityQuery.getEntityMetadataFilters().size());
		assertEquals("birth_date", entityQuery.getEntityMetadataFilters().get(0).getName());
		assertEquals("10-20-1945", entityQuery.getEntityMetadataFilters().get(0).getValue());
        
	}	
	
	@Test
	public void limit4() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities limit 20 offset 100");
        
		assertNotNull(entityQuery);
		assertEquals(20, entityQuery.getLimit());
		assertEquals(100, entityQuery.getOffset());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        
	}
	
	@Test
	public void offset1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where metadata \"birth_date\" = \"10-20-1945\" limit 3 offset 10");
        
		assertNotNull(entityQuery);
		assertEquals(3, entityQuery.getLimit());
		assertEquals(10, entityQuery.getOffset());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
        assertEquals(1, entityQuery.getEntityMetadataFilters().size());
		assertEquals("birth_date", entityQuery.getEntityMetadataFilters().get(0).getName());
		assertEquals("10-20-1945", entityQuery.getEntityMetadataFilters().get(0).getValue());		
        
	}
	
	@Test
	public void offset2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities offset 10");
        
		assertNotNull(entityQuery);
		assertEquals(10, entityQuery.getOffset());
        assertTrue(StringUtils.isEmpty(entityQuery.getContext()));
        assertTrue(StringUtils.isEmpty(entityQuery.getDocumentId()));
        assertTrue(StringUtils.isEmpty(entityQuery.getText()));
             
	}
		
	@Test
	public void metadata1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where metadata \"birth_date\" = \"10-20-1945\"");
        
		assertNotNull(entityQuery);
		assertEquals(1, entityQuery.getEntityMetadataFilters().size());
		assertEquals("birth_date", entityQuery.getEntityMetadataFilters().get(0).getName());
		assertEquals("10-20-1945", entityQuery.getEntityMetadataFilters().get(0).getValue());
		
	}
	
	@Test
	public void metadata2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where metadata \"birth_date\" = \"10-20-1945\" and metadata \"death_date\" = \"7-13-1987\"");
        
		assertNotNull(entityQuery);
		assertTrue(entityQuery.getEntityMetadataFilters().size() == 2);
		assertEquals("birth_date", entityQuery.getEntityMetadataFilters().get(0).getName());
		assertEquals("10-20-1945", entityQuery.getEntityMetadataFilters().get(0).getValue());
		assertTrue(entityQuery.getEntityMetadataFilters().get(1).getName().equalsIgnoreCase("death_date"));
		assertTrue(entityQuery.getEntityMetadataFilters().get(1).getValue().equalsIgnoreCase("7-13-1987"));
		
	}
	
	@Test
	public void metadata3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where metadata \"birth_date\" = \"10/20/1945\"");
        
		assertNotNull(entityQuery);
		assertEquals(1, entityQuery.getEntityMetadataFilters().size());
		assertEquals("birth_date", entityQuery.getEntityMetadataFilters().get(0).getName());
		assertEquals("10/20/1945", entityQuery.getEntityMetadataFilters().get(0).getValue());
		
	}
	
	@Test
	public void compound1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence = 50 and text = \"test\"");
        
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
		assertEquals(0.5, entityQuery.getConfidenceRange().getMaximum(), 0);
        assertEquals("test", entityQuery.getText());
        
	}
	
	@Test
	public void compound2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context = \"ctx\" and text = \"test\"");
        
		assertNotNull(entityQuery);
		assertEquals("ctx", entityQuery.getContext());
        assertEquals("test", entityQuery.getText());
        
	}
	
	@Test
	public void compound3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context = \"ctx\" and text = \"test\" and documentid = \"docid\"");
        
		assertNotNull(entityQuery);
		assertEquals("ctx", entityQuery.getContext());
        assertEquals("test", entityQuery.getText());
        assertTrue(entityQuery.getDocumentId().contains("docid"));
        
	}
	
	@Test
	public void compound4() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context = \"ctx\" and text = \"test\" and documentid = \"docid\" and confidence = 30");
        
		assertNotNull(entityQuery);
		assertEquals("ctx", entityQuery.getContext());
        assertEquals("test", entityQuery.getText());
        assertTrue(entityQuery.getDocumentId().contains("docid"));
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 0.3);
        
	}
		
	@Test
	public void confidence1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence = 50");
        
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 0.5);
        
	}
	
	@Test
	public void confidence2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence between 25 and 50");
        
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 0.5);
        assertNotNull(entityQuery.getConfidenceRange().getMinimum());
        assertTrue(entityQuery.getConfidenceRange().getMinimum() == 0.25);
        
	}

	@Test
	public void confidence3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence > 25");
        
		LOGGER.info(entityQuery.getConfidenceRange().toString());
		
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 1.0);
        assertNotNull(entityQuery.getConfidenceRange().getMinimum());
        assertTrue(entityQuery.getConfidenceRange().getMinimum() == 0.26);
        
	}
	
	@Test
	public void confidence4() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence < 60");
		
		LOGGER.info(entityQuery.getConfidenceRange().toString());
        
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 0.59);
        assertNotNull(entityQuery.getConfidenceRange().getMinimum());
        assertTrue(entityQuery.getConfidenceRange().getMinimum() == 0.0);
        
	}
	
	@Test
	public void confidence5() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence >= 60");
        
		assertNotNull(entityQuery);
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 1.0);
        assertNotNull(entityQuery.getConfidenceRange().getMinimum());
        assertTrue(entityQuery.getConfidenceRange().getMinimum() == 0.60);
        
	}

	@Test
	public void confidence6() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where confidence >= 60 and text = \"George Washington\"");
        
		assertNotNull(entityQuery);		
		assertNotNull(entityQuery.getConfidenceRange());
		assertNotNull(entityQuery.getConfidenceRange().getMaximum());
        assertTrue(entityQuery.getConfidenceRange().getMaximum() == 1.0);
        assertNotNull(entityQuery.getConfidenceRange().getMinimum());
        assertTrue(entityQuery.getConfidenceRange().getMinimum() == 0.60);
        assertEquals("George Washington", entityQuery.getText());
        
	}
	
	@Test
	public void context() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where context = \"test\"");
        
        assertEquals("test", entityQuery.getContext());
        
	}
	
	@Test
	public void documentid() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where documentid = \"test\"");
        
        assertEquals("test", entityQuery.getDocumentId());
        
	}
	
	@Test
	public void text() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where text = \"test\"");
        
        assertEquals("test", entityQuery.getText());
        
	}
	
	@Test
	public void textSpace1() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where text = \"GeorgeWashington\"");
        
        assertEquals("GeorgeWashington", entityQuery.getText());
        
	}
	
	@Test
	public void textSpace2() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where text = \"George Washington\"");
        
		assertEquals("George Washington", entityQuery.getText());
        
	}
	
	@Test
	public void textSpace3() throws Exception {
		
		EntityQuery entityQuery = Eql.generate("select * from entities where text=\"George Washington\"");
        
		assertEquals("George Washington", entityQuery.getText());
        
	}
		
}