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
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.gson.Gson;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.integrations.Integration;
import ai.philterd.entitydb.model.integrations.IntegrationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS SES that sends email.
 * 
 * @author Philterd, LLC
 *
 */
public class SesIntegration implements Integration {
	
	private static final Logger LOGGER = LogManager.getLogger(SesIntegration.class);

	private AmazonSimpleEmailServiceClient sesClient;
	private Gson gson;
	
	private String to;
	private String from;
	private String subject;
	
	/**
	 * Creates a new SES integration. AWS credentials are retrieved from the EC2 metadata service.
	 * @param region The AWS service {@link Region region}.
	 */
	public SesIntegration(String to, String from, String subject, Region region) {
		
		sesClient = new AmazonSimpleEmailServiceClient();
		gson = new Gson();
		
		this.to = to;
		this.from = from;
		this.subject = subject;
		
		sesClient.setRegion(region);
		
	}
	
	/**
	 * Creates a new SES integration. AWS credentials are retrieved from the EC2 metadata service.
	 * @param region The AWS service {@link Region region}.
	 */
	public SesIntegration(String to, String from, String subject, String endpoint) {
		
		sesClient = new AmazonSimpleEmailServiceClient();
		gson = new Gson();
		
		this.to = to;
		this.from = from;
		this.subject = subject;
		
		sesClient.setEndpoint(endpoint);
		
	}
	
	/**
	 * Creates a new SES integration.
	 * @param region The AWS service {@link Region region}.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SesIntegration(String to, String from, String subject, Region region, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		this.to = to;
		this.from = from;
		this.subject = subject;
		
		sesClient = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(accessKey, secretKey));	
		
		sesClient.setRegion(region);

	}
	
	/**
	 * Creates a new SES integration.
	 * @param region The AWS service {@link Region region}.
	 * @param accessKey The AWS SQS access key.
	 * @param secretKey The AWS SQS secret key.
	 */
	public SesIntegration(String to, String from, String subject,String endpoint, String accessKey, String secretKey) {
		
		gson = new Gson();
		
		this.to = to;
		this.from = from;
		this.subject = subject;
		
		sesClient = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(accessKey, secretKey));	
		
		sesClient.setEndpoint(endpoint);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {

		for(Entity entity : entities) {
				
			try {
				
				String messageBody = gson.toJson(entity);
			
				// Construct an object to contain the recipient address.
		        Destination destination = new Destination().withToAddresses(new String[]{to});
		        
		        // Create the subject and body of the message.
		        Content subjectContent = new Content().withData(subject);
		        Content bodyContent = new Content().withData(messageBody); 
		        Body body = new Body().withText(bodyContent);
		        
		        // Create a message with the specified subject and body.
		        Message message = new Message().withSubject(subjectContent).withBody(body);
		        
		        // Assemble the email.
		        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
		        
		        LOGGER.debug("Sending email via AWS SES to {}.", to);
		        
	            // Send the email.
	            sesClient.sendEmail(request);
	
			} catch (Exception ex) {
				
				LOGGER.error("SES integration failed on entity: {}", entity);
				
				LOGGER.error("Unable to process integration.", ex);
				
				throw new IntegrationException("Unable to process SES integration.", ex);
				
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