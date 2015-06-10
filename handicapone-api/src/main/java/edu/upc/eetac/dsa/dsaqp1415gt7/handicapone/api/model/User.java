package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.UserResource;





public class User {
	
	
	@InjectLinks({
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "users", title = "Latest users", type = MediaType.HANDICAPONE_API_USER_COLLECTION),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Usuario", type = MediaType.HANDICAPONE_API_ERROR, method = "getUser", bindings = @Binding(name = "iduser", value = "${instance.iduser}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-pick", title = "Create User Pick", type = MediaType.HANDICAPONE_API_PICK, method = "createUserPick", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-comment", title = "Create User Comment", type = MediaType.HANDICAPONE_API_COMMENT, method = "createUserComment", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "picks", title = "Picks", type = MediaType.HANDICAPONE_API_PICK_COLLECTION, method = "getUserPicks", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "comments", title = "Comments", type = MediaType.HANDICAPONE_API_PICK_COLLECTION, method = "getUserComments", bindings = @Binding(name = "username", value = "${instance.username}"))
		
	})
	
	
	private List<Link> links;
	private String username;
	private String name;
	private String rol;
	private String email;
	private String password;
	private long lastModified;
	private long creationTimestamp;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}
