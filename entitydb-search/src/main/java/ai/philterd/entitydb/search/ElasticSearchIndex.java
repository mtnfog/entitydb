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
package ai.philterd.entitydb.search;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ai.philterd.entitydb.model.eql.EntityOrder;
import ai.philterd.entitydb.model.eql.SortOrder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.exceptions.InvalidQueryException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import ai.philterd.entitydb.model.search.SearchIndex;
import ai.philterd.entitydb.model.eql.EntityQuery;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.BulkResult.BulkResultItem;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.MaxAggregation;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.indices.CreateIndex;

/**
 * Implementation of {@link SearchIndex} that uses Elasticsearch.
 * 
 * @author Philterd, LLC
 *
 */
public class ElasticSearchIndex implements SearchIndex {
	
	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchIndex.class);
	
	public static final String INDEX_NAME = "entities";
	public static final String TYPE_NAME = "entity";
	private static final int CONNECTION_TIMEOUT = 120000;
	
	private JestClient jestClient;
	private String host;
	
	// A good Jest guide: 	http://docs.searchly.com/documentation/developer-api-guide/java-jest/
	// Another: 			https://github.com/searchbox-io/Jest/tree/master/jest
	
	/**
	 * Creates a new Elasticsearch client.
	 * @param host The hostname of the Elasticsearch cluster.
	 */
	public ElasticSearchIndex(String host) {
		
		if(host.endsWith("/")) {
			host = host.substring(0, host.length() -1);
		}
		
		this.host = host;
		
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(host)
				.connTimeout(CONNECTION_TIMEOUT)
				.readTimeout(CONNECTION_TIMEOUT)
				.multiThreaded(true)
				.build();
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(clientConfig);
	
		jestClient = factory.getObject();
		
		createIndex();
				
	}
	
	/**
	 * Creates a new Elasticsearch client with authentication.
	 * @param host The hostname of the Elasticsearch cluster.
	 * @param username The Elasticsearch username.
	 * @param password The Elasticsearch password.
	 */
	public ElasticSearchIndex(String host, String username, String password) {
		
		if(host.endsWith("/")) {
			host = host.substring(0, host.length() -1);
		}
		
		this.host = host;
		
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(host)
				.connTimeout(CONNECTION_TIMEOUT)
				.readTimeout(CONNECTION_TIMEOUT)
				.multiThreaded(true)
				.defaultCredentials(username, password)
				.build();
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(clientConfig);
	
		jestClient = factory.getObject();
				
		createIndex();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatus() {
		
		return "Elasticsearch (" + host + ")";
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLastIndexedId() {	
		
		String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    },\n" +
                "    \"aggs\" : {\n" +
                "        \"max1\" : {\n" +
                "            \"max\" : {\n" +
                "                \"field\" : \"transactionId\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
		
		 Search search = new Search.Builder(query)
	                .addIndex(INDEX_NAME)
	                .addType(TYPE_NAME)
	                .build();
		 
		 try {
			 
	        SearchResult result = jestClient.execute(search);
	       // assertTrue(result.getErrorMessage(), result.isSucceeded());
	 
	        MaxAggregation max = result.getAggregations().getMaxAggregation("max1");
	        //assertEquals("max1", max.getName());
	        
	        if(max.getMax() != null) {
	        
	        	return max.getMax().longValue();
	        	
	        } else {
	        	
	        	return 0;
	        	
	        }

		 } catch (IOException ex) {
			 
			 LOGGER.error("Unable to get maximum value.", ex);
		 
		 }
		 
		 return -1;
	        
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<String> queryForEntityIds(EntityQuery entityQuery, User user) throws InvalidQueryException {				
		
		List<String> entityIds = new LinkedList<String>();
		
		List<IndexedEntity> indexedEntities = queryForIndexedEntities(entityQuery, user);
			
		for(IndexedEntity indexedEntity : indexedEntities) {
			
			entityIds.add(indexedEntity.getEntityId());
			
		}
					
		return entityIds;
		
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<IndexedEntity> queryForIndexedEntities(EntityQuery entityQuery, User user) throws InvalidQueryException {				
		
		/*
		 * The match query will apply the same standard analyzer to the 
		 * search term and will therefore match what is stored in the index. 
		 * The term query does not apply any analyzers to the search term, so 
		 * will only look for that exact term in the inverted index.
		 * 
		 * Source: http://stackoverflow.com/a/23151332/1428388
		 * 
		 * termQuery matches as-is.
		 * matchQuery matches per the analyzers.
		 */
		
		// Wildcard queries: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html
		// Term queries: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html
		
		List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			
		BoolQueryBuilder qb = QueryBuilders.boolQuery();				
		
		if(StringUtils.isNotEmpty(entityQuery.getText())) {
			
			if(entityQuery.getText().startsWith("*") || entityQuery.getText().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("text", entityQuery.getText()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotText())) {
			
			if(entityQuery.getNotText().startsWith("*") || entityQuery.getNotText().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("text", entityQuery.getNotText()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getContext())) {
			
			if(entityQuery.getContext().startsWith("*") || entityQuery.getContext().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("context", entityQuery.getContext()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotContext())) {
			
			if(entityQuery.getNotContext().startsWith("*") || entityQuery.getNotContext().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("context", entityQuery.getNotContext()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getDocumentId())) {
			
			if(entityQuery.getDocumentId().startsWith("*") || entityQuery.getDocumentId().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("documentId", entityQuery.getDocumentId()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotDocumentId())) {
			
			if(entityQuery.getNotDocumentId().startsWith("*") || entityQuery.getNotDocumentId().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("documentId", entityQuery.getNotDocumentId()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getLanguageCode())) {
			
			if(entityQuery.getLanguageCode().startsWith("*") || entityQuery.getLanguageCode().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("language", entityQuery.getLanguageCode()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotLanguageCode())) {
			
			if(entityQuery.getNotLanguageCode().startsWith("*") || entityQuery.getNotLanguageCode().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("language", entityQuery.getNotLanguageCode()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getUri())) {
			
			if(entityQuery.getUri().startsWith("*") || entityQuery.getUri().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("uri", entityQuery.getUri()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotUri())) {
			
			if(entityQuery.getNotUri().startsWith("*") || entityQuery.getNotUri().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("uri", entityQuery.getNotUri()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getType())) {
			
			if(entityQuery.getType().startsWith("*") || entityQuery.getType().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.must(QueryBuilders.wildcardQuery("type", entityQuery.getType()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getNotType())) {
			
			if(entityQuery.getNotType().startsWith("*") || entityQuery.getNotType().startsWith("?")) {
				throw new InvalidQueryException("A field cannot begin with an asterisk.");
			}
			
			qb.mustNot(QueryBuilders.wildcardQuery("type", entityQuery.getNotType()));
			
		}
				
		if(entityQuery.getConfidenceRange() != null) {
			
			qb.must(QueryBuilders.rangeQuery("confidence")
				.gte(entityQuery.getConfidenceRange().getMinimum())
				.lte(entityQuery.getConfidenceRange().getMaximum()));
			
		}				
		
		StringBuilder sb = new StringBuilder();
		for(String group : user.getGroups()) {
			sb.append(String.format("[,]?%s[,]?|", group));
		}
		
		String groupRegEx = StringUtils.EMPTY;
		
		if(sb.toString().length() > 0) {
			// Remove the last pipe in the regex.
			groupRegEx = sb.toString().subSequence(0, sb.toString().length() - 1).toString();
		}
		
		// Set the ACL based on the user's permissions.
		qb.must(QueryBuilders.boolQuery()
				.should(QueryBuilders.regexpQuery("acl.users", String.format("[,]?%s[,]?", user.getUsername()))) 
				.should(QueryBuilders.regexpQuery("acl.groups", groupRegEx))
				.should(QueryBuilders.termQuery("acl.world", 1))
				.minimumNumberShouldMatch(1)
		);
		
		searchSourceBuilder.size(entityQuery.getLimit());
		searchSourceBuilder.query(qb);
		
		LOGGER.debug("Executing query: " + searchSourceBuilder.toString());
		
		// The default sort is by rank.
		Sort sort = new Sort("rank", Sort.Sorting.DESC);
		
		if(entityQuery.getEntityOrder() == EntityOrder.TEXT) {
			
			if(entityQuery.getSortOrder().equals(SortOrder.ASC)) {
			
				sort = new Sort("text", Sort.Sorting.ASC);
				
			} else {
				
				sort = new Sort("text", Sort.Sorting.DESC);
				
			}
		
		} else if(entityQuery.getEntityOrder() == EntityOrder.CONFIDENCE) {
			
			if(entityQuery.getSortOrder().equals(SortOrder.ASC)) {
				
				sort = new Sort("confidence", Sort.Sorting.ASC);
				
			} else {
				
				sort = new Sort("confidence", Sort.Sorting.DESC);
				
			}
			
		} else if(entityQuery.getEntityOrder() == EntityOrder.ID) {
			
			if(entityQuery.getSortOrder().equals(SortOrder.ASC)) {
				
				sort = new Sort("id", Sort.Sorting.ASC);
				
			} else {
				
				sort = new Sort("id", Sort.Sorting.DESC);
				
			}			
			
		}
						
		Search search = new Search.Builder(searchSourceBuilder.toString())
			.addIndex(INDEX_NAME)
			.addType(TYPE_NAME)
			.addSort(sort)
			.build();
		
		try {					
						
			JestResult result = jestClient.execute(search);
			
			indexedEntities = result.getSourceAsObjectList(IndexedEntity.class);
					
		} catch (IOException ex) {
			
			LOGGER.error("Unable to execute query.", ex);
			
		}
		
		return indexedEntities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getCount() {
		
		long count = -1;
		
		try {
		
			CountResult result = jestClient.execute(new Count.Builder()
	                .addIndex(INDEX_NAME)
	                .build());

		 	Double c = result.getCount();
		 	
		 	if(c != null) {
		 		count = c.longValue();
		 	}
		 
		} catch (IOException ex) {		
			
			LOGGER.error("Unable to get search index count.", ex);
			
		}		 		 
		
		return count;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndexedEntity getEntity(String entityId) {
		
		IndexedEntity indexedEntity = null;
		
		Get get = new Get.Builder(INDEX_NAME, entityId).type(TYPE_NAME).build();

		try {
		
			JestResult result = jestClient.execute(get);
	
			indexedEntity = result.getSourceAsObject(IndexedEntity.class);
		
		} catch (IOException ex) {
				
			LOGGER.error("Unable to get entity with ID: " + entityId, ex);
			
		}
		
		return indexedEntity;
			
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean index(IndexedEntity indexedEntity) {
		
        Index index = new Index.Builder(indexedEntity).id(indexedEntity.getEntityId()).index(INDEX_NAME).type(TYPE_NAME).build();
        
        try {        	        	
        	
        	DocumentResult documentResult = jestClient.execute(index);
        	
        	boolean result = documentResult.isSucceeded();        	        	
        	
        	if(!result) {
        		
        		LOGGER.error("Unable to index entity in Elasticsearch. Entity ID: " + indexedEntity.getEntityId() + ". Reason: " + documentResult.getErrorMessage()); 
        		
        	}
        	
        	return result;
        	
        } catch(IOException ex) {

        	LOGGER.error("Unable to index entity in Elasticsearch. Entity ID: " + indexedEntity.getEntityId(), ex);
        
        	return false;
        	
        }
        
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> index(Set<IndexedEntity> indexedEntities) {
				
		Set<String> failedEntityIds = new HashSet<String>();
        
        try {        	        	
        	
        	List<Index> indexes = new LinkedList<Index>();
        	
        	for(IndexedEntity indexedEntity : indexedEntities) {
        		indexes.add(new Index.Builder(indexedEntity).build());
        	}
        	
        	Bulk bulk = new Bulk.Builder()
                    .defaultIndex(INDEX_NAME)
                    .defaultType(TYPE_NAME)
                    .addAction(indexes)
                    .build();

        	BulkResult bulkResult = jestClient.execute(bulk);
        	
        	boolean result = bulkResult.isSucceeded();
        	
        	if(!result) {
        		        		
        		LOGGER.error("Unable to index entities in Elasticsearch. Reason: " + bulkResult.getErrorMessage()); 
        		
        		for(BulkResultItem item : bulkResult.getFailedItems()) {        			
        			failedEntityIds.add(item.id);        			
        		}
        		
        	}
 
        } catch(IOException ex) {

        	LOGGER.error("Unable to index entities in Elasticsearch.", ex);
        	
        	// This was a connection error. None of the entities were stored.
        	
        	for(IndexedEntity indexedEntity : indexedEntities) {
        		failedEntityIds.add(indexedEntity.getEntityId());
        	}
        	
        }
        
        return failedEntityIds;
        
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(IndexedEntity indexedEntity) {
		
		Update update = new Update.Builder(indexedEntity).id(indexedEntity.getEntityId()).index(INDEX_NAME).type(TYPE_NAME).build();
		
		try {
        	
        	DocumentResult documentResult = jestClient.execute(update);
        	
        	return documentResult.isSucceeded();
        	
        } catch(IOException ex) {
        	
        	LOGGER.error("Unable to update entity in Elasticsearch. Entity ID: " + indexedEntity.getEntityId(), ex);
        
        	return false;
        	
        }

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean delete(String entityId) {
		
		Delete delete = new Delete.Builder(entityId).build();
		
		try {
		
			DocumentResult documentResult = jestClient.execute(delete);
		
			boolean result = documentResult.isSucceeded();
			
			LOGGER.debug("Document insertion into Elasticsearch success: {}", result);
			
			return result;
			
		} catch(IOException ex) {
        	
			LOGGER.error("Unable to delete entity from Elasticsearch. Entity ID: " + entityId, ex);
        
        	return false;
        	
        }
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		
		jestClient.shutdownClient();
		
	}
	
	/**
	 * Create the index on Elasticsearch.
	 * @return <code>true</code> if the index was created or if the index already exists; otherwise <code>false</code>.
	 */
	private boolean createIndex() {
				
		LOGGER.info("Creating Elasticsearch index: {}", INDEX_NAME);
		
		boolean result = true;
		
		try {									
				
			final String settings = getSettings();
			
			CreateIndex createIndex = new CreateIndex.Builder(INDEX_NAME)
					.settings(settings)
	                .build();
			
			JestResult jestResult = jestClient.execute(createIndex);
			
			result = jestResult.isSucceeded();
			
			LOGGER.info("Elasticsearch index creation status: " + jestResult.getJsonString());
						
		} catch (IOException ex) {
			
			LOGGER.error("Unable to check for or create index.", ex);
			
		}
		
		return result;
		
	}
	
	private String getSettings() {
		
		String settings = null;
		
		try {
		
			settings = FileUtils.readFileToString(new File("mapping.json"));
		
		} catch (IOException ex) {
			
			LOGGER.error("Unable to read Elasticsearch settings file.", ex);
			
		}
		
		return settings;
		
	}
		
}