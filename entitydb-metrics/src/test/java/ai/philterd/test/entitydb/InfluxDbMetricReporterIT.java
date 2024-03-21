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
package ai.philterd.test.entitydb;

import org.junit.Ignore;
import org.junit.Test;

import ai.philterd.entitydb.metrics.InfluxDbMetricReporter;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;

@Ignore
public class InfluxDbMetricReporterIT {
	
	@Test
	public void test() {
		
		MetricReporter reporter = new InfluxDbMetricReporter("http://localhost:8086", "entitydb", "root", "root");
		
		reporter.report("cpu", "usage", 50L, Unit.COUNT);
		
	}
	
}