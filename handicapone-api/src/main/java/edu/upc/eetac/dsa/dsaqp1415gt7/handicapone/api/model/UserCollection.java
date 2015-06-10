package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;


public class UserCollection {
	
	private List<Link> links;
	private List<User> users;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	
	
	public UserCollection() {
		super();
		users = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public long getNewestTimestamp() {
		return newestTimestamp;
	}
	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}
	public long getOldestTimestamp() {
		return oldestTimestamp;
	}
	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}
	public void addUser(User user) {
		users.add(user);
	}
	

}

