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
package com.mtnfog.entitydb.schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;

@Configuration
@EnableScheduling
public class Scheduler {

	private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
	
	@Autowired
	private QueueConsumer queueConsumer;

	@Autowired
	private Indexer indexer;
	
	@Autowired
	private EntityStore<?> entityStore;
	
	@Autowired
	private SearchIndex searchIndex;
		
	@Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
		
        return Executors.newScheduledThreadPool(5);
        
    }
	
	@Scheduled(fixedRate = 5000)
	public void consume() throws EntityStoreException {
		
		queueConsumer.consume();
		
	}
	
	@Scheduled(fixedRate = 30000)
	public void status() throws EntityStoreException {
		
		LOGGER.info("Stored entities: {}, Indexed entities: {}", entityStore.getEntityCount(), searchIndex.getCount());
		
	}
	
	@Scheduled(fixedRate = 5000)
	public void index() throws EntityStoreException {
		
		LOGGER.info("Stored entities: {}, Indexed entities: {}", entityStore.getEntityCount(), searchIndex.getCount());
		
		if(properties.isIndexerEnabled()) {
			
			indexer.index(properties.getIndexerBatchSize());
			
		}
		
	}
	
}