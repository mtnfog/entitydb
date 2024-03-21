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
package ai.philterd.entitydb.entitystore.cassandra;

import ai.philterd.entitydb.entitystore.cassandra.model.CassandraStoredEntity;
import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.eql.EntityQuery;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link EntityStore} that uses a Cassandra database.
 * Query support is provided through the {@link EntityQuery} class.
 * More complex queries not supported by the {@link EntityQuery} can be
 * achieved through external SQL queries.
 * 
 * @author Philterd, LLC
 *
 */
public class CassandraEntityStore implements EntityStore<CassandraStoredEntity> {

	private static final Logger LOGGER = LogManager.getLogger(CassandraEntityStore.class);
	
	public static final String TABLE_NAME = "entities";
	private static final int DEFAULT_PAGE_SIZE = 25;
		
	private final String host;
	private final String keySpace;
	private final CqlSession session;
	
	/**
	 * Creates a Cassandra entity store. 
	 * @param host The host name or IP address of the Cassandra host.
	 * @param port The Cassandra port.
	 * @param keySpace The Cassandra keyspace.
	 */
	public CassandraEntityStore(String host, int port, String keySpace) {
		
		this.host = host;
		this.keySpace = keySpace;

		this.session = CqlSession.builder()
				.addContactPoint(new InetSocketAddress(host, port))
				.withKeyspace(keySpace)
				.withLocalDatacenter("datacenter1")
				.build();

	}
	
	/**
	 * Creates a Cassandra entity store.
	 * @param session The pre-existing Cassandra session.
	 * @param keySpace The Cassandra keyspace.
	 */
	public CassandraEntityStore(CqlSession session, String keySpace) {
		
		this.host = session.getName();
		this.keySpace = keySpace;
		this.session = session;
		
	}
	

	@Override
	public String getStatus() {
	
		return String.format("Cassandra host: %s, keyspace: %s, table: %s", host, keySpace, TABLE_NAME);
		
	}
	

	@Override
	public List<CassandraStoredEntity> getNonIndexedEntities(int limit) {

		// We can NOT allow filtering here because this query WILL be run.
		// The "indexed" column is indexed, but we can only use one index.
		// Because of being restricted to a single index, the "visible"
		// column is checked on each returned entity and not in the query.

		final SimpleStatement statement = SimpleStatement.builder("SELECT * FROM " + TABLE_NAME + " WHERE indexed = ?")
				.addPositionalValues("0")
				.setKeyspace(keySpace)
				.build();

		final ResultSet resultSet = session.execute(statement);

		final List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();

		final Iterator<Row> iterator = resultSet.iterator();
		
		while(iterator.hasNext()) {
			
			final Row row = iterator.next();
			
			CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			// Only "visible" entities need indexed.
			if(cassandraStoredEntity.getVisible() == 1) {
			
				cassandraStoredEntities.add(cassandraStoredEntity);
			
			}
			
		}
		
		return cassandraStoredEntities;
		
	}
	

	@Override
	public boolean markEntityAsIndexed(String entityId) {
		
		boolean result = false;

		final CassandraStoredEntity entity = getEntityById(entityId);

		if(entity != null) {

			final SimpleStatement statement = SimpleStatement.builder("UPDATE " + TABLE_NAME + " SET indexed = ? WHERE id = ?")
					.addPositionalValues("0")
					.addPositionalValues(entityId)
					.setKeyspace(keySpace)
					.build();
			
			try {

				final ResultSet resultSet = session.execute(statement);
				
				// wasApplied() is not the right function here because
				// this is not a conditional update, but it does indicate
				// that execution completed.
				result = resultSet.wasApplied();
		
			} catch (Exception ex) {
				
				LOGGER.error("Unable to mark entity " + entityId + " as indexed.", ex);
				
			}
			
		}
		
		return result;
		
	}
	

	@Override
	public long markEntitiesAsIndexed(Collection<String> entityIds) {
		
		int indexed = 0;
		
		for(final String entityId : entityIds) {

			final boolean marked = markEntityAsIndexed(entityId);
			
			if(marked) {
				indexed++;
			}
			
		}
		
		return indexed;
		
	}


