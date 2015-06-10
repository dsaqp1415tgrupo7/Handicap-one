package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PickResource;



public class Pick {
	@InjectLinks({
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "picks", title = "Latest picks", type = MediaType.HANDICAPONE_API_PICK_COLLECTION),
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Pick", type = MediaType.HANDICAPONE_API_ERROR, method = "getPick", bindings = @Binding(name = "idpick", value = "${instance.idpick}")),
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "comments", title = "PickComments", type = MediaType.HANDICAPONE_API_COMMENT, method = "getPickComments", bindings = @Binding(name = "idpick", value = "${instance.idpick}")),
		})
	
	private List<Link> links;
	private int idPick;
	private String username;
	private String text;
	private int resultado;
	private int seguidores;
	private int cuota;
	private long fechaEdicion;
	private long creationTimestamp;
	private long lastModified;
	
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public int getIdPick() {
		return idPick;
	}
	public void setIdPick(int idPick) {
		this.idPick = idPick;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getResultado() {
		return resultado;
	}
	public void setResultado(int resultado) {
		this.resultado = resultado;
	}
	public int getSeguidores() {
		return seguidores;
	}
	public void setSeguidores(int seguidores) {
		this.seguidores = seguidores;
	}
	public int getCuota() {
		return cuota;
	}
	public void setCuota(int cuota) {
		this.cuota = cuota;
	}
	public long getFechaEdicion() {
		return fechaEdicion;
	}
	public void setFechaEdicion(long fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
	}
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}