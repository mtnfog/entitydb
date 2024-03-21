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

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.google.common.io.Files;

/**
 * An Elasticsearch server that runs embedded in the same process as EntityDB.
 * This class is used for testing purposes and should not be used in a
 * production system.
 * 
 * @author Philterd, LLC
 *
 */
public class EmbeddedElasticsearchServer {

	private Node node;
	
	/**
	 * Creates a new embedded Elasticsearch server.
	 */
    public EmbeddedElasticsearchServer() {

    }
    
    /**
     * Starts the embedded Elasticsearch server.
     * @throws IOException Thrown if the server cannot successfully start.
     */
    public void start() throws IOException {
    	
        Settings.Builder elasticsearchSettings = Settings.builder()
                .put("http.enabled", "true")
                .put("http.port", "9200")
                .put("path.home", Files.createTempDir().getAbsolutePath())
                .put("path.data", Files.createTempDir().getAbsolutePath());

        node = NodeBuilder.nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();        

		node.client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject().startObject("entity")
					.startObject("properties")
						.startObject("id").field("type", "string").field("store", "yes").field("index", "analyzed").endObject()
						.startObject("text").field("type", "string").field("store", "yes").field("index", "analyzed").endObject()
						.endObject()
						.endObject()
					.endObject();
		
		Client client = node.client();

		CreateIndexRequest indexRequest = new CreateIndexRequest("entities");
		client.admin().indices().create(indexRequest).actionGet();

		client.admin().indices().preparePutMapping("entities").setType("entity").setSource(builder).execute()
				.actionGet();
		
    }
    
    /**
     * Closes and stops the server.
     */
    public void close() {
    	
    	node.close();
    	
    }

    /**
     * Gets the ELasticsearch {@link Client} for this server.
     * @return
     */
    public Client getClient() {
         
    	return node.client();
    	
    }

}