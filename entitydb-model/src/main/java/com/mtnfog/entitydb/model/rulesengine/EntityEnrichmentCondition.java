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
package com.mtnfog.entitydb.model.rulesengine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntityEnrichmentCondition extends Condition {
	
	public static final String EQUALS = "equals";
	public static final String MATCHES = "matches";
	
	public static final String ENRICHMENT = "enrichment";
	public static final String VALUE = "value";

	@XmlAttribute
	private String enrichment;
	
	@XmlAttribute
	private String value;
	
	@XmlAttribute
	private String test;
	
	public EntityEnrichmentCondition() {				
		
	}
	
	public EntityEnrichmentCondition(String enrichment, String value) {
		
		this.enrichment = enrichment;
		this.value = value;
		this.test = EQUALS;
		
	}
	
	public EntityEnrichmentCondition(String enrichment, String value, String test) {
		
		this.enrichment = enrichment;
		this.value = value;
		this.test = test;
		
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEnrichment() {
		return enrichment;
	}

	public void setEnrichment(String enrichment) {
		this.enrichment = enrichment;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
	
}