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
package com.mtnfog.entitydb.entitystore.dynamodb.model;

/**
 * Endpoints for DynamoDB regions.
 * Refer to: http://docs.aws.amazon.com/general/latest/gr/rande.html#ddb_region
 * 
 * @author Mountain Fog, Inc.
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