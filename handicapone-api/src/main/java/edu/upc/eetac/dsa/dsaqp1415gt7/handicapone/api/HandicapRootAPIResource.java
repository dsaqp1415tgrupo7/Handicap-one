package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.HandicapRootAPI;
 
@Path("/")
public class HandicapRootAPIResource {
	@GET
	public HandicapRootAPI getRootAPI() {
		HandicapRootAPI api = new HandicapRootAPI();
		return api;
	}
}