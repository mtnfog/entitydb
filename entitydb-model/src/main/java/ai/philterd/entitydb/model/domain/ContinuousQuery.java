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
package ai.philterd.entitydb.model.domain;

import java.io.Serializable;
import java.util.Date;

import ai.philterd.entitydb.model.datastore.entities.ContinuousQueryEntity;

/**
 * A continuous query.
 * 
 * @author Philterd, LLC
 *
 */
public class ContinuousQuery implements Serializable {

	private static final long serialVersionUID = -7819227992158410058L;
	
	private long id;
	private User user;
	private String query;
	private Date timestamp;
	private int days;

	/**
	 * Creates a new continuous query.
	 */
	public ContinuousQuery() {
		
	}
	
	/**
	 * Creates a new continuous query.
	 * @param user The {@link User user} creating the continuous query.
	 * @param query The (EQL) query.
	 * @param timestamp A timestamp of when the query was created.
	 * @param days The number of days after the timestamp in which the query is to be executed.
	 */
	public ContinuousQuery(User user, String query, Date timestamp, int days) {
		
		this.user = user;
		this.query = query;
		this.timestamp = timestamp;
		this.days = days;
		
	}
	
	/**
	 * Creates a {@link ContinuousQuery} from a {@link ContinuousQueryEntity}.
	 * @param continuousQueryEntity A {@link ContinuousQueryEntity}.
	 * @return A {@link ContinuousQuery}.
	 */
	public static ContinuousQuery fromEntity(ContinuousQueryEntity continuousQueryEntity) {
		
		ContinuousQuery continuousQuery = new ContinuousQuery();
		
		continuousQuery.setId(continuousQueryEntity.getId());
		continuousQuery.setDays(continuousQueryEntity.getDays());
		continuousQuery.setQuery(continuousQueryEntity.getQuery());
		continuousQuery.setTimestamp(continuousQueryEntity.getTimestamp());
		continuousQuery.setUser(User.fromEntity(continuousQueryEntity.getUser()));
		
		return continuousQuery;
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
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

}