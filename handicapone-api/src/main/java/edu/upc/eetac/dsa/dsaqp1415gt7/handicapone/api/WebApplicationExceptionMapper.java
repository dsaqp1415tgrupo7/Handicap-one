package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.HandicapError;
 
@Provider
public class WebApplicationExceptionMapper implements
		ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) {
		HandicapError error = new HandicapError(
				exception.getResponse().getStatus(), exception.getMessage());
		return Response.status(error.getStatus()).entity(error)
				.type(MediaType.HANDICAPONE_API_ERROR).build();
	}
 
}