	@Override
	public String storeEntity(Entity entity, String acl) throws EntityStoreException {

		final String entityId = EntityIdGenerator.generateEntityId(entity, acl);
		
		try {
		
			final Date timestamp = new Date();

			final SimpleStatement statement = SimpleStatement.builder("INSERT INTO " + TABLE_NAME + " id, text, confidence, type, context, documentid, uri, language, extractiondate, acl, metadata, timestamp, visible, indexed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.addPositionalValues(entityId)
					.addPositionalValues(entity.getText())
					.addPositionalValues(entity.getConfidence())
					.addPositionalValues(entity.getType())
					.addPositionalValues(entity.getContext())
					.addPositionalValues(entity.getDocumentId())
					.addPositionalValues(entity.getUri())
					.addPositionalValues(entity.getLanguageCode())
					.addPositionalValues(timestamp.getTime())
					.addPositionalValues(acl)
					.addPositionalValues(entity.getMetadata())
					.addPositionalValues(System.currentTimeMillis())
					.addPositionalValues(1)
					.addPositionalValues(0L)
					.setKeyspace(keySpace)
					.build();

			session.execute(statement);
		
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to store entity.", ex);
			
		}
		
		return entityId;
				
	}
	

	@Override
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException {

		final String newEntityId;

		final CassandraStoredEntity entity = getEntityById(entityId);
		
		if(entity != null) {
		
			// Create the cloned entity and save it.
			final CassandraStoredEntity cloned = new CassandraStoredEntity();
						
			cloned.setAcl(acl);
			cloned.setTimestamp(System.currentTimeMillis());
			cloned.setConfidence(entity.getConfidence());
			cloned.setContext(entity.getContext());
			cloned.setDocumentId(entity.getDocumentId());
			cloned.setMetadata(entity.getMetadata());
			cloned.setExtractionDate(entity.getExtractionDate());
			cloned.setLanguage(entity.getLanguage());
			cloned.setText(entity.getText());
			cloned.setType(entity.getType());
			cloned.setUri(entity.getUri());
			cloned.setIndexed(0);
			
			// Make the ID for the new entity and set it.
			newEntityId = EntityIdGenerator.generateEntityId(cloned.getText(), cloned.getConfidence(), cloned.getLanguage(), cloned.getContext(), cloned.getDocumentId(), acl);
			cloned.setId(newEntityId);

			final SimpleStatement newEntityStatement = SimpleStatement.builder("INSERT INTO " + TABLE_NAME + " id, text, confidence, type, context, documentid, uri, language, extractiondate, acl, metadata, timestamp, visible, indexed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.addPositionalValues(newEntityId)
					.addPositionalValues(cloned.getText())
					.addPositionalValues(cloned.getConfidence())
					.addPositionalValues(cloned.getType())
					.addPositionalValues(cloned.getContext())
					.addPositionalValues(cloned.getDocumentId())
					.addPositionalValues(cloned.getUri())
					.addPositionalValues(cloned.getLanguage())
					.addPositionalValues(cloned.getExtractionDate())
					.addPositionalValues(acl)
					.addPositionalValues(cloned.getMetadata())
					.addPositionalValues(System.currentTimeMillis())
					.addPositionalValues(1)
					.addPositionalValues(0L)
					.setKeyspace(keySpace)
					.build();

			final SimpleStatement hideEntityStatement = SimpleStatement.builder("UPDATE " + TABLE_NAME + " SET visible = ? WHERE id = ?")
					.addPositionalValues("0")
					.addPositionalValues(entityId)
					.setKeyspace(keySpace)
					.build();

			// TODO: Execute these statements in a transaction.
			session.execute(newEntityStatement);
			session.execute(hideEntityStatement);
        
		} else {
			
			throw new NonexistantEntityException("The entity does not exist.");
			
		}
        		
		return newEntityId;
		
	}


	@Override
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) throws EntityStoreException {

		final Map<Entity, String> storedEntities = new HashMap<>();
		
		for(final Entity entity : entities) {

			final String entityId = storeEntity(entity, acl);
			
			storedEntities.put(entity, entityId);
			
		}
		
		return storedEntities;
		
	}
	

	@Override
	public void deleteEntity(String entityId) {

		final SimpleStatement statement = SimpleStatement.builder("DELETE FROM " + TABLE_NAME + " WHERE id = ?")
				.addPositionalValues(entityId)
				.setKeyspace(keySpace)
				.build();
		
		session.execute(statement);
		
	}
	

	@Override
	public List<CassandraStoredEntity> getEntitiesByIds(List<String> entityIds, boolean maskAcl) {

		final List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();
		
		for(final String entityId : entityIds) {

			final CassandraStoredEntity entity = getEntityById(entityId);
			
			if(entity != null) {
				
				if(maskAcl) {
					entity.setAcl(StringUtils.EMPTY);
				}
				
				cassandraStoredEntities.add(entity);
				
			}
			
		}
		
		return cassandraStoredEntities;
		
	}

	/**
	 * {@inheritDoc}
	 * This query will not be optimal because filtering is enabled.
	 */
	@Override
	public QueryResult query(EntityQuery entityQuery) throws EntityStoreException {

//		final Select select = QueryBuilder.select().from(keySpace, TABLE_NAME);
//
//		if(StringUtils.isNotEmpty(entityQuery.getText())) {
//			select.where(eq("text", entityQuery.getText()));
//		}
//
//		if(StringUtils.isNotEmpty(entityQuery.getType())) {
//			select.where(eq("type", entityQuery.getType()));
//		}
//
//		if(StringUtils.isNotEmpty(entityQuery.getLanguageCode())) {
//			select.where(eq("language", entityQuery.getLanguageCode()));
//
//		}
//
//		if(StringUtils.isNotEmpty(entityQuery.getUri())) {
//			select.where(eq("uri", entityQuery.getUri()));
//		}
//
//		if(StringUtils.isNotEmpty(entityQuery.getContext())) {
//			select.where(eq("context", entityQuery.getContext()));
//		}
//
//		if(StringUtils.isNotEmpty(entityQuery.getDocumentId())) {
//			select.where(eq("documentid", entityQuery.getDocumentId()));
//		}
//
//		if(entityQuery.getConfidenceRange() != null) {
//			select.where(gte("confidence", entityQuery.getConfidenceRange().getMinimum()));
//			select.where(lte("confidence", entityQuery.getConfidenceRange().getMaximum()));
//		}
//
//		if(CollectionUtils.isNotEmpty(entityQuery.getEntityMetadataFilters())) {
//
//			for(final EntityMetadataFilter entityMetadataFilter : entityQuery.getEntityMetadataFilters()) {
//
//				select.where(containsKey("metadata", entityMetadataFilter.getName()))
//					.and(contains("metadata", entityMetadataFilter.getValue()));
//
//			}
//
//		}
//
//		/*
//		 * TODO: Ordering of Cassandra query results.
//		 * The entity store query() functions are not called externally. All querying is done against
//		 * Elasticsearch instead. However, it would be nice if all entity stores behaved the same.
//		 * So ordering of Cassandra query results needs implemented at some point.
//		 */
//
//		// The fetchsize must be sufficient to support this query.
//		final int fetchSize = entityQuery.getOffset() + entityQuery.getLimit();
//		select.setFetchSize(fetchSize);
//
//		// Some queries are not optimal. We won't prevent them but they are discouraged.
//		// See the table schema to understand the bad queries.
//		select.allowFiltering();

		// TODO: Build this query using the WHERE conditions commented out above.
		final SimpleStatement statement = SimpleStatement.builder("SELECT * FROM " + TABLE_NAME)
				.setKeyspace(keySpace)
				.build();

		final ResultSet resultSet = session.execute(statement);

		List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();

		final Iterator<Row> iterator = resultSet.iterator();
		
		while(iterator.hasNext()) {

			final Row row = iterator.next();

			final CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			cassandraStoredEntities.add(cassandraStoredEntity);
			
		}

		final String queryId = UUID.randomUUID().toString();
		
		// Determine the offset and limit.
		if(cassandraStoredEntities.size() < entityQuery.getOffset()) {
			
			// The offset is larger than the returned results. Return no results.
			cassandraStoredEntities.clear();
			
		} else {
			
			if(entityQuery.getOffset() + entityQuery.getLimit() > cassandraStoredEntities.size()) {		
				
				// With the given offset the limit is too large. Return from the offset up to the end.
				cassandraStoredEntities = cassandraStoredEntities.subList(entityQuery.getOffset(), cassandraStoredEntities.size());
				
			} else {
				
				// Can return what was asked for.
				cassandraStoredEntities = cassandraStoredEntities.subList(entityQuery.getOffset(), entityQuery.getOffset() + entityQuery.getLimit());
				
			}
			
		}

		final List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		for(final CassandraStoredEntity cassandraStoredEntity : cassandraStoredEntities) {
			
			try {
				
				indexedEntities.add(cassandraStoredEntity.toIndexedEntity());
				
			} catch (MalformedAclException ex) {
				
				LOGGER.error("The ACL for entity " + cassandraStoredEntity.getId() + " is malformed.", ex);
				
				throw new EntityStoreException("The ACL for entity " + cassandraStoredEntity.getId() + " is malformed.");
				
			}
			
		}
		
		return new QueryResult(indexedEntities, queryId);
		
	}


	@Override
	public CassandraStoredEntity getEntityById(String entityId) {

		final SimpleStatement statement = SimpleStatement.builder("SELECT * FROM " + TABLE_NAME + " WHERE id = ?")
				.addPositionalValues(entityId)
				.setKeyspace(keySpace)
				.build();

		final ResultSet resultSet = session.execute(statement);

		final List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();

		final Iterator<Row> iterator = resultSet.iterator();
	
		while(iterator.hasNext()) {
			
			Row row = iterator.next();

			final CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			cassandraStoredEntities.add(cassandraStoredEntity);
			
		}
		
		if(CollectionUtils.isNotEmpty(cassandraStoredEntities)) {
		
			return cassandraStoredEntities.get(0);
			
		} else {
			
			return null;
			
		}
		
	}


	@Override
	public long getEntityCount() throws EntityStoreException {
		
		try {
		
			long count = 0;

			// TODO: Update this for the new version.
//			final PreparedStatement select = session.prepare(select().countAll().from(keySpace, TABLE_NAME));
//			final Row row = session.execute(select.bind()).one();
//
//			if(row != null) {
//
//				count = row.getLong(0);
//
//			}
//
			return count;
			
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to get entity count from Cassandra.", ex);
			
		}
		
	}


	@Override
	public long getEntityCount(String context) throws EntityStoreException {		
		
		try {
		
			long count = 0;

			// TODO: Update this for the new version.
//			final PreparedStatement statement = session.prepare(select().countAll().from(keySpace, TABLE_NAME).where(eq("context", bindMarker())));
//			final Row row = session.execute(statement.bind(context)).one();
//
//			if(row != null) {
//
//				count = row.getLong(0);
//
//			}
			
			return count;
			
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to get entity count for context from Cassandra.", ex);
			
		}
				
	}


	@Override
	public List<String> getContexts() throws EntityStoreException {
		
		// Cannot do a distinct select on a column.
		// Can only do a distinct select on a partition key (primary key).
		
		return null; 
		
	}


	@Override
	public void deleteContext(String context) throws EntityStoreException {

		try {

			final SimpleStatement statement = SimpleStatement.builder("DELETE FROM " + TABLE_NAME + " WHERE context = ?")
					.addPositionalValues(context)
					.setKeyspace(keySpace)
					.build();

			session.execute(statement);
		
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to delete context from Cassandra.", ex);
			
		}
		
	}


	@Override
	public void deleteDocument(String documentId) throws EntityStoreException {
		
		try {

			final SimpleStatement statement = SimpleStatement.builder("DELETE FROM " + TABLE_NAME + " WHERE documentid = ?")
					.addPositionalValues(documentId)
					.setKeyspace(keySpace)
					.build();

			session.execute(statement);
			
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to delete document from Cassandra.", ex);
			
		}
		
	}

	/**
	 * {@inheritDoc}
	 * A new {@link CassandraEntityStore} must be created after calling <code>close</code>.
	 */
	@Override
	public void close() throws EntityStoreException {

		session.close();
		
	}
	
	private CassandraStoredEntity rowToEntity(Row row) {

		final CassandraStoredEntity cassandraStoredEntity = new CassandraStoredEntity();
		cassandraStoredEntity.setId(row.getString("id"));
		cassandraStoredEntity.setText(row.getString("text"));
		cassandraStoredEntity.setType(row.getString("type"));
		cassandraStoredEntity.setConfidence(row.getDouble("confidence"));
		cassandraStoredEntity.setContext(row.getString("context"));
		cassandraStoredEntity.setDocumentId(row.getString("documentid"));
		cassandraStoredEntity.setExtractionDate(row.getLong("extractiondate"));
		cassandraStoredEntity.setLanguage(row.getString("language"));
		cassandraStoredEntity.setMetadata(row.getMap("metadata", String.class, String.class));
		cassandraStoredEntity.setAcl(row.getString("acl"));
		cassandraStoredEntity.setVisible(row.getInt("visible"));
		cassandraStoredEntity.setIndexed(row.getLong("indexed"));
		
		return cassandraStoredEntity;
	}

}