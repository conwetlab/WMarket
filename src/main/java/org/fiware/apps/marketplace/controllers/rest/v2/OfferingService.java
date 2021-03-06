package org.fiware.apps.marketplace.controllers.rest.v2;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Offerings;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/api/v2/store/{storeName}/description/{descriptionName}/offering/")
public class OfferingService {
	
	// OBJECT ATTRIBUTES //
	@Autowired private OfferingBo offeringBo;
	
	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(DescriptionService.class));
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("{offeringName}")
	public Response getOffering(
			@PathParam("storeName") String storeName, 
			@PathParam("descriptionName") String descriptionName,
			@PathParam("offeringName") String offeringName) {
		
		Response response;
		
		try {
			Offering offering = offeringBo.findOfferingByNameStoreAndDescription(
					storeName, descriptionName, offeringName);
			response = Response.status(Status.OK).entity(offering).build();
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (OfferingNotFoundException | StoreNotFoundException | DescriptionNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
	}
	
	@POST
	@Path("{offeringName}/bookmark")
	public Response bookmark(@PathParam("storeName") String storeName, 
			@PathParam("descriptionName") String descriptionName,
			@PathParam("offeringName") String offeringName) {
		
		Response response;
		
		try {
			offeringBo.bookmark(storeName, descriptionName, offeringName);
			response = Response.status(Status.NO_CONTENT).build();
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (OfferingNotFoundException | StoreNotFoundException | DescriptionNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	public Response listOfferingsInDescription(
			@PathParam("storeName") String storeName, 
			@PathParam("descriptionName") String descriptionName,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max,
			@DefaultValue("id") @QueryParam("orderBy") String orderBy,
			@DefaultValue("false") @QueryParam("desc") boolean desc) {
		
		Response response;
		
		if (offset < 0 || max <= 0) {	
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse(String.format(
					"offset (%d) and/or max (%d) are not valid", offset, max));
		} else {
			try {
				Offerings offerings = new Offerings(offeringBo.getDescriptionOfferingsPage(
						storeName, descriptionName, offset, max, orderBy, desc));
				response = Response.status(Status.OK).entity(offerings).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (DescriptionNotFoundException | StoreNotFoundException ex) {
				response = ERROR_UTILS.entityNotFoundResponse(ex);
			} catch (QueryException | SQLGrammarException ex) {
				response = ERROR_UTILS.badRequestResponse("Offerings cannot be ordered by " + orderBy + ".");
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}
		}
		
		return response;
	}
	
}
