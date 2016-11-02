package com.mtnfog.entitydb.model.metrics;

import java.util.Map;

/**
 * Reports metrics.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface MetricReporter {

	/**
	 * Report the metrics.
	 * @measurement The measurement.
	 * @param metrics The metrics.
	 */
	public void report(String measurement, Map<String, Long> metrics);
	
	/**
	 * Report a metric.
	 * @param measurement The measurement.
	 * @param field The field.
	 * @param value The value.
	 */
	public void report(String measurement, String field, long value);
	
	/**
	 * Report the elapsed time for an event.
	 * @param measurement The measurement.
	 * @param field The field.
	 * @param startTime The start time in milliseconds of the event.
	 */
	public void reportElapsedTime(String measurement, String field, long startTime);
	
}