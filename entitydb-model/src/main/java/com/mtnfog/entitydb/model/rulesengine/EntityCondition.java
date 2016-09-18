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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.model.rulesengine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCondition extends Condition {

	public static final String EQUALS = "equals";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN = ">";
	public static final String LESS_THAN_OR_EQUAL = "<=";
	public static final String GREATER_THAN_OR_EQUAL = ">=";
	public static final String MATCHES = "matches";
	
	public static final String TEXT = "text";
	public static final String CONFIDENCE = "confidence";
	public static final String TYPE = "type";
	public static final String CONTEXT = "context";
	public static final String DOCUMENTID = "documentId";

	@XmlAttribute
	private String match;
	
	@XmlAttribute
	private String value;
	
	@XmlAttribute
	private String test;
	
	public EntityCondition() {
		
	}
	
	public EntityCondition(String match, String value) {
		
		this.match = match;
		this.value = value;
		this.test = EQUALS;
		
	}
	
	public EntityCondition(String match, String value, String test) {
		
		this.match = match;
		this.value = value;
		this.test = test;
		
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
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
	
}