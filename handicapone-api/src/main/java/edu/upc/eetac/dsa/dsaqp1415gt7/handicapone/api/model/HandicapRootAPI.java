package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

import java.util.List;

import javax.ws.rs.core.Link;
 
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.HandicapRootAPIResource;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.PartidoResource;

public class HandicapRootAPI {
	@InjectLinks({
            @InjectLink(resource = HandicapRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Handicap Root API"),
            @InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "collection", title = "Latest stings", type = MediaType.HANDICAPONE_API_PARTIDO_COLLECTION),
            @InjectLink(resource = PartidoResource.class, style = Style.ABSOLUTE, rel = "create-sting", title = "Create new sting", type = MediaType.HANDICAPONE_API_PARTIDO)})
    	private List<Link> links;
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}