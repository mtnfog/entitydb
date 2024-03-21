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
package ai.philterd.entitydb.integrations.aws;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordResult;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS Kinesis Firehose that publishes the entities to a firehose stream.
 * 
 * @author Philterd, LLC
 *
 */
public class KinesisFirehoseIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(KinesisFirehoseIntegration.class);

	private AmazonKinesisFirehoseClient kinesisFirehoseClient;
	private String streamName;
	private Gson gson;
	
	public KinesisFirehoseIntegration(String streamName, Region region) {
		
		this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient();
		this.gson = new Gson();		
		this.streamName = streamName;
		
		kinesisFirehoseClient.setRegion(region);
		
	}
	
	public KinesisFirehoseIntegration(String streamName, String endpoint) {
		
		this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient();
		this.gson = new Gson();		
		this.streamName = streamName;
		
		kinesisFirehoseClient.setEndpoint(endpoint);
		
	}
	
	public KinesisFirehoseIntegration(String streamName, Region region, String accessKey, String secretKey) {
		
		this.gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient();
			
		} else {

			this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.streamName = streamName;
	
		kinesisFirehoseClient.setRegion(region);
		
	}
	
	public KinesisFirehoseIntegration(String streamName, String endpoint, String accessKey, String secretKey) {
		
		this.gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient();
			
		} else {

			this.kinesisFirehoseClient = new AmazonKinesisFirehoseClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.streamName = streamName;
	
		kinesisFirehoseClient.setEndpoint(endpoint);
		
	}
	
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
		
		try {
			
			for(Entity entity : entities) {
			
				// The json of the object.			
				String json = gson.toJson(entity);
				
				Record record = new Record();
				record.setData(ByteBuffer.wrap(json.getBytes()));
				
				PutRecordRequest request = new PutRecordRequest();
				request.setDeliveryStreamName(streamName);
				request.setRecord(record);
	
				PutRecordResult result = kinesisFirehoseClient.putRecord(request);
							
				LOGGER.debug("Put record on Kinesis firehose stream {} with record ID {}.", streamName, result.getRecordId());
			
			}
			
		} catch (Exception ex) {
			
			throw new IntegrationException("Unable to process Kinesis Firehose integration. Not all entities may have been queued.", ex);
			
		}

	}
	
	@Override
	public void process(Entity entity) throws IntegrationException {
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		process(entities);
		
	}

}