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
package com.mtnfog.entitydb.model.domain;

import java.util.Date;

import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;

/**
 * A continuous query.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class ContinuousQuery {

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