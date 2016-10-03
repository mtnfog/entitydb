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
package com.mtnfog.idyl.sdk.integrations.aws;

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
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS Kinesis Firehose that publishes the entities to a firehose stream.
 * 
 * @author Mountain Fog, Inc.
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