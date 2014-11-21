package org.fiware.apps.marketplace.rest;

import java.util.ArrayList;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.LocaluserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.security.auth.UserRegistrationAuth;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@Path("/user")
public class UserRegistrationService {
	
	// CLASS ATTRIBUTES //
	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();	
	private LocaluserBo localuserBo = (LocaluserBo) context.getBean("localuserBo");
	private UserRegistrationAuth userRegistrationAuth = (UserRegistrationAuth) context.getBean("userRegistrationAuth");
	
	// CLASS METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createUser(Localuser localUser) {
		//FIXME: Catch Exceptions
		if (userRegistrationAuth.canCreate()) {
			localUser.setRegistrationDate(new Date());
			localuserBo.save(localUser);
			return Response.status(Status.CREATED).build();		
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response updateUser(@PathParam("username") String username, Localuser localUser) {
		//FIXME: Catch exceptions
		Response response;
		
		try {
			Localuser userToBeUpdated = localuserBo.findByName(username);
			
			if (userRegistrationAuth.canUpdate(userToBeUpdated)) {
				userToBeUpdated.setCompany(localUser.getCompany());
				userToBeUpdated.setPassword(localUser.getPassword());
				userToBeUpdated.setEmail(localUser.getEmail());
				userToBeUpdated.setUsername(localUser.getUsername());		
				localuserBo.update(userToBeUpdated);
				response = Response.status(Status.OK).build();	
			} else {
				response = Response.status(Status.FORBIDDEN).build();
			}
		} catch (UserNotFoundException ex) {
			response = Response.status(Status.NOT_FOUND).build();
		}
		
		return response;
	}

	@DELETE
	@Path("/{username}")	
	public Response deleteUser(@PathParam("username") String username) {
		//FIXME: Catch exceptions
		Response response;
		
		try {
			Localuser userToBeDeleted = localuserBo.findByName(username);
			// Only a user can delete his/her account
			if (userRegistrationAuth.canDelete(userToBeDeleted)) {
				localuserBo.delete(userToBeDeleted);
				response = Response.status(Status.OK).build();	
			} else {
				response = Response.status(Status.FORBIDDEN).build();
			}
		} catch (UserNotFoundException ex) {
			response = Response.status(Status.NOT_FOUND).build();
		}
		
		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response findUser(@PathParam("username") String username) {	
		//FIXME: Catch exceptions
		Response response;
		
		try {
			Localuser localuser = localuserBo.findByName(username);

			if (userRegistrationAuth.canGet(localuser)) {
				response = Response.status(Status.OK).entity(localuser).build();
			} else {
				response = Response.status(Status.FORBIDDEN).build();
			}
		} catch (UserNotFoundException ex) {
			response = Response.status(Status.NOT_FOUND).build();
		}
		
		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response getList() {
		//FIXME: Catch Exceptions
		if (userRegistrationAuth.canList()) {
			List<Localuser> users = localuserBo.findLocalusers();
			
			// Return an empty list just in case the system has no users registered
			if (users == null){
				users = new ArrayList<Localuser>();
			}
			
			return Response.status(Status.OK).entity(new Users(users)).build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
}
