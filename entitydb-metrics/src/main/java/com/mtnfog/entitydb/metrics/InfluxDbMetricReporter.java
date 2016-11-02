package com.mtnfog.entitydb.metrics;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import com.mtnfog.entitydb.model.metrics.MetricReporter;

/**
 * Implementation of {@link MetricReporter} that reports
 * metrics to InfluxDB.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class InfluxDbMetricReporter implements MetricReporter {

	private static final Logger LOGGER = LogManager.getLogger(InfluxDbMetricReporter.class);
	
	private static final String RETENTION = "autogen";
	
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

		// Flush every 2000 Points, at least every 100ms
		//influxDB.enableBatch(10, 100, TimeUnit.MILLISECONDS);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, Map<String, Long> metrics) {
		
		Builder builder = Point.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		
		for(String field : metrics.keySet()) {		
			builder.addField(field, metrics.get(field));			
		}
		
		Point point = builder.build();
		
		influxDB.write(database, RETENTION, point);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, String field, long value) {
		
		Builder builder = Point.measurement(measurement)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
			.addField(field, value);			
		
		Point point = builder.build();
		
		influxDB.write(database, RETENTION, point);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime);
		
	}
	
}