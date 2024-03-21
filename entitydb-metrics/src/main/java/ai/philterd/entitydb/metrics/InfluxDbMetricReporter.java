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
package ai.philterd.entitydb.metrics;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.springframework.scheduling.annotation.Async;

import ai.philterd.entitydb.model.metrics.Metric;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;

/**
 * Implementation of {@link MetricReporter} that reports
 * metrics to InfluxDB.
 * 
 * @author Philterd, LLC
 *
 */
public class InfluxDbMetricReporter extends AbstractMetricReporter implements MetricReporter {

	private static final Logger LOGGER = LogManager.getLogger(InfluxDbMetricReporter.class);
	
	private static final String DEFAULT_RETENTION_POLICY = "autogen";
	
	private InfluxDB influxDB;
	private String database;
	
	/**
	 * Creates a new InfluxDB metric reporter.
	 * @param endpoint The InfluxDB endpoint, such as http://172.17.0.2:8086.
	 * @param database The name of the InfluxDB database.
	 * @param username The InfluxDB username.
	 * @param password The InfluxDB password.
	 * 
	 */
	public InfluxDbMetricReporter(String endpoint, String database, String username, String password) {

		this.database = database;
		
		influxDB = InfluxDBFactory.connect(endpoint, username, password);
		influxDB.createDatabase(database);		

		// Flush every 10 data points, at least every 60 seconds.
		influxDB.enableBatch(10, 60, TimeUnit.SECONDS);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * The metric unit is ignored and milliseconds are used.
	 * This function is executed asynchronously.
	 */
	@Async
	@Override
	public void report(String measurement, List<Metric> metrics) {
		
		Builder builder = 
			Point
				.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		
		for(Metric metric : metrics) {		
			builder.addField(metric.getName(), metric.getValue());			
		}
		
		Point point = builder.build();
		
		influxDB.write(database, DEFAULT_RETENTION_POLICY, point);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * The metric unit is ignored and milliseconds are used.
	 * This function is executed asynchronously.
	 */
	@Async
	@Override
	public void report(String measurement, String field, long value, Unit unit) {
		
		Builder builder = 
			Point
				.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField(field, value);			
		
		Point point = builder.build();
		
		influxDB.write(database, DEFAULT_RETENTION_POLICY, point);
		
	}
	

	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime, Unit.MILLISECONDS);
		
	}
	
}