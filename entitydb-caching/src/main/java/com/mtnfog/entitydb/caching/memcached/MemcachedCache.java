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
package com.mtnfog.entitydb.caching.memcached;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import net.spy.memcached.MemcachedClient;

/**
 * Implementation of Spring's {@link Cache} interface that uses memcached.
 * 
 * @author Mountain Fog, Inc.
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