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
package com.mtnfog.entitydb.configurations;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
import com.mtnfog.entitydb.model.metrics.Metric;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;
import com.mtnfog.entitydb.model.queue.QueueConsumer;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;

@Configuration
@EnableScheduling
public class TaskSchedulerConfiguration {

	private static final Logger LOGGER = LogManager.getLogger(TaskSchedulerConfiguration.class);
	
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
	
	@Autowired
	private ThreadPoolExecutor executor;
	
	@Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(5);
    }
	
	@Scheduled(fixedDelay = 5000)
	public void consume() {
				
		LOGGER.info("Executing queue consumer.");
		
		queueConsumer.consume();
				
	}
	
	@Scheduled(fixedDelay = 60000)
	public void status() throws EntityStoreException {
		
		long stored = entityStore.getEntityCount();
		long indexed = searchIndex.getCount();
		
		LOGGER.info("Stored entities: {}, Indexed entities: {}", stored, indexed);
		
		List<Metric> metrics = new LinkedList<Metric>();
		metrics.add(new Metric("stored", stored, Unit.COUNT));
		metrics.add(new Metric("indexed", indexed, Unit.COUNT));
		metrics.add(new Metric("activeThreads", executor.getActiveCount(), Unit.COUNT));
		metrics.add(new Metric("queuedThreads", executor.getQueue().size(), Unit.COUNT));
		
		metricReporter.report(MetricReporter.MEASUREMENT_INGEST, metrics);
		
	}
	
	@Scheduled(fixedDelay = 500)
	public void index() {
		
		LOGGER.info("Executing indexer.");
		
		if(properties.isIndexerEnabled()) {			
			
			int sleepPeriod = 2000;
			
			while(indexer.index() == 0) {
			
				try {
					
					sleepPeriod = sleepPeriod * 2;
					
					// Sleep for a max of 3 minutes.
					if(sleepPeriod > 180000) {
						sleepPeriod = 180000;
					}
					
					Thread.sleep(sleepPeriod);
					
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				
			}
	
		}
		
	}
		
}