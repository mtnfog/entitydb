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
package com.mtnfog.entitydb.security;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.security.Acl;

public class AclUtils {
	
	private static final Logger LOGGER = LogManager.getLogger(AclUtils.class);

	private AclUtils() {
		// This is a utility class.
	}
	
	public static String generateAcl(Acl acl) {
		
		return generateAcl(acl.getUsers(), acl.getGroups(), acl.isWorld());
		
	}
	
	public static String generateAcl(String users, String groups, boolean world) {
	
		int worldValue = 0;
		
		if(world) worldValue = 1;
		
		String acl = String.format("%s:%s:%d", users, groups, worldValue);
		
		return acl;
		
	}
	
	public static String generateAcl(Set<String> users, Set<String> groups, boolean world) {		
		
		int worldValue = 0;
		
		if(world) worldValue = 1;
		
		String acl = String.format("%s:%s:%d", StringUtils.join(users, ","), StringUtils.join(groups, ","), worldValue);
		
		return acl;
		
	}
	
}