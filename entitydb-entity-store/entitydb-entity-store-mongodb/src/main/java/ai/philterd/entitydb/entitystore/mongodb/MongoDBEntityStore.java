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
package ai.philterd.entitydb.entitystore.mongodb;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mtnfog.entity.Entity;
import ai.philterd.entitydb.entitystore.mongodb.model.MongoDBStoredEntity;
import ai.philterd.entitydb.entitystore.mongodb.model.MongoDBStoredEntityCodec;
import com.mtnfog.entitydb.eql.model.EntityMetadataFilter;
import com.mtnfog.entitydb.eql.model.EntityOrder;
import com.mtnfog.entitydb.eql.model.EntityQuery;
import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.search.IndexedEntity;

/**
 * Implementation of {@link EntityStore} that utilizes
 * a MongoDB to store {@link Entity entities}.
 * 
 * @author Philterd, LLC
 *
 */
public class MongoDBEntityStore implements EntityStore<MongoDBStoredEntity> {

	private static final Logger LOGGER = LogManager.getLogger(MongoDBEntityStore.class);
	
	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;
	private MongoCollection<MongoDBStoredEntity> mongoCollection;
	
	/**
	 * Creates a new {@link MongoDBEntityStore}.
	 * @param mongoClient A {@link MongoClient}.
	 * @param database The name of the database.
	 * @param collection The name of the collection.
	 * @throws EntityStoreException Thrown if the connection cannot be made.
	 */
	public MongoDBEntityStore(MongoClient mongoClient, String database, String collection) throws EntityStoreException {
		
		this.mongoClient = mongoClient;
		mongoDatabase = mongoClient.getDatabase(database).withCodecRegistry(getCodecRegistry());
		mongoCollection = getMongoCollection(collection);

	}
	
	/**
	 * Creates a new {@link MongoDBEntityStore}.
	 * @param host The host name or IP address of the MongoDB server.
	 * @param port The MongoDB port.
	 * @param database The name of the database.
	 * @param collection The name of the collection.
	 * @throws EntityStoreException Thrown if the connection cannot be made.
	 */
	public MongoDBEntityStore(String host, int port, String database, String collection) throws EntityStoreException {
		
		String mongoServer = String.format("%s:%s", host, port);
		
		mongoClient = new MongoClient(mongoServer);
		mongoDatabase = mongoClient.getDatabase(database).withCodecRegistry(getCodecRegistry());
		mongoCollection = getMongoCollection(collection);
		
	}
	
