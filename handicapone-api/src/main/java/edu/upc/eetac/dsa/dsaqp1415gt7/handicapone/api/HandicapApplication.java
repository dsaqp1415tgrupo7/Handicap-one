package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;



import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
 
public class HandicapApplication extends ResourceConfig {
	public HandicapApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}