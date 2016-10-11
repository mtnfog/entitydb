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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mtnfog.entitydb.caching.memcached.MemcachedCache;
import com.mtnfog.entitydb.caching.memcached.MemcachedCacheManager;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;

/**
 * Implementation of Spring's {@link CachingConfigurer} that
 * provides a caching configuration for Memcached.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	
	private static final Logger LOGGER = LogManager.getLogger(CacheConfig.class);
	
	/*@Bean
	@Override
	public CacheManager cacheManager() {

	     SimpleCacheManager cacheManager = new SimpleCacheManager();
	     cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("entitydb")));

	     return cacheManager;
	     
	}*/
	
	// TODO: Make these properties.
	private String memcachedHost = "192.168.0.20:11211";
	private int ttl = 3600;
	private String memcachedUsername = "";
	private String memcachedPassword = "";
	
	@Bean
	@Override
	public CacheManager cacheManager() {
		
		CacheManager cacheManager = null;
		
		try {
		
			MemcachedClient memcachedClient = new MemcachedClient(
					new ConnectionFactoryBuilder()
						//.setTranscoder(new CustomSerializingTranscoder())
						.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
						//.setAuthDescriptor(ad)
						.build(),
						AddrUtil.getAddresses(memcachedHost));
			
			LOGGER.info("Created Memcached client for {}.", memcachedHost);
			
			final Collection<MemcachedCache> caches = new ArrayList<MemcachedCache>();
			
			caches.add(new MemcachedCache(memcachedClient, "nonExpiredContinuousQueries", ttl));
			caches.add(new MemcachedCache(memcachedClient, "continuousQueriesByUser", ttl));
			
			cacheManager = new MemcachedCacheManager(caches);
		
		} catch (IOException ex) {
			
			LOGGER.error("Unable to create memcached client.", ex);
			
		}
		
		return cacheManager;
		
	}

}