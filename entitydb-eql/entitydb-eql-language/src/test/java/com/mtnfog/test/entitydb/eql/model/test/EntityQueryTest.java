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
package com.mtnfog.test.entitydb.eql.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mtnfog.entitydb.eql.model.EntityQuery;

public class EntityQueryTest {

	/*@Test
	public void build() {
		
		EntityQuery entityQuery = EntityQuery.builder()
			.entityText("George Washington")
			.confidence(50)
			.context("context")
			.build();
		
		assertEquals("George Washington", entityQuery.getEntityText());
		assertEquals(50, entityQuery.getConfidenceRange().getMaximum(), 0);
		assertEquals("context", entityQuery.getContext());
		
	}*/
	
	@Test
	public void type() {
		
		EntityQuery entityQuery = new EntityQuery();
		
		assertNull(entityQuery.getType());
		assertEquals(0, entityQuery.getOffset());
		assertEquals(25, entityQuery.getLimit());
		
	}
	
}