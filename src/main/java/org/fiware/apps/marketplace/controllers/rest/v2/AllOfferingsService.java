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

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Offerings;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/api/v2/offering")	
public class AllOfferingsService {
	
	@Autowired private OfferingBo offeringBo;
	
	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(AllOfferingsService.class));
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response listOfferings(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max,
			@DefaultValue("id") @QueryParam("orderBy") String orderBy,
			@DefaultValue("false") @QueryParam("desc") boolean desc) {
		
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
					//String.format("offset (%d) and/or max (%d) are not valid", offset, max));
		} else {
			try {
				List<Offering> offeringsPage = offeringBo.getOfferingsPage(offset, max, orderBy, desc);
				response = Response.status(Status.OK).entity(new Offerings(offeringsPage)).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (QueryException | SQLGrammarException ex) {
				response = ERROR_UTILS.badRequestResponse("Offerings cannot be ordered by " + orderBy + ".");
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}

		}
		
		return response;
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/bookmark")	
	public Response listBookmarked(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max,
			@DefaultValue("id") @QueryParam("orderBy") String orderBy,
			@DefaultValue("false") @QueryParam("desc") boolean desc) {
		
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
					//String.format("offset (%d) and/or max (%d) are not valid", offset, max));
		} else {
			try {
				List<Offering> offeringsPage = offeringBo.getBookmarkedOfferingsPage(offset, max, orderBy, desc);
				response = Response.status(Status.OK).entity(new Offerings(offeringsPage)).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (QueryException | SQLGrammarException ex) {
				response = ERROR_UTILS.badRequestResponse("Offerings cannot be ordered by " + orderBy + ".");
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}

		}
		
		return response;
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/lastViewed")	
	public Response lastVisited(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
					//String.format("offset (%d) and/or max (%d) are not valid", offset, max));
		} else {
			try {
				List<Offering> offeringsPage = offeringBo.getLastViewedOfferingsPage(offset, max);
				response = Response.status(Status.OK).entity(new Offerings(offeringsPage)).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}

		}
		
		return response;
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/viewedByOthers")	
	public Response viewedByOthers(	@DefaultValue("20") @QueryParam("max") int max) {
		
		Response response;

		if (max <= 0) {
			// Max should be checked
			response = ERROR_UTILS.badRequestResponse("max is not valid");
		} else {
			try {
				List<Offering> offeringsPage = offeringBo.getOfferingsViewedByOtherUsers(max);
				response = Response.status(Status.OK).entity(new Offerings(offeringsPage)).build();
			} catch (NotAuthorizedException ex) {
				response = ERROR_UTILS.notAuthorizedResponse(ex);
			} catch (IllegalArgumentException ex) {
				response = ERROR_UTILS.badRequestResponse(ex.getMessage());
			} catch (Exception ex) {
				response = ERROR_UTILS.internalServerError(ex);
			}

		}
		
		return response;
	}	
}
