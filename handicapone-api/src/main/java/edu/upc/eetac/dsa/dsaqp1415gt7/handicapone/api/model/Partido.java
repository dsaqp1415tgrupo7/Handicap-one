
package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PartidoResource;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PickResource;

public class Partido {
	
	@InjectLinks({
		@InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "partidos", title = "Latest partidos", type = MediaType.HANDICAPONE_API_PARTIDO_COLLECTION),
		@InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Partido", type = MediaType.HANDICAPONE_API_PARTIDO, method = "getPartido", bindings = @Binding(name = "idpartido", value = "${instance.idpartido}")),
		@InjectLink(resource = PickResource.class, style = Style.ABSOLUTE, rel = "picks", title = "PartidoPicks", type = MediaType.HANDICAPONE_API_COMMENT, method = "getPartidoPicks", bindings = @Binding(name = "idpartido", value = "${instance.idpartido}")),
})
	private List<Link> links;
	private int idpartido;
	private String username;
	private String local;
	private String visitante;
	private String fechacierre;
	private String fechapartido;
	private int validado;
	private int resultado;
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
	public int getIdpartido() {
		return idpartido;
	}
	public void setIdpartido(int idpartido) {
		this.idpartido = idpartido;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getVisitante() {
		return visitante;
	}
	public void setVisitante(String visitante) {
		this.visitante = visitante;
	}
	public String getFechacierre() {
		return fechacierre;
	}
	public void setFechacierre(String fechacierre) {
		this.fechacierre = fechacierre;
	}
	public String getFechapartido() {
		return fechapartido;
	}
	public void setFechapartido(String fechapartido) {
		this.fechapartido = fechapartido;
	}
	public int getValidado() {
		return validado;
	}
	public void setValidado(int validado) {
		this.validado = validado;
	}
	public int getResultado() {
		return resultado;
	}
	public void setResultado(int resultado) {
		this.resultado = resultado;
	}


}

