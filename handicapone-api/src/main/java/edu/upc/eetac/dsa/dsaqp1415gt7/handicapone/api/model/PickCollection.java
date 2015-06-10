package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

public class PickCollection {

	private List<Link> links;
	private List<Pick> picks;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	public PickCollection() {
		super();
		picks = new ArrayList<>();
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Pick> getPicks() {
		return picks;
	}
	public void setPicks(List<Pick> picks) {
		this.picks = picks;
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
	public void addPicks(Pick pick) {
		picks.add(pick);
	}
}
