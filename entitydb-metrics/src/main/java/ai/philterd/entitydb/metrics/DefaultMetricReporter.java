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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.metrics.Metric;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;

/**
 * Implementation of {@link MetricReporter} that outputs the metrics
 * to the enabled loggers.
 * 
 * @author Philterd, LLC
 *
 */
public class DefaultMetricReporter extends AbstractMetricReporter implements MetricReporter {

	private static final Logger LOGGER = LogManager.getLogger(DefaultMetricReporter.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, List<Metric> metrics) {

		String m = "";
		
		for(Metric metric : metrics) {			
			m += metric.getName() + "=" + metric.getValue() + " " + metric.getUnit().getUnit() + "; ";			
		}
		
		LOGGER.info("Reporting measurement {} with metrics: {}", measurement, m);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(String measurement, String field, long value, Unit unit) {
		
		LOGGER.info("Reporting measurement {} with metric: {} = {} {}.", measurement, field, value, unit.getUnit());
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime, Unit.MILLISECONDS);
		
	}
	
}