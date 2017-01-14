/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
package com.mtnfog.entitydb.metrics;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.springframework.scheduling.annotation.Async;

import com.mtnfog.entitydb.model.metrics.Metric;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;

/**
 * Implementation of {@link MetricReporter} that reports
 * metrics to InfluxDB.
 * 
 * @author Mountain Fog, Inc.
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime, Unit.MILLISECONDS);
		
	}
	
}