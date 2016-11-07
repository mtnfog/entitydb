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
package com.mtnfog.entitydb.search;

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
 * @author Mountain Fog, Inc.
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