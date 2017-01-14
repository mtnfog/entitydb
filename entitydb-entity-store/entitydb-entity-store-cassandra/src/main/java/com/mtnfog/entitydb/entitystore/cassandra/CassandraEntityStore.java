/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
package com.mtnfog.entitydb.entitystore.cassandra;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.contains;
import static com.datastax.driver.core.querybuilder.QueryBuilder.containsKey;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.entitystore.cassandra.model.CassandraStoredEntity;
import com.mtnfog.entitydb.eql.model.EntityEnrichmentFilter;
import com.mtnfog.entitydb.eql.model.EntityQuery;
import com.mtnfog.entitydb.model.entitystore.EntityIdGenerator;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.search.IndexedEntity;

/**
 * Implementation of {@link EntityStore} that uses a Cassandra database.
 * Query support is provided through the {@link EntityQuery} class.
 * More complex queries not supported by the {@link EntityQuery} can be
 * achieved through external SQL queries.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class CassandraEntityStore implements EntityStore<CassandraStoredEntity> {

	private static final Logger LOGGER = LogManager.getLogger(CassandraEntityStore.class);
	
	public static final String TABLE_NAME = "entities";
	private static final int DEFAULT_PAGE_SIZE = 25;
		
	private String host;
	private String keySpace;	
	private Session session;		
	
	/**
	 * Creates a Cassandra entity store. 
	 * @param host The host name or IP address of the Cassandra host.
	 * @param port The Cassandra port.
	 * @param keySpace The Cassandra keyspace.
	 */
	public CassandraEntityStore(String host, int port, String keySpace) {
		
		this.host = host;
		this.keySpace = keySpace;
		
		Cluster cluster = Cluster.builder()
				.addContactPoint(host)
				.withPort(port)
				.withQueryOptions(new QueryOptions().setFetchSize(DEFAULT_PAGE_SIZE))
				.build();		
		
		this.session = cluster.connect(keySpace);
	      
	}
	
	/**
	 * Creates a Cassandra entity store.
	 * @param session The pre-existing Cassandra session.
	 * @param keySpace The Cassandra keyspace.
	 */
	public CassandraEntityStore(Session session, String keySpace) {
		
		this.host = session.getCluster().getClusterName();
		this.keySpace = keySpace;
		this.session = session;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatus() {
	
		return String.format("Cassandra host: %s, keyspace: %s, table: %s", host, keySpace, TABLE_NAME);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CassandraStoredEntity> getNonIndexedEntities(int limit) {

		// We can NOT allow filtering here because this query WILL be run.
		// The "indexed" column is indexed but we can only use one index.
		// Because of being restricted to a single index, the "visible"
		// column is checked on each returned entity and not in the query.
		// That's why the "visible" condition is commented out in the below query.
		
		Select select = QueryBuilder.select()
		        .all()
		        .from(keySpace, TABLE_NAME)
		        .where(eq("indexed", Long.valueOf(0)))
		        //.and(eq("visible", Long.valueOf(1)))
		        .limit(limit);
		
		ResultSet resultSet = session.execute(select);
		
		List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();
		
		Iterator<Row> iterator = resultSet.iterator();				
		
		while(iterator.hasNext()) {
			
			Row row = iterator.next();
			
			CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			// Only "visible" entities need indexed.
			if(cassandraStoredEntity.getVisible() == 1) {
			
				cassandraStoredEntities.add(cassandraStoredEntity);
			
			}
			
		}
		
		return cassandraStoredEntities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean markEntityAsIndexed(String entityId) {
		
		boolean result = false;
		
		CassandraStoredEntity entity = getEntityById(entityId);

		if(entity != null) {
			
			Statement statement = QueryBuilder
				.update(keySpace, TABLE_NAME)
				.with(set("indexed", System.currentTimeMillis()))
				.where(eq("id", entityId));		
			
			try {
			
				ResultSet resultSet = session.execute(statement);
				
				// wasApplied() is not the right function here because
				// this is not a conditional update but it does indicate
				// that execution completed.
				result = resultSet.wasApplied();
		
			} catch (Exception ex) {
				
				LOGGER.error("Unable to mark entity " + entityId + " as indexed.", ex);
				
				result = false;
				
			}
			
		}
		
		return result;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long markEntitiesAsIndexed(Collection<String> entityIds) {
		
		int indexed = 0;
		
		for(String entityId : entityIds) {
			
			boolean marked = markEntityAsIndexed(entityId);
			
			if(marked) {
				indexed++;
			}
			
		}
		
		return indexed;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String storeEntity(Entity entity, String acl) throws EntityStoreException {

		final String entityId = EntityIdGenerator.generateEntityId(entity, acl);
		
		try {
		
			final Date timestamp = new Date();			
		
			// Executing in a batch doesn't help performance - it provides atomicity for the insert.
			final BatchStatement batchStatement = new BatchStatement(BatchStatement.Type.LOGGED);
			
			PreparedStatement entityInsert = session.prepare(insertInto(keySpace, TABLE_NAME)
					.value("id", bindMarker())
					.value("text", bindMarker())
					.value("confidence", bindMarker())
					.value("type", bindMarker())
					.value("context", bindMarker())
					.value("documentid", bindMarker())
					.value("uri", bindMarker())
					.value("language", bindMarker())
					.value("extractiondate", bindMarker())
					.value("acl", bindMarker())
					.value("enrichments", bindMarker())
					.value("timestamp", bindMarker())
					.value("visible", bindMarker())
					.value("indexed", bindMarker()));
			
			BoundStatement boundEntityInsert = entityInsert.bind(
					entityId, 
					entity.getText(),
					entity.getConfidence(), 
					entity.getType(), 
					entity.getContext(), 
					entity.getDocumentId(), 
					entity.getUri(), 
					entity.getLanguageCode(),
					timestamp.getTime(), 
					acl,
					entity.getMetadata(),
					System.currentTimeMillis(),
					1,
					Long.valueOf(0));
			
			batchStatement.add(boundEntityInsert);
						
			session.execute(batchStatement);
		
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to store entity.", ex);
			
		}
		
		return entityId;
				
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException {
		
		String newEntityId = StringUtils.EMPTY;
				
		CassandraStoredEntity entity = getEntityById(entityId);
		
		if(entity != null) {
		
			// Create the cloned entity and save it.
			CassandraStoredEntity cloned = new CassandraStoredEntity();
						
			cloned.setAcl(acl.toString());
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
			
			// Executing in a batch doesn't help performance - it provides atomicity for the insert.
			final BatchStatement batchStatement = new BatchStatement(BatchStatement.Type.LOGGED);
			
			PreparedStatement entityInsert = session.prepare(insertInto(keySpace, TABLE_NAME)
					.value("id", bindMarker())
					.value("text", bindMarker())
					.value("confidence", bindMarker())
					.value("type", bindMarker())
					.value("context", bindMarker())
					.value("documentid", bindMarker())
					.value("uri", bindMarker())
					.value("language", bindMarker())
					.value("extractiondate", bindMarker())
					.value("acl", bindMarker())
					.value("visible", bindMarker())
					.value("timestamp", bindMarker())
					.value("indexed", bindMarker())
					.value("enrichments", bindMarker()));		
			
			BoundStatement boundEntityInsert = entityInsert.bind(
					newEntityId, 
					cloned.getText(),
					cloned.getConfidence(), 
					cloned.getType(), 
					cloned.getContext(), 
					cloned.getDocumentId(), 
					cloned.getUri(), 
					cloned.getLanguage(),
					cloned.getExtractionDate(), 
					acl,
					cloned.getVisible(),
					System.currentTimeMillis(),
					cloned.getIndexed(),
					cloned.getMetadata());
						
			Statement updateEntityStatement = 
					QueryBuilder
						.update(keySpace, TABLE_NAME)
						.with(set("visible", 0))
						.where(eq("id", entityId));
			
			batchStatement.add(updateEntityStatement);
			batchStatement.add(boundEntityInsert);
						
			try {
				
				session.execute(batchStatement);
				
			} catch (Exception ex) {
				
				LOGGER.error("Entity " + entityId + " could not be set as not visible.", ex);
				
			}
        
		} else {
			
			throw new NonexistantEntityException("The entity does not exist.");
			
		}
        		
		return newEntityId;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) throws EntityStoreException {
		
		Map<Entity, String> storedEntities = new HashMap<Entity, String>();
		
		for(Entity entity : entities) {						
			
			String entityId = storeEntity(entity, acl);
			
			storedEntities.put(entity, entityId);
			
		}
		
		return storedEntities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEntity(String entityId) {
		
		Statement statement = QueryBuilder.delete()
		        .from(keySpace, TABLE_NAME)
		        .where(eq("id", entityId));
		
		session.execute(statement);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CassandraStoredEntity> getEntitiesByIds(List<String> entityIds, boolean maskAcl) {
		
		List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();
		
		for(String entityId : entityIds) {
			
			CassandraStoredEntity entity = getEntityById(entityId);
			
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
	
		Select select = QueryBuilder.select().from(keySpace, TABLE_NAME);
				
		if(StringUtils.isNotEmpty(entityQuery.getText())) {
			
			select.where(eq("text", entityQuery.getText()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getType())) {
			
			select.where(eq("type", entityQuery.getType()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getLanguageCode())) {
			
			select.where(eq("language", entityQuery.getLanguageCode()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getUri())) {
			
			select.where(eq("uri", entityQuery.getUri()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getContext())) {
			
			select.where(eq("context", entityQuery.getContext()));
			
		}
		
		if(StringUtils.isNotEmpty(entityQuery.getDocumentId())) {
			
			select.where(eq("documentid", entityQuery.getDocumentId()));
			
		}
		
		if(entityQuery.getConfidenceRange() != null) {
			
			select.where(gte("confidence", entityQuery.getConfidenceRange().getMinimum()));
			select.where(lte("confidence", entityQuery.getConfidenceRange().getMaximum()));
			
		}
		
		if(CollectionUtils.isNotEmpty(entityQuery.getEntityEnrichmentFilters())) {
			
			for(EntityEnrichmentFilter entityEnrichmentFilter : entityQuery.getEntityEnrichmentFilters()) {
			
				select.where(containsKey("enrichments", entityEnrichmentFilter.getName()))
					.and(contains("enrichments", entityEnrichmentFilter.getValue()));				
					
			}
			
		}
		
		/*
		 * TODO: Ordering of Cassandra query results.
		 * The entity store query() functions are not called externally. All querying is done against
		 * Elasticsearch instead. However, it would be nice if all entity stores behaved the same.
		 * So ordering of Cassandra query results needs implemented at some point.
		 */
					
		// The fetchsize must be sufficient to support this query.
		int fetchSize = entityQuery.getOffset() + entityQuery.getLimit();
		select.setFetchSize(fetchSize);
		
		// Some queries are not optimal. We won't prevent them but they are discouraged.
		// See the table schema to understand the bad queries.
		select.allowFiltering();
		
		ResultSet resultSet = session.execute(select);
					
		List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();
		
		Iterator<Row> iterator = resultSet.iterator();				
		
		while(iterator.hasNext()) {
			
			Row row = iterator.next();
			
			CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			cassandraStoredEntities.add(cassandraStoredEntity);
			
		}
		
		String queryId = UUID.randomUUID().toString();
		
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
		
		List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		for(CassandraStoredEntity cassandraStoredEntity : cassandraStoredEntities) {
			
			try {
				
				indexedEntities.add(cassandraStoredEntity.toIndexedEntity());
				
			} catch (MalformedAclException ex) {
				
				LOGGER.error("The ACL for entity " + cassandraStoredEntity.getId() + " is malformed.", ex);
				
				throw new EntityStoreException("The ACL for entity " + cassandraStoredEntity.getId() + " is malformed.");
				
			}
			
		}
		
		return new QueryResult(indexedEntities, queryId);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CassandraStoredEntity getEntityById(String id) {
	
		Select select = QueryBuilder.select().from(keySpace, TABLE_NAME);
		
		select.where(eq("id", id));
		
		ResultSet resultSet = session.execute(select);
		
		List<CassandraStoredEntity> cassandraStoredEntities = new LinkedList<CassandraStoredEntity>();
		
		Iterator<Row> iterator = resultSet.iterator();	
	
		while(iterator.hasNext()) {
			
			Row row = iterator.next();
			
			CassandraStoredEntity cassandraStoredEntity = rowToEntity(row);
			
			cassandraStoredEntities.add(cassandraStoredEntity);
			
		}
		
		if(CollectionUtils.isNotEmpty(cassandraStoredEntities)) {
		
			return cassandraStoredEntities.get(0);
			
		} else {
			
			return null;
			
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getEntityCount() throws EntityStoreException {
		
		try {
		
			long count = 0;
			
			PreparedStatement select = session.prepare(select().countAll().from(keySpace, TABLE_NAME));
			final Row row = session.execute(select.bind()).one();
			
			if(row != null) {
				
				count = row.getLong(0);
			
			}
			
			return count;
			
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to get entity count from Cassandra.", ex);
			
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getEntityCount(String context) throws EntityStoreException {		
		
		try {
		
			long count = 0;
			
			PreparedStatement statement = session.prepare(select().countAll().from(keySpace, TABLE_NAME).where(eq("context", bindMarker())));
			Row row = session.execute(statement.bind(context)).one();   
			
			if(row != null) {
				
				count = row.getLong(0);
			
			}
			
			return count;
			
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to get entity count for context from Cassandra.", ex);
			
		}
				
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getContexts() throws EntityStoreException {
		
		// Cannot do a distinct select on a column.
		// Can only do a distinct select on a partition key (primary key).
		
		return null; 
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteContext(String context) throws EntityStoreException {

		try {
		
			Statement statement = QueryBuilder.delete()
			        .from(keySpace, TABLE_NAME)
			        .where(eq("context", context));
			
			session.execute(statement);
		
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to delete context from Cassandra.", ex);
			
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDocument(String documentId) throws EntityStoreException {
		
		try {
			
			Statement statement = QueryBuilder.delete()
			        .from(keySpace, TABLE_NAME)
			        .where(eq("documentid", documentId));
			
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
		
		CassandraStoredEntity cassandraStoredEntity = new CassandraStoredEntity();
		cassandraStoredEntity.setId(row.getString("id"));
		cassandraStoredEntity.setText(row.getString("text"));
		cassandraStoredEntity.setType(row.getString("type"));
		cassandraStoredEntity.setConfidence(row.getDouble("confidence"));
		cassandraStoredEntity.setContext(row.getString("context"));
		cassandraStoredEntity.setDocumentId(row.getString("documentid"));
		cassandraStoredEntity.setExtractionDate(row.getLong("extractiondate"));
		cassandraStoredEntity.setLanguage(row.getString("language"));
		cassandraStoredEntity.setMetadata(row.getMap("enrichments", String.class, String.class));
		cassandraStoredEntity.setAcl(row.getString("acl"));
		cassandraStoredEntity.setVisible(row.getInt("visible"));
		cassandraStoredEntity.setIndexed(row.getLong("indexed"));
		
		return cassandraStoredEntity;
	}

}