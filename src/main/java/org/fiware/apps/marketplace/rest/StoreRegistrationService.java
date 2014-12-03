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

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.Stores;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.StoreRegistrationAuth;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;


@Path("/store")
public class StoreRegistrationService {

	// OBJECT ATTRIBUTES //
	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();	
	private StoreBo storeBo = (StoreBo) context.getBean("storeBo");
	private StoreRegistrationAuth storeRegistrationAuth = (StoreRegistrationAuth) context.getBean("storeRegistrationAuth");
	private StoreValidator storeValidator = (StoreValidator) context.getBean("storeValidator");


	// CLASS ATTRIBUTES //
	private static final AuthUtils AUTH_UTILS = AuthUtils.getAuthUtils();
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			"There is already a Store with that name/URL registered in the system");


	// OBJECT METHODS //
	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createStore(Store store) {
		Response response;

		try {
			if (storeRegistrationAuth.canCreate() && storeValidator.validateStore(store)) {
				// Get the current user
				User currentUser = AUTH_UTILS.getLoggedUser();

				store.setRegistrationDate(new Date());
				store.setCreator(currentUser);
				store.setLasteditor(currentUser);

				// Save the new Store and return CREATED
				storeBo.save(store);
				response = Response.status(Status.CREATED).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("create store");
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError("There was an error retrieving the user from the database");
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
		}

		return response;	
	}


	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/{storeName}")	
	public Response updateStore(@PathParam("storeName") String storeName, Store store) throws UserNotFoundException {
		Response response;

		try {
			Store storeDB = storeBo.findByName(storeName);				
			if (storeRegistrationAuth.canUpdate(storeDB) && storeValidator.validateStore(store)) {
				if (store.getName() != null) {
					storeDB.setName(store.getName());
				}

				if (store.getUrl() != null) {
					storeDB.setUrl(store.getUrl());
				}

				if (store.getDescription() != null) {
					storeDB.setDescription(store.getDescription());
				}

				store.setLasteditor(AUTH_UTILS.getLoggedUser());

				// Save the new Store and Return OK
				storeBo.update(storeDB);
				response = Response.status(Status.OK).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("update store " + storeName);
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError("There was an error retrieving the user from the database");
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
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

			if (storeRegistrationAuth.canDelete(store)) {
				storeBo.delete(store);
				response = Response.status(Status.OK).build();		// Return OK
			} else {
				response = ERROR_UTILS.unauthorizedResponse("delete store " + storeName);
			}
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

			if (storeRegistrationAuth.canGet(store)) {
				response = Response.status(Status.OK).entity(store).build(); 	//Return the Store
			} else {
				response = ERROR_UTILS.unauthorizedResponse("get store " + storeName);
			}
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
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
				if (storeRegistrationAuth.canList()) {
					List<Store> stores = storeBo.getStoresPage(offset, max);
					response = Response.status(Status.OK).entity(new Stores(stores)).build();
				} else {
					response = ERROR_UTILS.unauthorizedResponse("list stores");
				}
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex.getCause().getMessage());
			}
		}

		return response;
	}
}
