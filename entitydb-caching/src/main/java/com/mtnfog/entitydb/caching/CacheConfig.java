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
package com.mtnfog.entitydb.caching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mtnfog.entitydb.caching.memcached.MemcachedCache;
import com.mtnfog.entitydb.caching.memcached.MemcachedCacheManager;
import com.mtnfog.entitydb.configuration.EntityDbProperties;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;

/**
 * This class provides the {@link CacheManager} that is used by
 * EntityDB for caching entities and queries.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	
	private static final Logger LOGGER = LogManager.getLogger(CacheConfig.class);
	
	private static final EntityDbProperties properties = ConfigFactory.create(EntityDbProperties.class);
	
	/**
	 * {@inheritDoc}
	 * 
	 * Depending on EntityDB's properties, this function will either configure and return
	 * a {@link MemcachedCacheManager} or a {@link SimpleCacheManager}. If there is an error
	 * configuring the {@link MemcachedCacheManager} the function will return <code>null</code>.
	 * 
	 */
	@Bean
	@Override
	public CacheManager cacheManager() {
		
		CacheManager cacheManager = null;
		
		if(StringUtils.equalsIgnoreCase(properties.getCache(), "memcached")) {
			
			try {
				
				MemcachedClient memcachedClient = new MemcachedClient(
						new ConnectionFactoryBuilder()
							.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
							.build(),
							AddrUtil.getAddresses(properties.getMemcachedHosts()));
				
				LOGGER.info("Created Memcached client for {}.", properties.getMemcachedHosts());
				
				final Collection<MemcachedCache> caches = new ArrayList<MemcachedCache>();
				
				for(String cacheName : getCacheNames()) {
				
					caches.add(new MemcachedCache(memcachedClient, cacheName, properties.getCacheTtl()));
					
				}
				
				return new MemcachedCacheManager(caches);
			
			} catch (IOException ex) {
				
				LOGGER.error("Unable to create memcached client.", ex);
				
			}
			
		} else {
			
			LOGGER.info("Using internal cache.");
			
			SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
			
			List<ConcurrentMapCache> caches = new LinkedList<ConcurrentMapCache>();
			
			for(String cacheName : getCacheNames()) {
				
				caches.add(new ConcurrentMapCache(cacheName));
				
			}
			
			simpleCacheManager.setCaches(caches);
			
			return cacheManager;

		}
		
		return null;
		
	}
	
	private List<String> getCacheNames() {
		
		List<String> cacheNames = new LinkedList<String>();
		cacheNames.add("nonExpiredContinuousQueries");
		cacheNames.add("continuousQueriesByUser");
		cacheNames.add("general");
		
		return cacheNames;
		
	}

}