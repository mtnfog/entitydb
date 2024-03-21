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
package ai.philterd.entitydb.model.metrics;

import java.util.List;

/**
 * Reports metrics.
 * 
 * @author Philterd, LLC
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