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
package com.mtnfog.entitydb.model.queue;

/**
 * Constants used by the queues.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class QueueConstants {
	
	/**
	 * The action to be taken on the queue message.
	 */
	public static final String ACTION = "action";	
	
	/**
	 * An entity is to be ingested.
	 */
	public static final String ACTION_INGEST = "ingest";
	
	/**
	 * An entity's ACL is to be updated.
	 */
	public static final String ACTION_UPDATE_ACL = "updateAcl";	
	
	private QueueConstants() {
		// This is a utility class.
	}
	
}