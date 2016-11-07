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
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mtnfog.entitydb.configuration.EntityDbProperties;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;

@Configuration
@EnableAsync
@EnableScheduling
public class Scheduler extends AsyncConfigurerSupport {

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
		
	@Autowired
	private MetricReporter metricReporter;
	
	@Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
		
        return Executors.newScheduledThreadPool(5);
        
    }
	
	@Override
    public Executor getAsyncExecutor() {
		
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("EntityDB-");
        executor.initialize();
        
        return executor;
        
    }
	
	@Scheduled(fixedDelay = 5000)
	public void consume() throws EntityStoreException {
		
		queueConsumer.consume();
		
	}
	
	@Scheduled(fixedDelay = 60000)
	public void status() throws EntityStoreException {
		
		long stored = entityStore.getEntityCount();
		long indexed = searchIndex.getCount();
		
		LOGGER.info("Stored entities: {}, Indexed entities: {}", stored, indexed);
		
		metricReporter.report("Entities", "stored", stored);
		metricReporter.report("Entities", "indexed", indexed);
		
	}
	
	@Scheduled(fixedDelay = 5000)
	public void index() throws EntityStoreException {
		
		if(properties.isIndexerEnabled()) {
			
			indexer.index(properties.getIndexerBatchSize());
			
		}
		
	}
	
}