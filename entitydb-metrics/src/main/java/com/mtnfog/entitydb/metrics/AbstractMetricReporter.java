package com.mtnfog.entitydb.metrics;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.mtnfog.entitydb.model.metrics.Unit;

public abstract class AbstractMetricReporter {

	public StandardUnit getStandardUnit(Unit unit) {
		
		if(unit == Unit.MILLISECONDS) {
			
			return StandardUnit.Milliseconds;
			
		} else if(unit == Unit.ENTITIES) {
			
			return StandardUnit.Count;
			
		} else {
			
			return StandardUnit.None;
			
		}
		
	}
	
}