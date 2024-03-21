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
package ai.philterd.entitydb.entitystore.rdbms;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.entitystore.rdbms.model.RdbmsStoredEntity;
import ai.philterd.entitydb.entitystore.rdbms.model.RdbmsStoredEntityMetadata;
import ai.philterd.entitydb.entitystore.rdbms.util.HibernateUtil;
import com.mtnfog.entitydb.eql.model.EntityMetadataFilter;
import com.mtnfog.entitydb.eql.model.EntityQuery;
import ai.philterd.entitydb.model.SystemProperties;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.search.IndexedEntity;

/**
 * Implementation of {@link EntityStore} that uses a JDBC-compliant
 * relational database management system. Query support is provided
 * through the {@link EntityQuery} class. More complex queries not
 * supported by the {@link EntityQuery} can be achieved through
 * external SQL queries.
 * 
 * This entity store supports case-sensitive querying of entity
 * metadata.
 * 
 * @author Philterd, LLC
 *
 */
public class RdbmsEntityStore implements EntityStore<RdbmsStoredEntity> {
	
	private static final Logger LOGGER = LogManager.getLogger(RdbmsEntityStore.class);

	private SessionFactory sessionFactory;
	private String jdbcUrl;
		
	/**
	 * Creates a connection to a SQL Server entity store.
	 * The JDBC driver jar must be on the classpath.
	 * @param jdbcUrl The JDBC URL of the entity store.
	 * @param userName The username for the connection.
	 * @param password The password for the connection.
	 * @param schemaExport Hibernate schema export (create, create-drop, etc.)
	 * @return A {@link RdbmsEntityStore store}.
	 */
	public static RdbmsEntityStore createSqlServerEntityStore(String jdbcUrl, String userName, String password, String schemaExport) {
		
		final String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		final String dialect = "org.hibernate.dialect.SQLServerDialect";
		
		return new RdbmsEntityStore(jdbcUrl, jdbcDriver, userName, password, dialect, schemaExport);
		
	}
	
	/**
	 * Creates a connection to an Oracle 10g/11g entity store.
	 * The JDBC driver jar must be on the classpath.
	 * @param jdbcUrl The JDBC URL of the entity store.
	 * @param userName The username for the connection.
	 * @param password The password for the connection.
	 * @param schemaExport Hibernate schema export (create, create-drop, etc.)
	 * @return A {@link RdbmsEntityStore store}.
	 */
	public static RdbmsEntityStore createOracleEntityStore(String jdbcUrl, String userName, String password, String schemaExport) {
		
		final String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		
		// 10g and 11g use the same 10g dialect.
		final String dialect = "org.hibernate.dialect.Oracle10g.Dialect";
		
		return new RdbmsEntityStore(jdbcUrl, jdbcDriver, userName, password, dialect, schemaExport);
		
	}
	
	/**
	 * Creates a connection to a Hypersonic entity store.
	 * The JDBC driver jar must be on the classpath.
	 * @param jdbcUrl The JDBC URL of the entity store.
	 * @param userName The username for the connection.
	 * @param password The password for the connection.
	 * @param schemaExport Hibernate schema export (create, create-drop, etc.)
	 * @return A {@link RdbmsEntityStore store}.
	 */
	public static RdbmsEntityStore createHypersonicEntityStore(String jdbcUrl, String userName, String password, String schemaExport) {
		
		final String jdbcDriver = "org.hsqldb.jdbcDriver";
		final String dialect = "org.hibernate.dialect.HSQLDialect";
		
		return new RdbmsEntityStore(jdbcUrl, jdbcDriver, userName, password, dialect, schemaExport);
		
	}
	
	/**
	 * Creates a connection to a MySQL entity store.
	 * The JDBC driver jar must be on the classpath.
	 * @param jdbcUrl The JDBC URL of the entity store.
	 * @param userName The username for the connection.
	 * @param password The password for the connection.
	 * @param schemaExport Hibernate schema export (create, create-drop, etc.)
	 * @return A {@link RdbmsEntityStore store}.
	 */
	public static RdbmsEntityStore createMySQL5EntityStore(String jdbcUrl, String userName, String password, String schemaExport) {
		
		final String jdbcDriver = "com.mysql.jdbc.Driver";
		final String dialect = "org.hibernate.dialect.MySQL5Dialect";
		
		return new RdbmsEntityStore(jdbcUrl, jdbcDriver, userName, password, dialect, schemaExport);
		
	}
	
