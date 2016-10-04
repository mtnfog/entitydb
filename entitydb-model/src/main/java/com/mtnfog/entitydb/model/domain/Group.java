package com.mtnfog.entitydb.model.domain;

import java.util.HashSet;
import java.util.Set;

import com.mtnfog.entitydb.model.datastore.entities.GroupEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;

public class Group {

	private long id;
	private String groupName;
	private Set<String> users;
	
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