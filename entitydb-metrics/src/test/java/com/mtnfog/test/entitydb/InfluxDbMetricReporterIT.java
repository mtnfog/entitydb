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
package com.mtnfog.test.entitydb;

import org.junit.Ignore;
import org.junit.Test;

import com.mtnfog.entitydb.metrics.InfluxDbMetricReporter;
import com.mtnfog.entitydb.model.metrics.MetricReporter;
import com.mtnfog.entitydb.model.metrics.Unit;

@Ignore
public class InfluxDbMetricReporterIT {
	
	@Test
	public void test() {
		
		MetricReporter reporter = new InfluxDbMetricReporter("http://localhost:8086", "entitydb", "root", "root");
		
		reporter.report("cpu", "usage", 50L, Unit.COUNT);
		
	}
	
}