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
package com.mtnfog.entitydb.datastore.data;

import java.util.ArrayList;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.datastore.repository.UserRepository;
import com.mtnfog.entitydb.model.datastore.entities.GroupEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;

/**
 * This class (when enabled by the properties) inserts test data
 * into the datastore. This data is useful when testing, debugging,
 * and (maybe) evaluating EntityDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Component
public class TestDataLoader implements ApplicationRunner {

	private static final Logger LOGGER = LogManager.getLogger(TestDataLoader.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
	
	@Autowired
    private UserRepository userRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		if(properties.isPopulateTestData()) {
			
			LOGGER.info("Writing test data to the datastore.");
			
			UserEntity userEntity = new UserEntity();
			userEntity.setUserName("jsmith");
			userEntity.setEmail("jsmith@test-email-fake.com");
			userEntity.setMobile("555-555-5555");
			userEntity.setApiKey("asdf1234");
			userEntity.setGroups(new ArrayList<GroupEntity>());
			
			userRepository.save(userEntity);
		
		}
		
	}
	
}