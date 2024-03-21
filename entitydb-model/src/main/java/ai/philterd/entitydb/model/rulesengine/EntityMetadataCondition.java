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
package ai.philterd.entitydb.model.rulesengine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntityMetadataCondition extends Condition {
	
	public static final String EQUALS = "equals";
	public static final String MATCHES = "matches";
	
	public static final String METADATA = "metadata";
	public static final String VALUE = "value";

	@XmlAttribute
	private String metadata;
	
	@XmlAttribute
	private String value;
	
	@XmlAttribute
	private String test;
	
	public EntityMetadataCondition() {				
		
	}
	
	public EntityMetadataCondition(String metadata, String value) {
		
		this.metadata = metadata;
		this.value = value;
		this.test = EQUALS;
		
	}
	
	public EntityMetadataCondition(String metadata, String value, String test) {
		
		this.metadata = metadata;
		this.value = value;
		this.test = test;
		
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
}