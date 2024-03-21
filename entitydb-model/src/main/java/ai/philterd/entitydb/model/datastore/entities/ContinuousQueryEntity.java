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
package ai.philterd.entitydb.model.datastore.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A persisted query that is executed continuously as entities are ingested.
 * 
 * @author Philterd, LLC
 *
 */
@Entity
@Table(name="ContinuousQueries")
public class ContinuousQueryEntity implements Serializable {

	private static final long serialVersionUID = -525179305412047212L;

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
    private long id;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="UserID")
	private UserEntity user;
	
	@Column(name="Query")
	private String query;
	
	@Column(name="Timestamp")
	private Date timestamp;
	
	@Column(name="Days")
	private int days;
	
	@Column(name="snsTopicArn")
	private String snsTopicArn;

	public ContinuousQueryEntity() {
		
	}
	
	public ContinuousQueryEntity(UserEntity user, String query, Date timestamp, int days, String snsTopicArn) {
		
		this.user = user;
		this.query = query;
		this.timestamp = timestamp;
		this.days = days;
		this.snsTopicArn = snsTopicArn;
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getSnsTopicArn() {
		return snsTopicArn;
	}

	public void setSnsTopicArn(String snsTopicArn) {
		this.snsTopicArn = snsTopicArn;
	}

}