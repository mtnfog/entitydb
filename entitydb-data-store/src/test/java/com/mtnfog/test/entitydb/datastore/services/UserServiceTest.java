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
package com.mtnfog.test.entitydb.datastore.services;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.datastore.entities.User;

@Ignore
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void test() {
			
		User user = userRepository.getByApiKey("apikey");
		assertNull(user);
    
	}
	
	@Configuration
	@ComponentScan(basePackages = "com.mtnfog.entitydb")
	static class ContextConfiguration { }
	
}