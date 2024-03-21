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

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.Indexer;
import ai.philterd.entitydb.model.search.SearchIndex;

/**
 * Implementation of {@link Indexer} that indexes entities in ElasticSearch.
 * 
 * @author Philterd, LLC
 *
 */
public class ElasticSearchIndexer extends AbstractIndexer implements Indexer {

	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchIndexer.class);
	
	private int batchSize;
	
	/**
	 * Creates a new ElasticSearch indexer.
	 * @param searchIndex The search index.
	 * @param entityStore The entity store.
	 * @param indexerCache The cache used by the indexer.
	 */
	public ElasticSearchIndexer(SearchIndex searchIndex, EntityStore<?> entityStore, ConcurrentLinkedQueue<IndexedEntity> indexerCache, int batchSize) {
		
		super(searchIndex, entityStore, indexerCache);
		
		this.batchSize = batchSize;
		
	}
	

	@Override
	public long index() {
	
		LOGGER.trace("Indexing up to {} entities in Elasticsearch.", batchSize);
		
		return super.index(batchSize);
		
	}
	
}