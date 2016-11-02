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
	
}