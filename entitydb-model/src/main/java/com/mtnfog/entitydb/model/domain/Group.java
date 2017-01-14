/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.mtnfog.entitydb.model.datastore.entities.GroupEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;

/**
 * A group of users.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 7032996772573941419L;
	
	private long id;
	private String groupName;
	private Set<String> users;
	
	/**
	 * Creates a {@link Group} from a {@link GroupEntity}.
	 * @param groupEntity A {@link GroupEntity}.
	 * @return A {@link Group}.
	 */
	public static Group fromEntity(GroupEntity groupEntity) {
				
		Set<String> users = new HashSet<String>();
		
		for(UserEntity userEntity : groupEntity.getUsers()) {
			users.add(userEntity.getUserName());
		}
		
		Group group = new Group();
		group.setId(groupEntity.getId());
		group.setGroupName(groupEntity.getGroupName());
		group.setUsers(users);
		
		return group;
		
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}
	
}