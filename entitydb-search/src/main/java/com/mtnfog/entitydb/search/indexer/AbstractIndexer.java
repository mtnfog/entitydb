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
package com.mtnfog.entitydb.search.indexer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.entitystore.AbstractStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.SearchIndex;

/**
 * Base class for indexers.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public abstract class AbstractIndexer {

	private static final Logger LOGGER = LogManager.getLogger(AbstractIndexer.class);
	
	private SearchIndex searchIndex;
	private EntityStore<?> entityStore;
	private ConcurrentLinkedQueue<IndexedEntity> indexerCache;
	
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
		
		Set<IndexedEntity> entitiesToIndex = new LinkedHashSet<IndexedEntity>();
		Set<String> entityIds = new LinkedHashSet<String>();
	
		if(!indexerCache.isEmpty()) {

			for(int x = 0; x <= limit; x++) {
			
				// The entity is removed from the queue. If something happens and the entity fails to
				// index the entity will not be on the queue anymore but it will be picked up when
				// the indexer looks at the database for non-indexed entities.
				
				IndexedEntity indexedEntity = indexerCache.poll();
				
				if(indexedEntity != null) {
						
					entitiesToIndex.add(indexedEntity);
					entityIds.add(indexedEntity.getEntityId());
					
				} else {
					
					break;
					
				}
				
			}						
			
		} else {
		
			List<?> entities = entityStore.getNonIndexedEntities(limit);
					
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
		
			Set<String> failedIndexEntityIds = searchIndex.index(entitiesToIndex);
			entityIds.removeAll(failedIndexEntityIds);
			
			indexed = entityStore.markEntitiesAsIndexed(entityIds);
			
			LOGGER.info("Indexed {} entities.", indexed);
		
		} else {
					
			LOGGER.debug("No entities to index.");
			
		}
	
		return indexed;
		
	}
	
}