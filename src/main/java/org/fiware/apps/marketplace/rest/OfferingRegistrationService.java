package org.fiware.apps.marketplace.rest;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014-2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Services;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.validators.ServiceValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.OfferingRegistrationAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;

@Component
@Path("/store/{storeName}/offering/")	
public class OfferingRegistrationService {

	// OBJECT ATTRIBUTES //
	@Autowired private StoreBo storeBo;
	@Autowired private ServiceBo serviceBo;
	@Autowired private OfferingRegistrationAuth offeringRegistrationAuth;
	@Autowired private ServiceValidator serviceValidator;
	@Autowired private AuthUtils authUtils;

	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(OfferingRegistrationService.class), 
			"There is already an Offering in this Store with that name/URL");

	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/")	
	public Response createService(@PathParam("storeName") String storeName, Service service) {	
		Response response;

		try {			
			if (offeringRegistrationAuth.canCreate()) {
				
				// Validate service (exception is thrown if the service is not valid) 
				serviceValidator.validateService(service, true);

				User user = authUtils.getLoggedUser();
				Store store = storeBo.findByName(storeName);
				service.setRegistrationDate(new Date());
				service.setStore(store);
				service.setCreator(user);
				service.setLasteditor(user);

				serviceBo.save(service);
				response = Response.status(Status.CREATED).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("create offering");
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (StoreNotFoundException ex) {
			//The Store is an URL... If the Store does not exist a 404
			//should be returned instead of a 400
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError(
					"There was an error retrieving the user from the database");
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}


	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/{serviceName}")	
	public Response updateService(@PathParam("storeName") String storeName, 
			@PathParam("serviceName") String serviceName, Service serviceInfo) {

		Response response;

		try {
			@SuppressWarnings("unused")
			Store store = storeBo.findByName(storeName);	//Check that the Store exists
			Service service = serviceBo.findByNameAndStore(serviceName, storeName);

			if (offeringRegistrationAuth.canUpdate(service)) {
				
				// Validate service (exception is thrown if the service is not valid) 
				serviceValidator.validateService(serviceInfo, false);
				
				if (serviceInfo.getName() != null) {
					service.setName(serviceInfo.getName());
				}
				
				if (serviceInfo.getUrl() != null) {
					service.setUrl(serviceInfo.getUrl());
				}
				
				if (serviceInfo.getDescription() != null) {
					service.setDescription(serviceInfo.getDescription());
				}
				
				service.setLasteditor(authUtils.getLoggedUser());

				serviceBo.update(service);
				response = Response.status(Status.OK).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse(
						"update offering " + serviceName);
			}
		} catch (ValidationException ex) {
			response = ERROR_UTILS.badRequestResponse(ex.getMessage());
		} catch (ServiceNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (UserNotFoundException ex) {
			response = ERROR_UTILS.internalServerError(
					"There was an error retrieving the user from the database");
		} catch (DataAccessException ex) {
			response = ERROR_UTILS.badRequestResponse(ex);
		} catch (Exception ex) {

			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}

	@DELETE
	@Path("/{serviceName}")	
	public Response deleteService(@PathParam("storeName") String storeName, 
			@PathParam("serviceName") String serviceName) {
		Response response;

		try {
			@SuppressWarnings("unused")
			Store store = storeBo.findByName(storeName);	//Check that the Store exists
			Service service = serviceBo.findByNameAndStore(serviceName, storeName);

			if (offeringRegistrationAuth.canDelete(service)) {
				serviceBo.delete(service);
				response = Response.status(Status.NO_CONTENT).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("delete offering " + serviceName);
			}
		} catch (ServiceNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}

		return response;
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/{serviceName}")	
	public Response getService(@PathParam("storeName") String storeName, 
			@PathParam("serviceName") String serviceName) {	
		Response response;

		try {
			@SuppressWarnings("unused")
			Store store = storeBo.findByName(storeName);	//Check that the Store exists
			Service service = serviceBo.findByNameAndStore(serviceName, storeName);

			if (offeringRegistrationAuth.canGet(service)) {
				response = Response.status(Status.OK).entity(service).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse("get offering " + serviceName);
			}
		} catch (ServiceNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
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
	public Response listServicesInStore(@PathParam("storeName") String storeName, 
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
		} else {
			try {
				int toIndex;
				Store store = storeBo.findByName(storeName);
				
				if (offeringRegistrationAuth.canList(store)) {
					Services returnedServices = new Services();
					List<Service> allServices = store.getServices();

					// Otherwise (if offset > allServices.size() - 1) an empty list will be returned
					if (offset <= allServices.size() - 1) {
						if (offset + max > allServices.size()) {
							toIndex = allServices.size();
						} else {
							toIndex = offset + max;
						}	
						returnedServices.setServices(store.getServices().subList(offset, toIndex));
					}

					response = Response.status(Status.OK).entity(returnedServices).build();
				} else {
					response = ERROR_UTILS.unauthorizedResponse("list offerings");
				}
				

			} catch (StoreNotFoundException ex) {
				response = ERROR_UTILS.entityNotFoundResponse(ex);
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}
		}

		return response;
	}
}
