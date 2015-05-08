package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;


public class PartidoCollection {
	
	private List<Link> links;
	private List<Partido> partido;
	private long newestTimestamp;
	private long oldestTimestamp;
	private String aaa;
	
	
	
	public PartidoCollection() {
		super();
		partido = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Partido> getPartidos() {
		return partido;
	}
	public void setPartidos(List<Partido> partidos) {
		this.partido = partidos;
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
	public String getAaa() {
		return aaa;
	}
	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	
	
	

}
