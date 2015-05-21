package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PartidoResource;


public class PartidoCollection {
	@InjectLinks({
		@InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "create-partido", title = "Create partido", type = MediaType.HANDICAPONE_API_PARTIDO),
		@InjectLink(value = "/partidos?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous partido", type = MediaType.HANDICAPONE_API_PARTIDO_COLLECTION, bindings = { @Binding(name = "before", value = "${instance.oldestTimestamp}") }),
		@InjectLink(value = "/partidos?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest partido", type = MediaType.HANDICAPONE_API_PARTIDO_COLLECTION, bindings = { @Binding(name = "after", value = "${instance.newestTimestamp}") }) })
	private List<Link> links;
	private List<Partido> partidos;
	private long newestTimestamp;
	private long oldestTimestamp;
	private String aaa;
	
	
	
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
	public String getAaa() {
		return aaa;
	}
	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	public void addPartido(Partido partido) {
		partidos.add(partido);
	}
	

}
