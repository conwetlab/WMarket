package org.fiware.apps.marketplace.rest;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.net.URI;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserAuth;
import org.hibernate.HibernateException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Path("/user")
public class UserService {

	// OBJECT ATTRIBUTES //
	@Autowired private UserBo userBo;
	@Autowired private UserAuth userAuth;
	@Autowired private UserValidator userValidator;
	// Encoder must be the same in all the platform: use the bean
	@Autowired private PasswordEncoder enconder;

	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(UserService.class),
			"The user and/or the email introduced are already registered in the system");

	// OBJECT METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createUser(@Context UriInfo uri, User user) {

		Response response;
		try {
			if (userAuth.canCreate()) {
				// Validate the user (exception is thrown if the user is not valid)
				userValidator.validateUser(user, true);
				
				// Users are not allowed to chose their user name
				// Set user name to null. In this case, the BO will be the one in 
				// charge of setting the user name
				user.setUserName(null);
								
				// Encode password. Set registration date...
				user.setPassword(enconder.encode(user.getPassword()));
				user.setRegistrationDate(new Date());
				
				// Save the new user
				userBo.save(user);
				
				// Generate the URI and return CREATED
				URI newURI = UriBuilder
						.fromUri(uri.getPath())
						.path(user.getUserName())
						.build();
				
				response = Response.created(newURI).build();		
			} else {
				response = ERROR_UTILS.unauthorizedResponse("create user");
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (HibernateException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
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

			if (userAuth.canUpdate(userToBeUpdated)) {
				// Validate the user (exception is thrown when the user is not valid)
				userValidator.validateUser(user, false);
				
				// At this moment, user name cannot be changed to avoid error with sessions...
				// userToBeUpdated.setUserName(user.getUserName());
				if (user.getUserName() != null && user.getUserName() != userToBeUpdated.getUserName()) {
					throw new ValidationException("userName cannot be changed");
				}
				
				if (user.getCompany() != null) {
					userToBeUpdated.setCompany(user.getCompany());
				}
				
				if (user.getPassword() != null) {
					userToBeUpdated.setPassword(enconder.encode(user.getPassword()));
				}
				
				if (user.getEmail() != null) {
					userToBeUpdated.setEmail(user.getEmail());
				}
				
				if (user.getDisplayName() != null) {
					userToBeUpdated.setDisplayName(user.getDisplayName());
				}
								
				userBo.update(userToBeUpdated);
				response = Response.status(Status.OK).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("update user " + username);
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (HibernateException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
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
			if (userAuth.canDelete(userToBeDeleted)) {
				userBo.delete(userToBeDeleted);
				response = Response.status(Status.NO_CONTENT).build();	
			} else {
				response = ERROR_UTILS.unauthorizedResponse("delete user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{username}")	
	public Response getUser(@PathParam("username") String username) {	

		Response response;
		try {
			User user = userBo.findByName(username);

			if (userAuth.canGet(user)) {
				// If the value of the attribute is null, the 
				// attribute won't be returned in the response
				// Note: We are not saving the user, otherwise the information will be lost
				user.setPassword(null);
				user.setEmail(null);
				response = Response.status(Status.OK).entity(user).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("get user " + username);
			}
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
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
				if (userAuth.canList()) {
					List<User> users = userBo.getUsersPage(offset, max);
					
					// If the value of the attribute is null, the 
					// attribute won't be returned in the response
					// Note: We are not saving the users, otherwise the information will be lost
					for (User user: users) {
						user.setPassword(null);
						user.setEmail(null);
					}
					
					return Response.status(Status.OK).entity(new Users(users)).build();
				} else {
					response = ERROR_UTILS.unauthorizedResponse("list users");
				}
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}	
		}

		return response;
	}
}
