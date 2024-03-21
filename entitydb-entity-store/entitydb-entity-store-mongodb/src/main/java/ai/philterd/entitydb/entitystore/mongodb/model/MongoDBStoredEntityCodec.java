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
package ai.philterd.entitydb.entitystore.mongodb.model;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;

import ai.philterd.entitydb.model.entitystore.EntityIdGenerator;

/**
 * A MongoDB codec for {@link MongoDBStoredEntity}.
 * 
 * @author Philterd, LLC
 *
 */
public class MongoDBStoredEntityCodec implements CollectibleCodec<MongoDBStoredEntity> {

	private Codec<Document> codec;
	 
	public MongoDBStoredEntityCodec() {
        this.codec = new DocumentCodec();
    }
 
    public MongoDBStoredEntityCodec(Codec<Document> codec) {
        this.codec = codec;
    }
	
	@Override
	public void encode(BsonWriter writer, MongoDBStoredEntity mongoDBStoredEntity, EncoderContext encoderContext) {

		Document document = new Document();
		
	    document.put("_id", mongoDBStoredEntity.getId());
	    
	    if (!StringUtils.isEmpty(mongoDBStoredEntity.getText())) {
	    	document.put("text", mongoDBStoredEntity.getText());
	    }
	    
	    if (!StringUtils.isEmpty(mongoDBStoredEntity.getType())) {
	    	document.put("type", mongoDBStoredEntity.getType());
	    }
	    	    
	    if (!StringUtils.isEmpty(mongoDBStoredEntity.getContext())) {
	    	document.put("context", mongoDBStoredEntity.getContext());
	    }
	    
	    if (!StringUtils.isEmpty(mongoDBStoredEntity.getDocumentId())) {
	    	document.put("documentId", mongoDBStoredEntity.getDocumentId());
	    }
	    
	    if (!StringUtils.isEmpty(mongoDBStoredEntity.getAcl())) {
	    	document.put("acl", mongoDBStoredEntity.getAcl());
	    }	    	
	    
    	if (!StringUtils.isEmpty(mongoDBStoredEntity.getUri())) {
	    	document.put("uri", mongoDBStoredEntity.getUri());
	    }
    	
    	if (!StringUtils.isEmpty(mongoDBStoredEntity.getLanguage())) {
	    	document.put("language", mongoDBStoredEntity.getLanguage());
	    }    		    	    
    	
    	if (mongoDBStoredEntity.getMetadata() != null && !mongoDBStoredEntity.getMetadata().isEmpty()) {
    		document.put("metadata", mongoDBStoredEntity.getMetadata());
    	}
    	 	
        // These values are never null.
    	document.put("confidence", mongoDBStoredEntity.getConfidence());
    	document.put("extractionDate", mongoDBStoredEntity.getExtractionDate());
    	document.put("timestamp", mongoDBStoredEntity.getTimestamp());
	    document.put("visible", mongoDBStoredEntity.getVisible());
    	document.put("indexed", mongoDBStoredEntity.getIndexed());
    	
	    codec.encode(writer, document, encoderContext);
	    
	}

	@Override
	public MongoDBStoredEntity decode(BsonReader reader, DecoderContext decoderContext) {

		Document document = codec.decode(reader, decoderContext);

		MongoDBStoredEntity mongoDBStoredEntity = new MongoDBStoredEntity();
			
		mongoDBStoredEntity.setId(document.getString("_id"));
		mongoDBStoredEntity.setText(document.getString("text"));
		mongoDBStoredEntity.setType(document.getString("type"));
		mongoDBStoredEntity.setContext(document.getString("context"));
		mongoDBStoredEntity.setDocumentId(document.getString("documentId"));
		mongoDBStoredEntity.setConfidence(document.getDouble("confidence"));
		mongoDBStoredEntity.setExtractionDate(document.getLong("extractionDate"));
		mongoDBStoredEntity.setUri((String) document.get("uri"));
		mongoDBStoredEntity.setLanguage((String) document.get("language"));
		mongoDBStoredEntity.setAcl((String) document.get("acl"));
		mongoDBStoredEntity.setVisible(document.getInteger("visible"));
		mongoDBStoredEntity.setTimestamp(document.getLong("timestamp"));
		mongoDBStoredEntity.setIndexed(document.getLong("indexed"));
		
		// Add the metadata.
		Map<String, String> metadata = (Map<String, String>) document.get("metadata");
		mongoDBStoredEntity.setMetadata(metadata);		
		
		return mongoDBStoredEntity;

	}
	
	@Override
	public Class<MongoDBStoredEntity> getEncoderClass() {

		return MongoDBStoredEntity.class;
		
	}

	@Override
	public MongoDBStoredEntity generateIdIfAbsentFromDocument(MongoDBStoredEntity document) {

		if (!documentHasId(document)) {
			document.setId(EntityIdGenerator.generateEntityId(document.getText(), document.getConfidence(), document.getLanguage(), document.getContext(), document.getDocumentId(), document.getAcl()));
	    }
		
	    return document;
	    
	}

	@Override
	public boolean documentHasId(MongoDBStoredEntity document) {

		return document.getId() != null;
		
	}

	@Override
	public BsonValue getDocumentId(MongoDBStoredEntity document) {

		if (!documentHasId(document)) {
	        throw new IllegalStateException("The document does not contain an _id");
	    }
	 
	    return new BsonString(document.getId().toString());
	    
	}

}