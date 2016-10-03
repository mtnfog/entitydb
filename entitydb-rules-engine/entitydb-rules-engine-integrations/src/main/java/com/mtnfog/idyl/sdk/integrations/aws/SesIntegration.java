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
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integration for AWS SES that sends email.
 * 
 * @author Mountain Fog, Inc.
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