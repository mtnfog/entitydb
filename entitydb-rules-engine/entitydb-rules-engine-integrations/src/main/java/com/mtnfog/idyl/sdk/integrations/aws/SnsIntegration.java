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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.idyl.sdk.integrations.aws;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.google.gson.Gson;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

/**
 * Integration for AWS SNS.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class SnsIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(SnsIntegration.class);

	private AmazonSNSClient snsClient;
	private String topicArn;
	private String subject;	
	private Gson gson;
	
	/**
	 * Creates a new SNS integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param topicArn The SNS topic's ARN.
	 * @param subject The subject of each published message.
	 * @param region The AWS service region.
	 */
	public SnsIntegration(String topicArn, String subject, Region region) {
		
		gson = new Gson();
		
		this.snsClient = new AmazonSNSClient();
		this.topicArn = topicArn;
		this.subject = subject;
		
		snsClient.setRegion(region);
		
	}

	/**
	 * Creates a new SNS integration. AWS credentials are retrieved
	 * from the EC2 metadata service.
	 * @param topicArn The SNS topic's ARN.
	 * @param subject The subject of each published message.
	 * @param endpoint The AWS service endpoint.
	 */
	public SnsIntegration(String topicArn, String subject, String endpoint) {
		
		gson = new Gson();
		
		this.snsClient = new AmazonSNSClient();
		this.topicArn = topicArn;
		this.subject = subject;
		
		snsClient.setEndpoint(endpoint);
		
	}
	
	/**
	 * Creates a new SNS integration.
	 * @param topicArn The SNS topic's ARN.
	 * @param subject The subject of each published message.
	 * @param region The AWS service region.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SnsIntegration(String topicArn, String subject, Region region, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
		
			this.snsClient = new AmazonSNSClient();
			
		} else {
			
			this.snsClient = new AmazonSNSClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.topicArn = topicArn;
		this.subject = subject;
		
		snsClient.setRegion(region);
		
	}
	
	/**
	 * Creates a new SNS integration.
	 * @param topicArn The SNS topic's ARN.
	 * @param subject The subject of each published message.
	 * @param endpoint The AWS service region.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SnsIntegration(String topicArn, String subject, String endpoint, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		if(StringUtils.isEmpty(accessKey)) {
		
			this.snsClient = new AmazonSNSClient();
			
		} else {
			
			this.snsClient = new AmazonSNSClient(new BasicAWSCredentials(accessKey, secretKey));
			
		}		
		
		this.topicArn = topicArn;
		this.subject = subject;
		
		snsClient.setEndpoint(endpoint);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Publishes the entities to an AWS SNS topic. The entities published as
	 * JSON documents. The context and documentId under which the entities were 
	 * extracted are published as message attributes with the names context
	 * and documentId.	 
	 */
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
								
		for(Entity entity : entities) {
				
			try {
				
				String message = gson.toJson(entity);
						
				// Not going to check max message size. Messages that are too large will be rejected.
					
				PublishRequest publishRequest = new PublishRequest(topicArn, message);				
				publishRequest.setSubject(subject);
					
				// Send it. We don't care about the response.
				snsClient.publish(publishRequest);			
							
			} catch (Exception ex) {
				
				LOGGER.error("SNS integration failed on entity: {}", entity);
				
				LOGGER.error("Unable to process integration.", ex);
				
				throw new IntegrationException("Unable to process SNS integration. Not all entities may have been published.", ex);
				
			}
			
		}

	}
	
	@Override
	public void process(Entity entity) throws IntegrationException {
		
		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		process(entities);
		
	}
	
}