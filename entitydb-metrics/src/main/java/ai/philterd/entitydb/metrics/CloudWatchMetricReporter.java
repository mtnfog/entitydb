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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import ai.philterd.entitydb.model.metrics.Metric;
import ai.philterd.entitydb.model.metrics.MetricReporter;
import ai.philterd.entitydb.model.metrics.Unit;

/**
 * Implementation of {@link MetricReporter} that reports
 * metrics to AWS CloudWatch.
 * 
 * @author Philterd, LLC
 *
 */
public class CloudWatchMetricReporter extends AbstractMetricReporter implements MetricReporter {

	private static final Logger LOGGER = LogManager.getLogger(CloudWatchMetricReporter.class);
	
	private AmazonCloudWatchClient cloudWatchClient;
	
	private String systemId;
	private String namespace;	
	
	/**
	 * Creates a CloudWatch metric reporter.
	 * @param systemId The system ID.
	 * @param namespace The CloudWatch namespace. 
	 * @param accessKey The AWS access key.
	 * @param secretKey The AWS secret key.
	 * @param endpoint The CloudWatch endpoint.
	 */
	public CloudWatchMetricReporter(String systemId, String namespace, String accessKey, String secretKey, String endpoint) {
		
		cloudWatchClient = new AmazonCloudWatchAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
		cloudWatchClient.setEndpoint(endpoint);
		
		this.systemId = systemId;
		this.namespace = namespace;
		  
	}
	
	/**
	 * Creates a CloudWatch metric reporter. Gets AWS credentials from 
	 * the credentials chain.
	 * @param systemId The system ID.
	 * @param namespace The CloudWatch namespace. 
	 * @param endpoint The CloudWatch endpoint.
	 */
	public CloudWatchMetricReporter(String systemId, String namespace, String endpoint) {
		
		cloudWatchClient = new AmazonCloudWatchAsyncClient();
		cloudWatchClient.setEndpoint(endpoint);
		
		this.systemId = systemId;
		this.namespace = namespace;
		  
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * This function is executed asynchronously.
	 */
	@Async
	@Override
	public void report(String measurement, List<Metric> metrics) {
	    
		PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();		   
		putMetricDataRequest.setNamespace(String.format("%s/%s", namespace, measurement));
		
		Collection<MetricDatum> metricData = new LinkedList<MetricDatum>();
		
		for(Metric metric : metrics) {
			
			MetricDatum metricDatum = new MetricDatum();
			
		    metricDatum.setMetricName(metric.getName());
		    metricDatum.setValue((double) metric.getValue());
		    metricDatum.setTimestamp(new Date());
		    metricDatum.setUnit(getStandardUnit(metric.getUnit()));
		    
		    metricData.add(metricDatum);
		    
		}
		
		putMetricDataRequest.setMetricData(metricData);
  
	    cloudWatchClient.putMetricData(putMetricDataRequest);
	    
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This function is executed asynchronously.
	 */
	@Async
	@Override
	public void report(String measurement, String field, long value, Unit unit) {
		
		PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();		   
		putMetricDataRequest.setNamespace(String.format("%s/%s", namespace, measurement));
		
		Collection<MetricDatum> metricData = new LinkedList<MetricDatum>();

		MetricDatum metricDatum = new MetricDatum();
		
	    metricDatum.setMetricName(field);
	    metricDatum.setValue((double) value);
	    metricDatum.setTimestamp(new Date());
	    metricDatum.setUnit(getStandardUnit(unit));
	    metricDatum.setDimensions(getDimension());
	    
	    metricData.add(metricDatum);
	    
	    putMetricDataRequest.setMetricData(metricData);
  
	    cloudWatchClient.putMetricData(putMetricDataRequest);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportElapsedTime(String measurement, String field, long startTime) {
		
		report(measurement, field, System.currentTimeMillis() - startTime, Unit.MILLISECONDS);
		
	}

	private Collection<Dimension> getDimension() {
		
		Collection<Dimension> dimensions = new LinkedList<Dimension>();
		
		Dimension dimension = new Dimension();
		dimension.setName("SystemId");
		dimension.setValue(systemId);
		
		dimensions.add(dimension);
		
		return dimensions;
		
	}
	
}