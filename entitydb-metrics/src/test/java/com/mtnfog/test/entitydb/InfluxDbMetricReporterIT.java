package com.mtnfog.test.entitydb;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mtnfog.entitydb.metrics.InfluxDbMetricReporter;
import com.mtnfog.entitydb.model.metrics.MetricReporter;

public class InfluxDbMetricReporterIT {
	
	@Test
	public void test() {
		
		MetricReporter reporter = new InfluxDbMetricReporter("http://localhost:8086", "entitydb", "root", "root");
		
		Map<String, Long> metrics = new HashMap<String, Long>();
		metrics.put("usage", 50L);
		
		reporter.report("cpu", metrics);
		
	}
	
}