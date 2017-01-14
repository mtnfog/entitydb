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
package com.mtnfog.entitydb.metrics.utils;

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
 * @author Mountain Fog, Inc.
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