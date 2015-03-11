package org.fiware.apps.marketplace.rest.v2;

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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.Stores;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.StoreAuth;
import org.hibernate.HibernateException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Path("/api/v2/store")
public class StoreService {

	// OBJECT ATTRIBUTES //
	@Autowired private UserBo userBo;
	@Autowired private StoreBo storeBo;
	@Autowired private StoreAuth storeAuth;
	@Autowired private StoreValidator storeValidator;

	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(StoreService.class),
			"There is already a Store with that name/URL registered in the system");

	// OBJECT METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createStore(@Context UriInfo uri, Store store) {
		Response response;

		try {				
			// Get the current user
			User currentUser = userBo.getCurrentUser();

			store.setRegistrationDate(new Date());
			store.setCreator(currentUser);
			store.setLasteditor(currentUser);

			// Save the new Store
			storeBo.save(store);

			// Generate the URI and return CREATED
			URI newURI = UriBuilder
					.fromUri(uri.getPath())
					.path(store.getName())
					.build();

			response = Response.created(newURI).build();

		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError("There was an error retrieving the user from the database");
		} catch (DataIntegrityViolationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (HibernateException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;	
	}


	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/{storeName}")	
	public Response updateStore(@PathParam("storeName") String storeName, Store store) {
		Response response;

		try {
			Store storeDB = storeBo.findByName(storeName);								

			// At this moment, the URL cannot be changed...
			// if (store.getName() != null) {
			// 	storeDB.setName(store.getName());
			// }

			if (store.getUrl() != null) {
				storeDB.setUrl(store.getUrl());
			}

			if (store.getDescription() != null) {
				storeDB.setDescription(store.getDescription());
			}

			if (store.getDisplayName() != null) {
				storeDB.setDisplayName(store.getDisplayName());
			}

			store.setLasteditor(userBo.getCurrentUser());

			// Save the new Store and Return OK
			storeBo.update(storeDB);
			response = Response.status(Status.OK).build();

		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError("There was an error retrieving the user from the database");
		} catch (DataIntegrityViolationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (HibernateException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}

	@DELETE
	@Path("/{storeName}")	
	public Response deleteStore(@PathParam("storeName") String storeName) {
		Response response;

		try {
			//Retrieve the Store from the database
			Store store = storeBo.findByName(storeName);
			storeBo.delete(store);
			response = Response.status(Status.NO_CONTENT).build();		// Return 204 No Content
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{storeName}")	
	public Response getStore(@PathParam("storeName") String storeName) {
		Response response;

		try {
			// Retrieve the Store from the database
			Store store = storeBo.findByName(storeName);
			// Return the store
			response = Response.status(Status.OK).entity(store).build();
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response listStores(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
		} else {
			try {
				List<Store> stores = storeBo.getStoresPage(offset, max);
				response = Response.status(Status.OK).entity(new Stores(stores)).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}
		}

		return response;
	}
}
