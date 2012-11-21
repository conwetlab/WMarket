package org.fiware.apps.marketplace.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.LocaluserBo;
import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.util.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Path("/registration/userManagement")
public class UserRegistrationService {

	
	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	
	LocaluserBo localuserBo = (LocaluserBo)appContext.getBean("localuserBo");

	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/user")	
	public Response saveLocaluser(Localuser lucaluser) {	
		lucaluser.setRegistrationDate(new Date());
		localuserBo.save(lucaluser);
		return Response.status(Status.CREATED).build();		
	}


	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/user/{username}")	
	public Response updateLocaluser(@PathParam("username") String username, Localuser localuser) {		
		Localuser localuserDB = localuserBo.findByName(username);	
		localuserDB.setCompany(localuser.getCompany());
		localuser.setPassword(localuser.getPassword());
		localuser.setEmail(localuser.getEmail());
		localuser.setUsername(localuser.getUsername());		
		localuserBo.update(localuserDB);
		return Response.status(Status.OK).build();		
	}

	@DELETE
	@Path("/user/{username}")	
	public Response deleteLocaluser(@PathParam("username") String username) {	
		Localuser localuser = localuserBo.findByName(username);
		localuserBo.delete(localuser);
		return Response.status(Status.OK).build();		
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/user/{username}")	
	public Localuser findLocaluser(@PathParam("username") String username) {		
		Localuser localuser = localuserBo.findByName(username);
		if (localuser==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}
		return localuser;		
		
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/users/")	
	public List<Localuser> findLocalusers() {				
		List<Localuser> users = localuserBo.findLocalusers();
		if (users==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}
		return users;		
		
	}

	
}
