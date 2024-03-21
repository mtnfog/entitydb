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
package ai.philterd.entitydb.data;

import java.util.ArrayList;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import ai.philterd.entitydb.configuration.EntityDbProperties;
import ai.philterd.entitydb.datastore.repository.UserRepository;
import ai.philterd.entitydb.model.datastore.entities.GroupEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;

/**
 * This class (when enabled by the properties) inserts test data
 * into the datastore. This data is useful when testing, debugging,
 * and (maybe) evaluating EntityDB.
 * 
 * @author Philterd, LLC
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