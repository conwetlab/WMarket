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
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.ServiceError;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.security.auth.UserRegistrationAuth;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Path("/user")
public class UserRegistrationService {
		
	// OBJECT ATTRIBUTES //
	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();	
	private LocaluserBo localuserBo = (LocaluserBo) context.getBean("localuserBo");
	private UserRegistrationAuth userRegistrationAuth = (UserRegistrationAuth) context.getBean("userRegistrationAuth");
	
	// CLASS METHODS: Generate HTTP Responses //
	private static Response badRequestResponse(DataAccessException ex) {
		String message;
		if (ex.getRootCause() instanceof MySQLIntegrityConstraintViolationException)  {
			message = "The user and/or the email introduced are already registered in the system";
		} else {
			message = ex.getRootCause().getMessage();
		}
		
		return Response.status(Status.BAD_REQUEST).entity(
				new ServiceError(ErrorType.BAD_REQUEST, message)).build();
	}
	
	private static Response userNotFoundResponse(String username) {
		return Response.status(Status.NOT_FOUND).entity(
				new ServiceError(ErrorType.NOT_FOUND, "User " + username + " not found")).build();
	}
	
	private static Response unauthorizedResponse(String action) {
		return Response.status(Status.UNAUTHORIZED).entity(
				new ServiceError(ErrorType.NOT_FOUND, "You are not authorized to " + action)).build();
	}
	
	private static Response serviceUnavailableResponse(String cause) {
		return Response.status(Status.SERVICE_UNAVAILABLE).entity(
				new ServiceError(ErrorType.SERVICE_UNAVAILABLE, cause)).build();
	}
	
	// OBJECT METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createUser(Localuser localUser) {
		
		Response response;
		try {
			if (userRegistrationAuth.canCreate()) {
				localUser.setRegistrationDate(new Date());
				localuserBo.save(localUser);
				response = Response.status(Status.CREATED).build();		
			} else {
				response = unauthorizedResponse("create user");
			}
		} catch (DataAccessException ex) {
			response = badRequestResponse(ex);
		} catch (Exception ex) {
			response = serviceUnavailableResponse(ex.getCause().getMessage());
		}
		
		return response;

	}

	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response updateUser(@PathParam("username") String username, Localuser localUser) {
		
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
				response = unauthorizedResponse("update user " + username);
			}
		} catch (DataAccessException ex) {
			response = badRequestResponse(ex);
		} catch (UserNotFoundException ex) {
			response = userNotFoundResponse(username);
		} catch (Exception ex) {
			response = serviceUnavailableResponse(ex.getCause().getMessage());
		}
		
		return response;
	}

	@DELETE
	@Path("/{username}")	
	public Response deleteUser(@PathParam("username") String username) {

		Response response;
		try {
			Localuser userToBeDeleted = localuserBo.findByName(username);
			// Only a user can delete his/her account
			if (userRegistrationAuth.canDelete(userToBeDeleted)) {
				localuserBo.delete(userToBeDeleted);
				response = Response.status(Status.OK).build();	
			} else {
				response = unauthorizedResponse("delete user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = userNotFoundResponse(username);
		} catch (Exception ex) {
			response = serviceUnavailableResponse(ex.getCause().getMessage());
		}
		
		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response findUser(@PathParam("username") String username) {	

		Response response;
		try {
			Localuser localuser = localuserBo.findByName(username);

			if (userRegistrationAuth.canGet(localuser)) {
				response = Response.status(Status.OK).entity(localuser).build();
			} else {
				response = unauthorizedResponse("get user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = userNotFoundResponse(username);
		} catch (Exception ex) {
			response = serviceUnavailableResponse(ex.getCause().getMessage());
		}
		
		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response getList() {
		
		Response response;
		try {
			if (userRegistrationAuth.canList()) {
				List<Localuser> users = localuserBo.findLocalusers();
				
				// Return an empty list just in case the system has no users registered
				if (users == null){
					users = new ArrayList<Localuser>();
				}
				
				return Response.status(Status.OK).entity(new Users(users)).build();
			} else {
				response = unauthorizedResponse("list users");
			}
		} catch (Exception ex) {
			response = serviceUnavailableResponse(ex.getCause().getMessage());
		}
		
		return response;
	}
}
