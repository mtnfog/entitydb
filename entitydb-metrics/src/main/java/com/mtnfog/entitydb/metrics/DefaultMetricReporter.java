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
package com.mtnfog.entitydb.metrics;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.metrics.Metric;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;

/**
 * Implementation of {@link MetricReporter} that outputs the metrics
 * to the enabled loggers.
 * 
 * @author Mountain Fog, Inc.
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