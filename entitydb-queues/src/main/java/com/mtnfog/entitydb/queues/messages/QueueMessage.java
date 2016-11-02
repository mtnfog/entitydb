package com.mtnfog.entitydb.queues.messages;

/**
 * A base class for messages that are queued. Note that
 * not all queue implementations use this class.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public abstract class QueueMessage {
	
	// Used to determine how long a message stays in a queue. 
	private long timestamp;
	
	public QueueMessage() {
		
		timestamp = System.currentTimeMillis();
		
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}