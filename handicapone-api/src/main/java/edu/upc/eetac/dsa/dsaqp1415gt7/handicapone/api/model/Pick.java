package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PartidoResource;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;

public class Pick {
	
	@InjectLinks({
		@InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "self", title = "Pickpage", type = MediaType.HANDICAPONE_API_PICK, method = "getPicks", bindings = @Binding(name = "picks", value = "${instance.picks}"))
	})

	private List<Link> links;
	private String idpick;
	private String iduser;
	private String text;
	private String resultado;
	private String seguidores;
	private int cuota;
	private String fechaedicion;
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public String getIdpick() {
		return idpick;
	}
	public void setIdpick(String idpick) {
		this.idpick = idpick;
	}
	public String getIduser() {
		return iduser;
	}
	public void setIduser(String iduser) {
		this.iduser = iduser;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getSeguidores() {
		return seguidores;
	}
	public void setSeguidores(String seguidores) {
		this.seguidores = seguidores;
	}
	public int getCuota() {
		return cuota;
	}
	public void setCuota(int cuota) {
		this.cuota = cuota;
	}
	public String getFechaedicion() {
		return fechaedicion;
	}
	public void setFechaedicion(String fechaedicion) {
		this.fechaedicion = fechaedicion;
	}
	
	

}
