package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;


public class PartidoCollection {
	
	private List<Link> links;
	private List<Partido> partidos;//He cambiado partidos por partido, igual hay que cambiar algo mas
	private long newestTimestamp;
	private long oldestTimestamp;
	
	
	
	public PartidoCollection() {
		super();
		partidos = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Partido> getPartidos() {
		return partidos;
	}
	public void setPartidos(List<Partido> partidos) {
		this.partidos = partidos;
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
	public void addPartido(Partido partido) {
		partidos.add(partido);
	}
	

}
