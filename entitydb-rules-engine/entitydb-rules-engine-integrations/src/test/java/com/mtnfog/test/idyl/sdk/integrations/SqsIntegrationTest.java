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
package com.mtnfog.test.idyl.sdk.integrations;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.mtnfog.entity.Entity;
import com.mtnfog.idyl.sdk.integrations.aws.SqsIntegration;

public class SqsIntegrationTest {		

	private static final Logger LOGGER = LogManager.getLogger(SqsIntegrationTest.class);

	private static final String QUEUE_NAME = "testqueue";
	private static final int ELASTIMQ_PORT = 9324;
	private static final String ENDPOINT = String.format("http://localhost:%s", ELASTIMQ_PORT);
	
	private static AmazonSQSClient sqsClient;
    private static SQSRestServer sqsServer;
    private static String queueUrl;
    
    @BeforeClass
    public static void beforeClass() {
    	
        sqsServer = SQSRestServerBuilder
        		.withPort(ELASTIMQ_PORT)
        		.withInterface("localhost")
        		.start();
        
        sqsClient = new AmazonSQSClient(new BasicAWSCredentials("x", "y"));
        sqsClient.setEndpoint(ENDPOINT);
        
        CreateQueueResult result = sqsClient.createQueue(QUEUE_NAME);
        queueUrl = result.getQueueUrl();

    }
    
    @AfterClass
    public static void afterClass() {
    	
    	sqsServer.stopAndWait();
    	
    }
    
    @Before
    public void before() {
    	
    	PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest();
    	purgeQueueRequest.setQueueUrl(queueUrl);
    	
    	sqsClient.purgeQueue(purgeQueueRequest);
    	
    }
    
    @Test
    public void publishOneEntity() {
    	
    	LOGGER.info("Using SQS queue URL: {}", queueUrl);
    	
    	final int delaySeconds = 0;
    			
    	SqsIntegration sqsIntegration = new SqsIntegration(queueUrl, delaySeconds, ENDPOINT, "accesskey", "secretkey");
    	
    	Entity entity = new Entity("George Washington");
    	entity.setContext("context");
    	entity.setDocumentId("docid");
    	
    	sqsIntegration.process(entity);
    	
    	List<String> attributeNames = Arrays.asList("ApproximateNumberOfMessages");
    	
    	GetQueueAttributesResult result = sqsClient.getQueueAttributes(queueUrl, attributeNames);
    	Map<String, String> queueAtributes = result.getAttributes();
    	
    	int messages = Integer.parseInt(queueAtributes.get("ApproximateNumberOfMessages"));
    	
    	assertEquals(1, messages);
    	    	
    }
    
    @Test
    public void publishMultipleEntities() {
    	
    	LOGGER.info("Using SQS queue URL: {}", queueUrl);
    	
    	final int delaySeconds = 0;
    			
    	SqsIntegration sqsIntegration = new SqsIntegration(queueUrl, delaySeconds, ENDPOINT, "accesskey", "secretkey");
    	
    	Entity e1 = new Entity("George Washington");
    	e1.setContext("context");
    	e1.setDocumentId("docid");
    	
    	Entity e2 = new Entity("George Washington");
    	e2.setContext("context");
    	e2.setDocumentId("docid");
    	
    	Entity e3 = new Entity("George Washington");
    	e3.setContext("context");
    	e3.setDocumentId("docid");    	
    	
    	Collection<Entity> entities = new ArrayList<Entity>();
    	entities.add(e1);
    	entities.add(e2);
    	entities.add(e3);
    	
    	sqsIntegration.process(entities);
    	
    	List<String> attributeNames = Arrays.asList("ApproximateNumberOfMessages");
    	
    	GetQueueAttributesResult result = sqsClient.getQueueAttributes(queueUrl, attributeNames);
    	Map<String, String> queueAtributes = result.getAttributes();
    	
    	int messages = Integer.parseInt(queueAtributes.get("ApproximateNumberOfMessages"));
    	
    	assertEquals(3, messages);
    	
    	
    }
    	
}