	/**
	 * Creates a new {@link MongoDBEntityStore}.
	 * @param host The host name or IP address of the MongoDB server.
	 * @param port The MongoDB port.
	 * @param username The MongoDB username.
	 * @param password The MongoDB password.
	 * @param database The name of the database.
	 * @param collection The name of the collection.
	 * @throws EntityStoreException Thrown if the connection cannot be made.
	 */
	public MongoDBEntityStore(String host, int port, String username, String password, String database, String collection) throws EntityStoreException {
		
		MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
		mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
		mongoDatabase = mongoClient.getDatabase(database).withCodecRegistry(getCodecRegistry());;
		mongoCollection = getMongoCollection(collection);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatus() {
	
		return String.format("MongoDB database: %s, Collection: %s", mongoDatabase.getName(), mongoCollection.getNamespace().getCollectionName());
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MongoDBStoredEntity> getNonIndexedEntities(int limit) {
		
		Document document = new Document();
		document.append("indexed", Long.valueOf(0));
		document.append("visible", Long.valueOf(1));
		
		FindIterable<MongoDBStoredEntity> contexts = mongoCollection
				.find(document, MongoDBStoredEntity.class)
				.limit(limit);
		
		List<MongoDBStoredEntity> entities = IteratorUtils.toList(contexts.iterator());
		
		return entities;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean markEntityAsIndexed(String entityId) {

		boolean result = false;
		
		MongoDBStoredEntity entity = getEntityById(entityId);

		if(entity != null) {
		
			entity.setIndexed(System.currentTimeMillis());
			
			UpdateResult updateResult = mongoCollection.updateOne(eq("_id", entityId), 
					new Document("$set", new Document("indexed", System.currentTimeMillis())));
			
			if(updateResult.getModifiedCount() == 1) {
				result = true;
			}
			
		}
		
		return result;
			
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long markEntitiesAsIndexed(Collection<String> entityIds) {

		long indexedTime = System.currentTimeMillis();
		
		// For some reason, passing entityId as a single string causes MongoDB
		// to throw an exception that "_id" is an illegal field. That's why
		// entityIds is converted to an array.
	
		UpdateResult updateResult = mongoCollection.updateMany(in("_id", entityIds.toArray()), 
				new Document("$set", new Document("indexed", indexedTime)),
				new UpdateOptions().upsert(false));
	
		return updateResult.getModifiedCount();
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MongoDBStoredEntity> getEntitiesByIds(List<String> entityIds, boolean maskAcl) {
		
		List<MongoDBStoredEntity> mongoDBStoredEntities = new LinkedList<MongoDBStoredEntity>();
		
		for(String entityId : entityIds) {
			
			MongoDBStoredEntity entity = getEntityById(entityId);
			
			if(entity != null) {
				
				if(maskAcl) {
					entity.setAcl(StringUtils.EMPTY);
				}
				
				mongoDBStoredEntities.add(entity);
				
			}
			
		}
		
		return mongoDBStoredEntities;
		
	}
	
	@Override
	public QueryResult query(EntityQuery entityQuery) throws EntityStoreException {
				
		Document document = new Document();
		
		if(entityQuery.getConfidenceRange() != null) {
			
			document
				.append("confidence", new Document("$gte", entityQuery.getConfidenceRange().getMinimum())
				.append("$lte", entityQuery.getConfidenceRange().getMaximum()));
			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getText())) {			
			document.append("text", entityQuery.getText());			
		}
		
		if(entityQuery.getType() != null) {
			document.append("type", entityQuery.getType());			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getUri())) {
			document.append("uri", entityQuery.getUri());			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getLanguageCode())) {
			document.append("language", entityQuery.getLanguageCode());			
		}
		
		if(!StringUtils.isEmpty(entityQuery.getContext())) {
			document.append("context", entityQuery.getContext());
		}
		
		if(!StringUtils.isEmpty(entityQuery.getDocumentId())) {
			document.append("documentId", entityQuery.getDocumentId());
		}
		
		if(!CollectionUtils.isEmpty(entityQuery.getEntityMetadataFilters())) {
			
			for(EntityMetadataFilter entityMetadataFilter : entityQuery.getEntityMetadataFilters()) {
			
				document
					.append("metadata." + entityMetadataFilter.getName(), new Document("$eq", entityMetadataFilter.getValue()));
				
			}
			
		}

		Document sortDocument = new Document();
		
		if(entityQuery.getEntityOrder().equals(EntityOrder.TEXT)) {
			
			sortDocument.append("text", 1);
			
		} else if(entityQuery.getEntityOrder().equals(EntityOrder.CONFIDENCE)) {
			
			sortDocument.append("confidence", -1);
			
		} else if(entityQuery.getEntityOrder().equals(EntityOrder.EXTRACTION_DATE)) {
			
			sortDocument.append("extractionDate", -1);		
			
		} else if(entityQuery.getEntityOrder().equals(EntityOrder.ID)) {
			
			sortDocument.append("_id", 1);
			
		}
		
		FindIterable<MongoDBStoredEntity> contexts = mongoCollection.find(document, MongoDBStoredEntity.class)
				.sort(sortDocument).limit(entityQuery.getLimit()).skip(entityQuery.getOffset());
		
		List<MongoDBStoredEntity> entities = IteratorUtils.toList(contexts.iterator());
		
		String queryId = UUID.randomUUID().toString();
		
		List<IndexedEntity> indexedEntities = new LinkedList<IndexedEntity>();
		
		for(MongoDBStoredEntity mongoDBStoredEntity : entities) {
			
			try {
				
				indexedEntities.add(mongoDBStoredEntity.toIndexedEntity());
				
			} catch (MalformedAclException ex) {
				
				LOGGER.error("The ACL for entity " + mongoDBStoredEntity.getId() + " is malformed.", ex);
				
				throw new EntityStoreException("The ACL for entity " + mongoDBStoredEntity.getId() + " is malformed.");
				
			}
			
		}
		
		QueryResult queryResult = new QueryResult(indexedEntities, queryId);
		
		return queryResult;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateAcl(String entityId, String acl) throws EntityStoreException, NonexistantEntityException {
		
		String newEntityId = StringUtils.EMPTY;
		
		MongoDBStoredEntity entity = getEntityById(entityId);
		
		if(entity != null) {
			
			// Set the original entity as not visible and update it.		
			entity.setVisible(0);
			mongoCollection.replaceOne(eq("_id", entityId), entity);
			
			// Create the cloned entity and save it.
			MongoDBStoredEntity cloned = new MongoDBStoredEntity();
			
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
			
			// Make the ID for the new entity and set it.
			newEntityId = EntityIdGenerator.generateEntityId(cloned.getText(), cloned.getConfidence(), cloned.getLanguage(), cloned.getContext(), cloned.getDocumentId(), acl);
			cloned.setId(newEntityId);
			
			mongoCollection.insertOne(cloned);
		
		} else {
			
			throw new NonexistantEntityException("The entity does not exist.");
			
		}
		
		return newEntityId;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MongoDBStoredEntity getEntityById(String id) {
		
		Document document = new Document();
		document.append("_id", id);
		
		FindIterable<MongoDBStoredEntity> it = mongoCollection.find(document, MongoDBStoredEntity.class);
		
		List<MongoDBStoredEntity> entities = IteratorUtils.toList(it.iterator());
		
		if(CollectionUtils.isNotEmpty(entities)) {
		
			return entities.get(0);
			
		} else {
			
			return null;
			
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEntity(String entityId) {
		
		Document document = new Document();
		document.append("_id", entityId);
	
		DeleteResult deleteResult = mongoCollection.deleteOne(document);
		
		//deleteResult.getDeletedCount();
		
	}
	
	@Override
	public String storeEntity(Entity entity, String acl) throws EntityStoreException {

		String entityId = null;
		
		try {
		
			MongoDBStoredEntity mongoDBStoredEntity = MongoDBStoredEntity.fromEntity(entity, acl);
			mongoCollection.insertOne(mongoDBStoredEntity);
			
			entityId = mongoDBStoredEntity.getId();
		
		} catch (Exception ex) {
			
			throw new EntityStoreException("Unable to store entity.", ex);
			
		}
		
		return entityId;
		
	}

	@Override
	public Map<Entity, String> storeEntities(Set<Entity> entities, String acl) throws EntityStoreException {

		Map<Entity, String> storedEntities = new HashMap<Entity, String>();
		
		List<MongoDBStoredEntity> documents = new LinkedList<>();
		
		for(Entity entity : entities) {
			
			MongoDBStoredEntity mongoDBStoredEntity = MongoDBStoredEntity.fromEntity(entity, acl);
			documents.add(mongoDBStoredEntity);
			
			storedEntities.put(entity, mongoDBStoredEntity.getId().toString());
			
		}
		
		mongoCollection.insertMany(documents);
		
		return storedEntities;
		
	}

	@Override
	public long getEntityCount() throws EntityStoreException {

		long count = mongoCollection.count();
		
		return count;
		
	}

	@Override
	public long getEntityCount(String context) throws EntityStoreException {

		Bson condition = new Document("$eq", context);
		Bson filter = new Document("context", condition);
		
		long count = mongoCollection.count(filter);
	
		return count;
		
	}

	@Override
	public List<String> getContexts() throws EntityStoreException {

		DistinctIterable<String> contexts = mongoCollection.distinct("context", String.class);
		List<String> list = IteratorUtils.toList(contexts.iterator());
		
		return list;
		
	}

	@Override
	public void deleteContext(String context) throws EntityStoreException {

		Bson condition = new Document("$eq", context);
		Bson filter = new Document("context", condition);
		
		DeleteResult deleteResult = mongoCollection.deleteMany(filter);
		
		long deletedCount = deleteResult.getDeletedCount();
		
		LOGGER.info("Deleted {} items from MongoDB having context {}.", deletedCount, context);
		
	}

	@Override
	public void deleteDocument(String documentId) throws EntityStoreException {

		Bson condition = new Document("$eq", documentId);
		Bson filter = new Document("documentId", condition);
		
		DeleteResult deleteResult = mongoCollection.deleteMany(filter);
		
		long deletedCount = deleteResult.getDeletedCount();
		
		LOGGER.info("Deleted {} items from MongoDB having documentId {}.", deletedCount, documentId);
		
	}

	@Override
	public void close() throws EntityStoreException {

		mongoClient.close();
		
	}
	
	private CodecRegistry getCodecRegistry() {
		
		Codec<Document> defaultDocumentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		MongoDBStoredEntityCodec mongoDBStoredEntityCodec = new MongoDBStoredEntityCodec(defaultDocumentCodec);
		
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
			    MongoClient.getDefaultCodecRegistry(),
			    CodecRegistries.fromCodecs(mongoDBStoredEntityCodec)
			);
			 
		return codecRegistry;
		
	}
	
	private MongoCollection<MongoDBStoredEntity> getMongoCollection(String collection) {
		
		MongoCollection<MongoDBStoredEntity> mongoCollection = null;
		
		MongoIterable<String> collectionNames = mongoDatabase.listCollectionNames();
		List<String> list = IteratorUtils.toList(collectionNames.iterator());
		
		if (!mongoDatabase.toString().contains("Fongo") && !list.contains(collection)) {
			
			CreateCollectionOptions options = new CreateCollectionOptions();
			
			// The collection does not exist so create it.
			mongoDatabase.createCollection(collection, options);
			
			// Get the newly creation collection.
			mongoCollection = mongoDatabase.getCollection(collection, MongoDBStoredEntity.class);
			
			// Create an ascending index on the text field.
			mongoCollection.createIndex(new Document("text", 1));
			
		} else {
			
			mongoCollection = mongoDatabase.getCollection(collection, MongoDBStoredEntity.class);
			
		}
		    
		return mongoCollection;
		
	}

}