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
package ai.philterd.test.entitydb.datastore.services;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ai.philterd.entitydb.datastore.repository.UserRepository;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class, 
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RepositoryTestConfig.class })
@DirtiesContext
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void before() {
		
		userRepository.deleteAll();
		
	}

	@Test
	public void getByApiKey() {
		
		UserEntity userEntity = new UserEntity();
		userEntity.setApiKey("apikey");
		userEntity.setUserName("test");
		userEntity.setEmail("test@asdf.com");
		
		userRepository.save(userEntity);
		
		UserEntity u = userRepository.getByApiKey("apikey");
		assertEquals(userEntity.getUserName(), u.getUserName());
		
		u = userRepository.getByApiKey("none");
		assertNull(u);
		
	}

}