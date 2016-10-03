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
package com.mtnfog.entitydb.model;

/**
 * System properties that can change default values and behaviors. Refer to the
 * project's wiki or documentation for information on each property.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class SystemProperties {

	private SystemProperties() {
		// This class contains constants.
	}
	
	/**
	 * The size of batch writes for RDBMS entity stores.
	 */
	public static final String RDBMS_ENTITY_STORE_BATCH_SIZE = "entitydb.rdbms.batchsize";
	
}