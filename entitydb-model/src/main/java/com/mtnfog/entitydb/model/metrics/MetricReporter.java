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
package com.mtnfog.entitydb.model.metrics;

import java.util.List;

/**
 * Reports metrics.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface MetricReporter {

	/**
	 * A measurement for metrics related to ingest.
	 */
	public static final String MEASUREMENT_INGEST = "Ingest";
	
	/**
	 * A measurement for metrics related to the API.
	 */
	public static final String MEASUREMENT_API = "API";
	
	/**
	 * A measurement for metrics related to queries.
	 */
	public static final String MEASUREMENT_QUERY = "Query";
	
	/**
	 * A measurement for metrics related to continuous queries.
	 */
	public static final String MEASUREMENT_CONTINUOUS_QUERY = "ContinuousQuery";
		
	/**
	 * Report the metrics.
	 * @measurement The measurement.
	 * @param metrics A list of {@link Metric metrics}.
	 */
	public void report(String measurement, List<Metric> metrics);
	
	/**
	 * Report a metric.
	 * @param measurement The measurement.
	 * @param field The field.
	 * @param value The value.
	 * @param The metric's {@link Unit unit}.
	 */
	public void report(String measurement, String field, long value, Unit unit);
	
	/**
	 * Report the elapsed time for an event.
	 * @param measurement The measurement.
	 * @param field The field.
	 * @param startTime The start time in milliseconds of the event.
	 */
	public void reportElapsedTime(String measurement, String field, long startTime);
	
}