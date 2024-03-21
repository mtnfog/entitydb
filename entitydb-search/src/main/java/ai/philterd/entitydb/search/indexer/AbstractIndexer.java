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
package ai.philterd.entitydb.search.indexer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.entitystore.AbstractStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;

/**
 * Base class for indexers.
 * 
 * @author Philterd, LLC
 *
 */
public abstract class AbstractIndexer {

	private static final Logger LOGGER = LogManager.getLogger(AbstractIndexer.class);
	
	private final SearchIndex searchIndex;
	private final EntityStore<?> entityStore;
	private final ConcurrentLinkedQueue<IndexedEntity> indexerCache;
	
	/**
	 * Base constructor for indexers.
	 * @param searchIndex The {@link SearchIndex}.
	 * @param entityStore The {@link EntityStore}.
	 * @param indexerCache The indexer cache.
	 */
	public AbstractIndexer(SearchIndex searchIndex, EntityStore<?> entityStore, ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		this.searchIndex = searchIndex;
		this.entityStore = entityStore;
		this.indexerCache = indexerCache;
		
	}
	
	/**
	 * Indexes entities in the search index. When this function is executed it first looks
	 * in the internal list of entities to index. If there are none then it asks the
	 * database for any entities that are not marked as indexed. Note that the internal
	 * list of entities to index is exclusive to each instance of EntityDB.
	 * 
	 * @param limit THe maximum batch size for indexing operations.
	 */
	protected long index(int limit) {
		
		LOGGER.info("Executing indexer.");
		
		long indexed = 0;
		
		final Set<IndexedEntity> entitiesToIndex = new LinkedHashSet<>();
		final Set<String> entityIds = new LinkedHashSet<>();
	
		if(!indexerCache.isEmpty()) {

			for(int x = 0; x <= limit; x++) {
			
				// The entity is removed from the queue. If something happens and the entity fails to
				// index the entity will not be on the queue anymore but it will be picked up when
				// the indexer looks at the database for non-indexed entities.

				final IndexedEntity indexedEntity = indexerCache.poll();
				
				if(indexedEntity != null) {
						
					entitiesToIndex.add(indexedEntity);
					entityIds.add(indexedEntity.getEntityId());
					
				} else {
					
					break;
					
				}
				
			}						
			
		} else {

			final List<?> entities = entityStore.getNonIndexedEntities(limit);
					
			if(CollectionUtils.isNotEmpty(entities)) {
				
				LOGGER.debug("Got {} entities to index from the database.", entities.size());
								
				for(Object e : entities) {
					
					try {
					
						IndexedEntity indexedEntity = ((AbstractStoredEntity) e).toIndexedEntity();
						entitiesToIndex.add(indexedEntity);
						entityIds.add(indexedEntity.getEntityId());
					
					} catch (MalformedAclException ex) {
						
						LOGGER.error("The ACL for entity " + e.toString() + " is invalid. The entity will not be indexed.", ex);
						
					}
					
				}				
							
			}
			
		}
		
		if(CollectionUtils.isNotEmpty(entitiesToIndex)) {

			final Set<String> failedIndexEntityIds = searchIndex.index(entitiesToIndex);
			entityIds.removeAll(failedIndexEntityIds);
			
			indexed = entityStore.markEntitiesAsIndexed(entityIds);
			
			// TODO: Report metrics on how long each entity waited to be indexed.
			
			LOGGER.info("Indexed {} entities.", indexed);
		
		} else {
					
			LOGGER.debug("No entities to index.");
			
		}
	
		return indexed;
		
	}
	
}