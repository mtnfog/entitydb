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
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

/**
 * Integration for AWS SNS.
 * 
 * @author Philterd, LLC
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