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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.search.indexer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mtnfog.entitydb.model.entitystore.AbstractStoredEntity;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;

/**
 * Implementation of {@link Indexer} that indexes entities in ElasticSearch.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class ElasticSearchIndexer implements Indexer {

	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchIndexer.class);

	private SearchIndex searchIndex;
	private EntityStore<?> entityStore;

	/**
	 * Creates a new ElasticSearch indexer.
	 * @param searchIndex The search index.
	 * @param entityStore The entity store.
	 */
	public ElasticSearchIndexer(SearchIndex searchIndex, EntityStore<?> entityStore) {
		
		this.searchIndex = searchIndex;
		this.entityStore = entityStore;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void index(int limit) {
		
		List<?> entities = entityStore.getNonIndexedEntities(limit);
					
		if(CollectionUtils.isNotEmpty(entities)) {
			
			LOGGER.info("Got {} entities to index.", entities.size());
			
			Set<IndexedEntity> entitiesToIndex = new LinkedHashSet<IndexedEntity>();
			Set<String> entityIds = new LinkedHashSet<String>();
			
			for(Object e : entities) {
				
				try {
				
					IndexedEntity indexedEntity = ((AbstractStoredEntity) e).toIndexedEntity();
					LOGGER.trace("Indexing entity {}.", indexedEntity.getEntityId());
					entitiesToIndex.add(indexedEntity);
					entityIds.add(indexedEntity.getEntityId());
				
				} catch (MalformedAclException ex) {
					
					LOGGER.error("The ACL for entity " + e + " is invalid. The entity will not be indexed.", ex);
					
				}
				
			}
			
			Set<String> failedIndexEntityIds = searchIndex.index(entitiesToIndex);
			entityIds.removeAll(failedIndexEntityIds);
			
			long indexed = entityStore.markEntitiesAsIndexed(entityIds);
			
			// If there are more to process don't sleep.
			if(entities.size() > 0) {
				index(limit);
			}
			
		}
	
	}
	
}