package com.mtnfog.entitydb.metrics;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.metrics.MetricReporter;

/**
 * Implementation of {@link MetricReporter} that outputs the metrics
 * to the enabled loggers.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class DefaultMetricReporter implements MetricReporter {

	private static final Logger LOGGER = LogManager.getLogger(DefaultMetricReporter.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, Map<String, Long> metrics) {

		String m = "";
		
		for(String k : metrics.keySet()) {			
			m += k + "=" + metrics.get(k) + "; ";			
		}
		
		LOGGER.info("Reporting measurement {} with metrics: {}", measurement, m);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, String field, long value) {
		
		LOGGER.info("Reporting measurement {} with metric: {} = {}.", measurement, field, value);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime);
		
	}
	
}