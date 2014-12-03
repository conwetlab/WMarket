package org.fiware.apps.marketplace.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserRegistrationAuth;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;


@Path("/user")
public class UserRegistrationService {

	// OBJECT ATTRIBUTES //
	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();	
	private UserBo userBo = (UserBo) context.getBean("userBo");
	private UserRegistrationAuth userRegistrationAuth = (UserRegistrationAuth) context.getBean("userRegistrationAuth");
	private UserValidator usersValidator = (UserValidator) context.getBean("usersValidator");

	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			"The user and/or the email introduced are already registered in the system");

	// OBJECT METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createUser(User user) {

		Response response;
		try {
			if (userRegistrationAuth.canCreate() && usersValidator.validateUser(user)) {
				user.setRegistrationDate(new Date());
				userBo.save(user);
				response = Response.status(Status.CREATED).build();		
			} else {
				response = ERROR_UTILS.unauthorizedResponse("create user");
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;

	}

	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response updateUser(@PathParam("username") String username, User user) {

		Response response;
		try {
			User userToBeUpdated = userBo.findByName(username);

			if (userRegistrationAuth.canUpdate(userToBeUpdated) 
					&& usersValidator.validateUser(user)) {
				if (user.getCompany() != null) {
					userToBeUpdated.setCompany(user.getCompany());
				}
				
				if (user.getPassword() != null) {
					userToBeUpdated.setPassword(user.getPassword());
				}
				
				if (user.getEmail() != null) {
					userToBeUpdated.setEmail(user.getEmail());
				}
				
				if (user.getDisplayName() != null) {
					userToBeUpdated.setDisplayName(user.getDisplayName());
				}
				
				// At this moment, user name cannot be changed to avoid error with sessions...
				// userToBeUpdated.setUserName(user.getUserName());
				if (user.getUserName() != null && user.getUserName() != userToBeUpdated.getUserName()) {
					throw new ValidationException("userName cannot be changed");
				}
				
				userBo.update(userToBeUpdated);
				response = Response.status(Status.OK).build();	
			} else {
				response = ERROR_UTILS.unauthorizedResponse("update user " + username);
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;
	}

	@DELETE
	@Path("/{username}")	
	public Response deleteUser(@PathParam("username") String username) {

		Response response;
		try {
			User userToBeDeleted = userBo.findByName(username);
			// Only a user can delete his/her account
			if (userRegistrationAuth.canDelete(userToBeDeleted)) {
				userBo.delete(userToBeDeleted);
				response = Response.status(Status.OK).build();	
			} else {
				response = ERROR_UTILS.unauthorizedResponse("delete user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response findUser(@PathParam("username") String username) {	

		Response response;
		try {
			User localuser = userBo.findByName(username);

			if (userRegistrationAuth.canGet(localuser)) {
				response = Response.status(Status.OK).entity(localuser).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("get user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response listUsers(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
		} else {
			try {
				if (userRegistrationAuth.canList()) {
					List<User> users = userBo.getUsersPage(offset, max);
					return Response.status(Status.OK).entity(new Users(users)).build();
				} else {
					response = ERROR_UTILS.unauthorizedResponse("list users");
				}
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
			}	
		}

		return response;
	}
}
