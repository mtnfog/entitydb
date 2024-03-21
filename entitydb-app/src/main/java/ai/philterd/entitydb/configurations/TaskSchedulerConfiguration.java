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
package ai.philterd.entitydb.configurations;

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

import ai.philterd.entitydb.configuration.EntityDbProperties;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.metrics.Metric;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;
import ai.philterd.entitydb.model.queue.QueueConsumer;
import ai.philterd.entitydb.model.search.Indexer;
import ai.philterd.entitydb.model.search.SearchIndex;

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