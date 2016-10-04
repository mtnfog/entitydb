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
			userEntity.setApiKey("asdf1234");
			userEntity.setGroups(new ArrayList<GroupEntity>());
			
			userRepository.save(userEntity);
		
		}
		
	}
	
}