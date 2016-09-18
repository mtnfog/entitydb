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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.test.entitydb.security;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.security.AclUtils;

public class AclUtilsTest {
	
	private static final Logger LOGGER = LogManager.getLogger(AclUtilsTest.class);

	@Test
	public void generateAcl1() {
		
		String acl = AclUtils.generateAcl("user1,user2", "group1,group2", false);
		
		LOGGER.info(acl);
		
		assertEquals("user1,user2:group1,group2:0", acl);
		
	}
	
	@Test
	public void generateAcl2() {
		
		Set<String> users = new HashSet<String>(Arrays.asList("user1", "user2"));
		Set<String> groups = new HashSet<String>(Arrays.asList("group1", "group2"));
		
		String acl = AclUtils.generateAcl(users, groups, false);
		
		LOGGER.info(acl);
		
		assertEquals("user1,user2:group2,group1:0", acl);
		
	}
	
	@Test
	public void generateAcl3() {
		
		Acl acl = new Acl("user1,user2", "group1,group2", false);
				
		LOGGER.info(acl);
		
		assertEquals("user1,user2:group2,group1:0", acl);
		
	}
	
}