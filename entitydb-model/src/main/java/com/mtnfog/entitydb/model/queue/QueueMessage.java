package com.mtnfog.entitydb.model.queue;

/**
 * A base class for messages that are queued. All classes that
 * inherit {@link QueueConsumer} and {@link QueuePublisher} should
 * use the classes extending {@link QueueMessage} in their implementations.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public abstract class QueueMessage {
	
	/**
	 * Used to determine how long a message stays in a queue before
	 * being processed. This value is reported by a {@link MetricReporter}.
	 */
	private long timestamp = System.currentTimeMillis();
	
	/**
	 * Gets the timestamp of when this message was created.
	 * @return The timestamp of when this message was created.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
}