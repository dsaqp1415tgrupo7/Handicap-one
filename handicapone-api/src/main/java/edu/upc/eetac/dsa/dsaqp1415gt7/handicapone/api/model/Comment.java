package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;



import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PickResource;

public class Comment {
	@InjectLinks({
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "comments", title = "Latest comments", type = MediaType.HANDICAPONE_API_COMMENT_COLLECTION),
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Comment", type = MediaType.HANDICAPONE_API_ERROR, method = "getComment", bindings = @Binding(name = "idComment", value = "${instance.idComment}")) })
	
	private List<Link> links;
	private int idcomment;
	private String username;
	private String text;
	private long fechaEdicion;
	private long creationTimestamp;
	private long lastModified;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public int getIdcomment() {
		return idcomment;
	}
	public void setIdcomment(int idcomment) {
		this.idcomment = idcomment;
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

