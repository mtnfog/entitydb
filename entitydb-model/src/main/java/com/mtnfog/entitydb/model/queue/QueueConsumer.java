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
 * A consumer of a queue to process entities.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface QueueConsumer {
	
	/**
	 * Get the exact or approximate size of the queue.
	 * Some queues may only return an approximate size.
	 * @return The size of the queue.
	 */
	public int getSize();
	
	/**
	 * Consume messages from the queue.
	 */
	public void consume();
	
	/**
	 * Gracefully stop the queue consumer.
	 */
	public void shutdown();
	
}