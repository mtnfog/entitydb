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
package ai.philterd.entitydb.caching.memcached;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.spy.memcached.MemcachedClient;

/**
 * Implementation of Spring's {@link Cache} interface that uses memcached.
 * 
 * @author Philterd, LLC
 *
 */
public class MemcachedCache implements Cache {

	private static final Logger LOGGER = LogManager.getLogger(MemcachedCache.class);

	private String name;
	private MemcachedClient client;		
	private int ttl;
	
	/**
	 * Creates a new implementation of {@link Cache} for memcached.
	 * @param client A {@link MemcachedClient client}.
	 * @param name The name of the cache.
	 * @param ttl The TTL for cached items.
	 */
	public MemcachedCache(MemcachedClient client, String name, int ttl) {
		
		this.client = client;
		this.name = name;
		this.ttl = ttl;
		
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public Object getNativeCache() {
		return client;
	}


	@Override
	public ValueWrapper get(final Object key) {
		
		Object value = null;
		
		try {
			
			value = client.get(key.toString());
			
		} catch (final Exception ex) {
			LOGGER.error("Unable to get item with key " + key.toString() + " from the cache.", ex);
		}
		
		if (value == null) {
			
			return null;
			
		} else {
		
			return new SimpleValueWrapper(value);
			
		}
		
	}


	@Override
	public void put(final Object key, final Object value) {
		client.set(key.toString(), ttl, value);
	}


	@Override
	public void evict(final Object key) {
		client.delete(key.toString());
	}


	@Override
	public void clear() {
		client.flush();
	}


	@Override
	public <T> T get(Object key, Class<T> type) {
		return (T) client.get((String) key);
	}


	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		client.set(key.toString(), ttl, value);
		return new SimpleValueWrapper(value);
	}
	

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return (T) client.get((String) key);
	}

}