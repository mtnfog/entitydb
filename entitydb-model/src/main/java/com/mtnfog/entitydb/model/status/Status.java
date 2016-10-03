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

package com.mtnfog.entitydb.model.status;

import java.util.Date;

/**
 * The status of EntityDB.
 * @author Mountain Fog, Inc.
 *
 */
public class Status {

	private long indexed;
	private long stored;
	private long timestamp;
	
	/**
	 * Creates a new status.
	 * @param indexed The number of indexed entities.
	 * @param stored The number of stored entities.
	 */
	public Status(long indexed, long stored) {
		this.indexed = indexed;
		this.stored = stored;
		this.timestamp = new Date().getTime();
	}
	
	public long getIndexed() {
		return indexed;
	}
	
	public long getStored() {
		return stored;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
}