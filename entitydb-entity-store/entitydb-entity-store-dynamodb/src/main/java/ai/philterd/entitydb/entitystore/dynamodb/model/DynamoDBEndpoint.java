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
package ai.philterd.entitydb.entitystore.dynamodb.model;

/**
 * Endpoints for DynamoDB regions.
 * Refer to: http://docs.aws.amazon.com/general/latest/gr/rande.html#ddb_region
 * 
 * @author Philterd, LLC
 *
 */
public enum DynamoDBEndpoint {

	/**
	 * us-east-1
	 */
	DYNAMODB_ENDPOINT_US_EAST_1("https://dynamodb.us-east-1.amazonaws.com"),
	
	/**
	 * us-west-1
	 */
	DYNAMODB_ENDPOINT_US_WEST_1("https://dynamodb.us-west-1.amazonaws.com"),
	
	/**
	 * us-west-2
	 */
	DYNAMODB_ENDPOINT_US_WEST_2("https://dynamodb.us-west-2.amazonaws.com"),
	
	/**
	 * eu-west-1
	 */
	DYNAMODB_ENDPOINT_EU_WEST_1("https://dynamodb.eu-west-1.amazonaws.com"),
	
	/**
	 * eu-central-1
	 */
	DYNAMODB_ENDPOINT_EU_CENTRAL_1("https://dynamodb.eu-central-1.amazonaws.com"),
	
	/**
	 * ap-southeast-1
	 */
	DYNAMODB_ENDPOINT_AP_SOUTHEAST_1("https://dynamodb.ap-southeast-1.amazonaws.com"),
	
	/**
	 * ap-southeast-2
	 */
	DYNAMODB_ENDPOINT_AP_SOUTHEAST_2("https://dynamodb.ap-southeast-2.amazonaws.com"),
	
	/**
	 * ap-northeast-1
	 */
	DYNAMODB_ENDPOINT_AP_NORTHEAST_1("https://dynamodb.ap-northeast-1.amazonaws.com"),
	
	/**
	 * sa-east-1
	 */
	DYNAMODB_ENDPOINT_SA_EAST_1("https://dynamodb.sa-east-1.amazonaws.com"),
	
	/**
	 * DynamoDB local client for testing.
	 */
	DYNAMODB_LOCAL("http://localhost:10500");

	private String endpoint;
	
	private DynamoDBEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * Gets the {@link DynamoDBEndpoint endpoint}.
	 * @param endpoint The endpoint.
	 * @return The {@link DynamoDBEndpoint endpoint}.
	 */
	public static DynamoDBEndpoint fromEndpoint(String endpoint) {
		
		for(DynamoDBEndpoint dynamoDBEndpoint : DynamoDBEndpoint.values()) {
			
			if(dynamoDBEndpoint.getEndpoint().equalsIgnoreCase(endpoint)) {
				return dynamoDBEndpoint;
			}
			
		}
		
		throw new IllegalArgumentException("Invalid DynamoDB endpoint: " + endpoint);
		
	}
	
	/**
	 * Gets the endpoint.
	 * @return The endpoint.
	 */
	public String getEndpoint() {
		return endpoint;
	}
	
	/**
	 * Gets the endpoint.
	 * @return The endpoint.
	 */
	@Override
	public String toString() {
		return endpoint;
	}
	
}