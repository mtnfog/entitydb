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
package com.mtnfog.entitydb.model.datastore.entities;

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

/*@Entity
@Table(name="Users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "ApiKey"),
		@UniqueConstraint(columnNames = "UserName") })*/
public class User implements Serializable {

	/*private static final long serialVersionUID = -525179305412047212L;

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
    private long id;
	
	@Column(name="UserName", unique = true, nullable = false)
	private String userName;
	
	@Column(name="ApiKey", unique = true, nullable = false)
	private String apiKey;
	
	@OneToMany(mappedBy="user")
	private List<ContinuousQuery> continuousQueries;
	
    @ManyToMany
    @JoinTable(name = "Groups", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GroupID"))
	private List<Group> groups;

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

	public List<ContinuousQuery> getContinuousQueries() {
		return continuousQueries;
	}

	public void setContinuousQueries(List<ContinuousQuery> continuousQueries) {
		this.continuousQueries = continuousQueries;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}*/
	
}