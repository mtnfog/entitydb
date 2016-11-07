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

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.search.IndexedEntity;
import com.mtnfog.entitydb.model.search.Indexer;
import com.mtnfog.entitydb.model.search.SearchIndex;

/**
 * Implementation of {@link Indexer} that indexes entities in ElasticSearch.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class ElasticSearchIndexer extends AbstractIndexer implements Indexer {

	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchIndexer.class);
	
	/**
	 * Creates a new ElasticSearch indexer.
	 * @param searchIndex The search index.
	 * @param entityStore The entity store.
	 * @param indexerCache The cache used by the indexer.
	 */
	public ElasticSearchIndexer(SearchIndex searchIndex, EntityStore<?> entityStore, ConcurrentLinkedQueue<IndexedEntity> indexerCache) {
		
		super(searchIndex, entityStore, indexerCache);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void index(int limit) {
	
		LOGGER.trace("Indexing up to {} entities in Elasticsearch.", limit);
		
		super.index(limit);
		
	}
	
}