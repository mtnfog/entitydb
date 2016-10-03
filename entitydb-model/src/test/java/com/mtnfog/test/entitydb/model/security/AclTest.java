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

import org.junit.Test;

import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.security.Acl;

public class AclTest {

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

}