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
package com.mtnfog.test.entitydb.model.security;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mtnfog.entitydb.model.domain.User;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.security.Acl;

public class AclTest {

	private static final Logger LOGGER = LogManager.getLogger(AclTest.class);
	
	@Test(expected = MalformedAclException.class)
	public void acl1() throws MalformedAclException {
		
		new Acl("asdf");
		
	}
	
	@Test(expected = MalformedAclException.class)
	public void acl2() throws MalformedAclException {
		
		new Acl("user1:group1:2");
		
	}
	
	@Test(expected = MalformedAclException.class)
	public void acl3() throws MalformedAclException {
		
		new Acl("user1,:group1:2");
		
	}
	
	@Test(expected = MalformedAclException.class)
	public void acl4() throws MalformedAclException {
		
		new Acl("user1:,group1:2");
		
	}
	
	@Test
	public void acl5() throws MalformedAclException {
		
		new Acl("user::1");
		
	}
	
	@Test
	public void acl6() throws MalformedAclException {
		
		new Acl(":group:1");
		
	}
	
	@Test
	public void acl7() throws MalformedAclException {
		
		new Acl("::1");
		
	}
	
	@Test
	public void visible1() throws MalformedAclException {
		
		long id = 1;
		String username = "user";
		String email = "email@emasdf.com";
		String mobile = "555-555-5555";
		String apiKey = "asdf1234";
		Set<String> groups = new HashSet<String>(Arrays.asList("g1", "g2", "g3"));
		
		User user = new User(id, username, email, mobile, apiKey, groups);		
		
		Acl acl = new Acl("::1");
		boolean visible = acl.isEntityVisibleToUser(user);
		
		assertTrue(visible);
		
	}
	
	@Test
	public void visible2() throws MalformedAclException {
		
		long id = 1;
		String username = "user";
		String email = "email@emasdf.com";
		String mobile = "555-555-5555";
		String apiKey = "asdf1234";
		Set<String> groups = new HashSet<String>(Arrays.asList("g1", "g2", "g3"));
		
		User user = new User(id, username, email, mobile, apiKey, groups);		
		
		Acl acl = new Acl("::0");
		boolean visible = acl.isEntityVisibleToUser(user);
		
		assertFalse(visible);
		
	}
	
	@Test
	public void visible3() throws MalformedAclException {
		
		long id = 1;
		String username = "user";
		String email = "email@emasdf.com";
		String mobile = "555-555-5555";
		String apiKey = "asdf1234";
		Set<String> groups = new HashSet<String>(Arrays.asList("g1", "g2", "g3"));
		
		User user = new User(id, username, email, mobile, apiKey, groups);		
		
		Acl acl = new Acl(":g1:0");
		boolean visible = acl.isEntityVisibleToUser(user);
		
		assertTrue(visible);
		
	}
	
	@Test
	public void visible4() throws MalformedAclException {
		
		long id = 1;
		String username = "user";
		String email = "email@emasdf.com";
		String mobile = "555-555-5555";
		String apiKey = "asdf1234";
		Set<String> groups = new HashSet<String>(Arrays.asList("g1", "g2", "g3"));
		
		User user = new User(id, username, email, mobile, apiKey, groups);		
		
		Acl acl = new Acl(":g5,g6:0");
		boolean visible = acl.isEntityVisibleToUser(user);
		
		assertFalse(visible);
		
	}
	
	@Test
	public void visible5() throws MalformedAclException {
		
		long id = 1;
		String username = "user";
		String email = "email@emasdf.com";
		String mobile = "555-555-5555";
		String apiKey = "asdf1234";
		Set<String> groups = new HashSet<String>(Arrays.asList("g1", "g2", "g3"));
		
		User user = new User(id, username, email, mobile, apiKey, groups);		
		
		Acl acl = new Acl("1:g5,g6:0");
		boolean visible = acl.isEntityVisibleToUser(user);
		
		assertTrue(visible);
		
	}

}