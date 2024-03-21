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
package ai.philterd.entitydb.metrics.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.util.EC2MetadataUtils;

/**
 * Utility class for metric functions.
 * 
 * @author Philterd, LLC
 *
 */
public class MetricUtils {

	private static final Logger LOGGER = LogManager.getLogger(MetricUtils.class);
	
	private static String systemId = null;
	
	private MetricUtils() {
		// This is a utility class.
	}
	
	/**
	 * Get the system ID. First, this function attempts to look up the EC2 instance's
	 * instance ID. If that fails it attemps to get the local hostname. If that also fails,
	 * a random {@link UUID} is returned. Note that this lookup process only occurs the first
	 * time this function is called.
	 * @return The system ID.
	 */
	public static String getSystemId() {
	
		if(StringUtils.isEmpty(systemId)) {
		
			// If all else fails use a random UUID.
			String systemId = UUID.randomUUID().toString();
			
			LOGGER.trace("Retrieving the EC2 instance ID.");
				
			systemId = EC2MetadataUtils.getInstanceId();
			
			if(StringUtils.isNotEmpty(systemId)) {
				
				return systemId;
				
			} else {
				
				try {
				
					systemId = InetAddress.getLocalHost().getHostName();
					
				} catch (UnknownHostException ex) {
					
					LOGGER.error("Unable to determine system host name.", ex);
					
				}
				
			}
			
		}
		
		return systemId;
		
	}
	
}