package com.webgame.main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/webservice")

public class WebService {
	
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String TestService(){
		return "Test Hello";
	}

}
