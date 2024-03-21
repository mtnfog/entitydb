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
package ai.philterd.test.entitydb.model.security;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.security.Acl;

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