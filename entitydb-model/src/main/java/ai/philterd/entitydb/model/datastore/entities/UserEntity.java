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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A persisted user.
 * 
 * @author Philterd, LLC
 *
 */
@Entity
@Table(name="Users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "ApiKey"),
		@UniqueConstraint(columnNames = "UserName") })
public class UserEntity implements Serializable {

	private static final long serialVersionUID = -525179305412047212L;

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
    private long id;
	
	@Column(name="UserName", unique = true, nullable = false)
	private String userName;
	
	@Column(name="ApiKey", unique = true, nullable = false)
	private String apiKey;
	
	@OneToMany(mappedBy="user")
	private List<ContinuousQueryEntity> continuousQueries;
	
	@OneToMany(mappedBy="user")
	private List<NotificationEntity> notifications;
	
	@Column(name="Email", unique = false, nullable = false)
	private String email;
	
	@Column(name="Mobile", unique = false, nullable = true)
	private String mobile;
	
    @ManyToMany
    @JoinTable(name = "Groups", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GroupID"))
	private List<GroupEntity> groups;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<ContinuousQueryEntity> getContinuousQueries() {
		return continuousQueries;
	}

	public void setContinuousQueries(List<ContinuousQueryEntity> continuousQueries) {
		this.continuousQueries = continuousQueries;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public List<GroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupEntity> groups) {
		this.groups = groups;
	}

	public List<NotificationEntity> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationEntity> notifications) {
		this.notifications = notifications;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}