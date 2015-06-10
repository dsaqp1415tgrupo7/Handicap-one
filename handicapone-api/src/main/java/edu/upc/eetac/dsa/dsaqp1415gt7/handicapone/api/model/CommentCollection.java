package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;



import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;


public class CommentCollection {

	private List<Link> links;
	private List<Comment> comments;
	private long newestTimestamp;
	private long oldestTimestamp;
	public CommentCollection(){
		super();
		comments = new ArrayList<Comment>();
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
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
	public void addComments(Comment comment) {
		this.comments.add(comment);
	}

}

