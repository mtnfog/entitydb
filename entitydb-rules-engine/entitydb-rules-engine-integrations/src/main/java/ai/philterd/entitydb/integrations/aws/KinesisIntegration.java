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
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.google.gson.Gson;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS Kinesis that publishes the entities to an AWS Kinesis stream.
 * 
 * @author Philterd, LLC
 *
 */
public class KinesisIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(KinesisIntegration.class);

	private AmazonKinesisClient kinesisClient;
	private String streamName;
	private Gson gson;
	
	public KinesisIntegration(String streamName, Region region) {
		
		this.kinesisClient = new AmazonKinesisClient();
		this.gson = new Gson();		
		this.streamName = streamName;

		kinesisClient.setRegion(region);
		
	}
	
	public KinesisIntegration(String streamName, String endpoint) {
		
		this.kinesisClient = new AmazonKinesisClient();
		this.gson = new Gson();		
		this.streamName = streamName;

		kinesisClient.setEndpoint(endpoint);
		
	}
	
	public KinesisIntegration(String streamName, Region region, String accessKey, String secretKey) {
		
		this.gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			this.kinesisClient = new AmazonKinesisClient();
			
		} else {

			this.kinesisClient = new AmazonKinesisClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}						
		
		this.streamName = streamName;
	
		kinesisClient.setRegion(region);
		
	}
	
	public KinesisIntegration(String streamName, String endpoint, String accessKey, String secretKey) {
		
		this.gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
			
			this.kinesisClient = new AmazonKinesisClient();
			
		} else {

			this.kinesisClient = new AmazonKinesisClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}						
		
		this.streamName = streamName;
	
		kinesisClient.setEndpoint(endpoint);
		
	}
	
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
		
		try {
						
			for(Entity entity : entities) {
			
				// The json of the object.			
				String json = gson.toJson(entity);
				
				PutRecordRequest request = new PutRecordRequest();
				request.setStreamName(streamName);
				request.setData(ByteBuffer.wrap(json.getBytes()));
				request.setPartitionKey(entity.getContext());
				
				PutRecordResult result = kinesisClient.putRecord(request);
							
				LOGGER.debug("Put record on Kinesis stream {} with sequence number {}.", streamName, result.getSequenceNumber());
				
			}
			
		} catch (Exception ex) {
			
			throw new IntegrationException("Unable to process Kinesis integration. Not all entities may have been queued.", ex);
			
		}

	}
	
	@Override
	public void process(Entity entity) throws IntegrationException {
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		process(entities);
		
	}

}