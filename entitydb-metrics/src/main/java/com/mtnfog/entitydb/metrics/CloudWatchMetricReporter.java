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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.mtnfog.entitydb.model.metrics.Metric;
import com.mtnfog.entitydb.model.metrics.MetricReporter;

/**
 * Implementation of {@link MetricReporter} that reports
 * metrics to AWS CloudWatch.
 * 
 * @author Mountain Fog, Inc.
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
		
		cloudWatchClient = new AmazonCloudWatchClient(new BasicAWSCredentials(accessKey, secretKey));
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
		
		cloudWatchClient = new AmazonCloudWatchClient();
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
		putMetricDataRequest.setNamespace(namespace);
		
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
	public void report(String measurement, String field, long value) {
		
		PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();		   
		putMetricDataRequest.setNamespace(namespace);
		
		Collection<MetricDatum> metricData = new LinkedList<MetricDatum>();

		MetricDatum metricDatum = new MetricDatum();
	    metricDatum.setMetricName(field);
	    metricDatum.setValue((double) value);
	    metricDatum.setTimestamp(new Date());
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
		
		report(measurement, field, System.currentTimeMillis() - startTime);
		
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