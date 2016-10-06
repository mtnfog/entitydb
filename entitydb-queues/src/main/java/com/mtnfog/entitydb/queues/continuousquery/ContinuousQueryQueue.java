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
package com.mtnfog.entitydb.queues.continuousquery;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.mtnfog.entitydb.queues.messages.InternalQueueContinuousQueryMessage;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.InMemoryObjectQueue;
import com.squareup.tape.ObjectQueue;

public class ContinuousQueryQueue {

	private static final Logger LOGGER = LogManager.getLogger(ContinuousQueryQueue.class);

	private static ObjectQueue<InternalQueueContinuousQueryMessage> continuousQueryQueue;

	public static ObjectQueue<InternalQueueContinuousQueryMessage> getContinuousQueryQueue() {
				
		if(continuousQueryQueue == null) {
			
			//try {
			
				// TODO: Set the queue file name.
				/*File backendFile = File.createTempFile(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());				
				LOGGER.info("Using continuous query file {}.", backendFile.getAbsolutePath());			
				Gson gson = new Gson();					
				continuousQueryQueue = new FileObjectQueue<InternalQueueContinuousQueryMessage>(backendFile, new GsonConverter<InternalQueueContinuousQueryMessage>(gson, InternalQueueContinuousQueryMessage.class));*/
				
				// TODO: Use the FileObjectQueue when I figure out why it throws EOF when creating it.
				// See: https://github.com/square/tape/issues/125
				continuousQueryQueue = new InMemoryObjectQueue<InternalQueueContinuousQueryMessage>();
				
			/*} catch (IOException ex) {
				
				LOGGER.error("Unable to create queue for continuous queries.", ex);
				
			}*/
			
		}
		
		return continuousQueryQueue;

	}
	
	
}