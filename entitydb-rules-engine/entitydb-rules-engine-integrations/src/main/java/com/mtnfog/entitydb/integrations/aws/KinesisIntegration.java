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
package com.mtnfog.entitydb.integrations.aws;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS Kinesis that publishes the entities to an AWS Kinesis stream.
 * 
 * @author Mountain Fog, Inc.
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