	/**
	 * Creates a new entity store backed by a RDBMs.
	 * @param enabled If the session store is enabled.
	 * @param jdbcUrl The JDBC connection string for the database.
	 * @param jdbcDriver The JDBC driver name.
	 * @param userName The database user name.
	 * @param password The database password.
	 * @param dialect The Hibernate dialect to use. Refer to the Hibernate documentation for the
	 * appropriate dialect for your database.
	 * @param schemaExport The schema export Hibernate method (create, update, etc.).
	 */
	public RdbmsEntityStore(String jdbcUrl, String jdbcDriver, String userName, String password, String dialect, String schemaExport) {
		
		this.jdbcUrl = jdbcUrl;

		sessionFactory = HibernateUtil.getSessionFactory(jdbcUrl, jdbcDriver, userName, password, dialect, schemaExport);	

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatus() {
		
		return String.format("RDBMS JDBC URL: %s", jdbcUrl);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RdbmsStoredEntity> getNonIndexedEntities(int limit) {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(RdbmsStoredEntity.class, "a");		
		criteria.add(Restrictions.eq("indexed", Long.valueOf(0)));
		criteria.add(Restrictions.eq("visible", 1));
		criteria.setMaxResults(limit);
		
		List<RdbmsStoredEntity> entities = criteria.list();
		
		session.close();
		
		return entities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long markEntitiesAsIndexed(Collection<String> entityIds) {
		
		int updated = 0;
		
		if(CollectionUtils.isNotEmpty(entityIds)) {
			
			Session session = sessionFactory.openSession();
			
			String sql = "UPDATE Entities SET indexed = :indexed WHERE id in (:entityIds)";
			
			Query query = session.createSQLQuery(sql)
					.setParameter("indexed", System.currentTimeMillis())
					.setParameterList("entityIds", entityIds);
			
			updated = query.executeUpdate();
			
			session.close();				
		
		}
		
		return updated;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean markEntityAsIndexed(String entityId) {
		
		Session session = sessionFactory.openSession();
		
		String sql = "UPDATE Entities SET indexed = :indexed WHERE id = :entityId";
		
		Query query = session.createSQLQuery(sql)
				.setParameter("indexed", System.currentTimeMillis())
				.setParameter("entityId", entityId);
		
		int updated = query.executeUpdate();
		
		session.close();
		
		if(updated == 1) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Wildcard characters are allowed in the entiy text. Use an asterisk (*) to match
	 * zero or more characters and use a question mark (?) to match a single character.
	 * 
	 * Queries on the entity text are case-insensitive.
	 */
	@Override
	public QueryResult query(EntityQuery entityQuery) throws EntityStoreException {
		
		Session session = sessionFactory.openSession();
		
		LOGGER.debug("Executing entity query: {}", entityQuery.toString());
		
		// Convert entityQuery to a Hibernate criteria.
		Criteria criteria = session.createCriteria(RdbmsStoredEntity.class, "a");
		
		if(entityQuery.getConfidenceRange() != null) {
			
			criteria.add(Restrictions.ge("confidence", entityQuery.getConfidenceRange().getMinimum()));
			criteria.add(Restrictions.le("confidence", entityQuery.getConfidenceRange().getMaximum()));
			
		}
		
		if(entityQuery.getLimit() > 0) {
			
			criteria.setMaxResults(entityQuery.getLimit());
			
		}
		
		if(entityQuery.getOffset() > 0) {
			
			criteria.setFirstResult(entityQuery.getOffset());
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getText())) {
			
			// Replace wildcard characters with SQL wildcards.
			String text = replaceWildCards(entityQuery.getText());
			criteria.add(Restrictions.ilike("text", text));							
				
		}
		
		if(entityQuery.getType() != null) {

			criteria.add(Restrictions.eq("type", entityQuery.getType()));
			
		}
		
		if(entityQuery.getLanguageCode() != null) {

			criteria.add(Restrictions.eq("language", entityQuery.getLanguageCode()));
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getUri())) {
			
			criteria.add(Restrictions.eq("uri", entityQuery.getUri()));
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getContext())) {
			
			criteria.add(Restrictions.eq("context", entityQuery.getContext()));
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getDocumentId())) {
			
			criteria.add(Restrictions.eq("documentId", entityQuery.getDocumentId()));
			
		}
		
		if(!CollectionUtils.isEmpty(entityQuery.getEntityMetadataFilters())) {
			
			criteria.createAlias("a.metadata", "b");
			
			for(EntityMetadataFilter attribute : entityQuery.getEntityMetadataFilters()) {
				
				String name = replaceWildCards(attribute.getName());
				String value = replaceWildCards(attribute.getValue());
				
				if(attribute.isCaseSensitive() == true) {
				
					criteria.add(Restrictions.ilike("b.name", name));
					criteria.add(Restrictions.ilike("b.value", value));
				
				} else {
					
					criteria.add(Restrictions.like("b.name", name));
					criteria.add(Restrictions.like("b.value", value));
					
				}
				
			}

		}
		
		criteria.addOrder(Order.asc(entityQuery.getEntityOrder().getProperty()));
		
		@SuppressWarnings("unchecked")
		List<RdbmsStoredEntity> storedEntities = criteria.list();
		
		session.close();
		
		LOGGER.debug("Query returned {} entities.", storedEntities.size());
		
		String queryId = UUID.randomUUID().toString();
		
		List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		for(RdbmsStoredEntity entity : storedEntities) {
			
			try {
								
				indexedEntities.add(entity.toIndexedEntity());
				
			} catch (MalformedAclException ex) {
				
				LOGGER.error("The ACL for entity " + entity.getId() + " is malformed.", ex);
				
				throw new EntityStoreException("The ACL for entity " + entity.getId() + " is malformed.");
				
			}
			
		}
		
		QueryResult queryResult = new QueryResult(indexedEntities, queryId);
		
		return queryResult;

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEntity(String entityId) {
		
		Session session = sessionFactory.openSession();
		
		String sql = "DELETE FROM Entities WHERE id = :entityId";
		
		Query query = session.createSQLQuery(sql)
				.setParameter("entityId", entityId);
		
		query.executeUpdate();
		
		session.close();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RdbmsStoredEntity> getEntitiesByIds(List<String> entityIds, boolean maskAcl) {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(RdbmsStoredEntity.class);
		
		List<RdbmsStoredEntity> entities = null;
		
		if(CollectionUtils.isNotEmpty(entityIds)) {
			
			criteria.add(Restrictions.in("id", entityIds));				
			
			entities = criteria.list();
			
			if(maskAcl) {
				
				for(RdbmsStoredEntity entity : entities) {					
					entity.setAcl(StringUtils.EMPTY);					
				}
				
			}
			
		} else {
			
			entities = Collections.emptyList();
			
		}
		
		session.close();
		
		return entities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException {
		
		String newEntityId = StringUtils.EMPTY;				
		
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();	
		
		RdbmsStoredEntity entity = getEntityById(entityId);
						
		if(entity != null) {
		
			// Set the original entity as not visible and update it.
			entity.setVisible(0);
			session.saveOrUpdate(entity);			
			
			// Create the cloned entity and save it.
			RdbmsStoredEntity cloned = new RdbmsStoredEntity();
						
			cloned.setAcl(acl);
			cloned.setTimestamp(System.currentTimeMillis());
			cloned.setConfidence(entity.getConfidence());
			cloned.setContext(entity.getContext());
			cloned.setDocumentId(entity.getDocumentId());
			cloned.setMetadata(new HashSet<RdbmsStoredEntityMetadata>(entity.getMetadata()));
			cloned.setExtractionDate(entity.getExtractionDate());
			cloned.setLanguage(entity.getLanguage());
			cloned.setText(entity.getText());
			cloned.setType(entity.getType());
			cloned.setUri(entity.getUri());
			cloned.setVisible(1);
			cloned.setIndexed(Long.valueOf(0));
			
			// Make the ID for the new entity and set it.
			newEntityId = EntityIdGenerator.generateEntityId(cloned.getText(), cloned.getConfidence(), cloned.getLanguage(), cloned.getContext(), cloned.getDocumentId(), acl);
			cloned.setId(newEntityId);
					
	        session.saveOrUpdate(cloned);
	        
	        session.getTransaction().commit();	    
        
		} else {
			
			session.close();
			
			throw new NonexistantEntityException("The entity does not exist.");
			
		}
		
		session.close();
        		
		return newEntityId;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RdbmsStoredEntity getEntityById(String id) {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(RdbmsStoredEntity.class);
		criteria.add(Restrictions.eq("id", id));		
		
		RdbmsStoredEntity entity = (RdbmsStoredEntity) criteria.uniqueResult();
		
		session.close();
		
		return entity;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getEntityCount() {
		
		Session session = sessionFactory.openSession();
		
		String sql = "SELECT COUNT(*) FROM Entities";

		Query query = session.createSQLQuery(sql);
		
		BigInteger count = (BigInteger) query.uniqueResult();
		
		session.close();
		
		return count.longValue();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getEntityCount( String context) {
		
		Session session = sessionFactory.openSession();

		String sql = "SELECT COUNT(*) FROM Entities WHERE context = :context";
		
		Query query = session.createSQLQuery(sql).setParameter("context", context);
		
		BigInteger count = (BigInteger) query.uniqueResult();
		
		session.close();
		
		return count.longValue();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getContexts() {
		
		Session session = sessionFactory.openSession();
		
		String sql = "SELECT DISTINCT(context) FROM Entities";

		Query query = session.createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List<String> contexts = query.list();
		
		session.close();
		
		return contexts;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String storeEntity(Entity entity, String acl) throws EntityStoreException {
		
		Session session = sessionFactory.openSession();
		
		String entityId = null;
		
		try {
		
			RdbmsStoredEntity storedEntity = RdbmsStoredEntity.fromEntity(entity, acl);					
			
			session.beginTransaction();	
			
	        session.save(storedEntity);
	        
	        session.getTransaction().commit();
	        
	        entityId = storedEntity.getId();
	        
		} catch (Exception ex) {

			session.close();
			
			throw new EntityStoreException("Unable to store entity.", ex);
			
		}
		
		session.close();
		
		return entityId;
        
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) {
		
		Map<Entity, String> storedEntities = new HashMap<Entity, String>();
		
		int batchSize = 20;
		int i = 1;
		
		if(System.getProperty(SystemProperties.RDBMS_ENTITY_STORE_BATCH_SIZE) != null) {
			batchSize = Integer.valueOf(System.getProperty(SystemProperties.RDBMS_ENTITY_STORE_BATCH_SIZE));
			LOGGER.debug("Using custom RDBMS batch size of {}.", batchSize);
		}
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();	
		
		for(Entity entity : entities) {			
		
			RdbmsStoredEntity storedEntity = RdbmsStoredEntity.fromEntity(entity, acl);
			session.save(storedEntity);
			String entityId = storedEntity.getId();
		
			if(entityId != null) {					
				storedEntities.put(entity, entityId);					
			}
			
			i++;
			
			if (i % batchSize == 0) {
		        session.flush();
		        session.clear();
		        i = 1;
		    }
				        
		}
		
		if(i < batchSize) {			
			session.flush();
	        session.clear();	        
		}
		
		session.close();
		
		return storedEntities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteContext(String context) {

		Session session = sessionFactory.openSession();
		
		String sql = "DELETE FROM Entities WHERE context = :context";
				
		Query query = session.createSQLQuery(sql).setParameter("context", context);
		
		query.executeUpdate();
		
		session.close();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDocument(String documentId) {
		
		Session session = sessionFactory.openSession();
		
		String sql = "DELETE FROM Entities WHERE documentId = :documentId";
				
		Query query = session.createSQLQuery(sql).setParameter("documentId", documentId);
		
		query.executeUpdate();
		
		session.close();
		
	}

	/**
	 * Closes the underlying Hibernate session. This {@link RdbmsEntityStore} class will
	 * no longer be usable after this function is called.
	 */
	@Override
	public void close() {
		
		// Nothing to close.
		
	}
	
	private String replaceWildCards(String term) {
		
		return term.replaceAll("\\*", "%").replaceAll("\\?", "_");
		
	}
	
}