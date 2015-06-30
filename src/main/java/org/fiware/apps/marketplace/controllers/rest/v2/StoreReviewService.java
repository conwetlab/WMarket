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

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.DetailedReview;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.Reviews;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/api/v2/store/{storeName}/review")
public class StoreReviewService {
	
	@Autowired private StoreBo storeBo;
	
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(StoreReviewService.class), "");
	
	@POST
	public Response createReview(
			@Context UriInfo uri,
			@PathParam("storeName") String storeName, 
			Review review) {
		
		Response response;
		
		try {
			
			// When the object is saved in the database, the ID is automatically set
			// so we can use it.
			storeBo.createReview(storeName, review);
			
			// Generate the URI and return CREATED
			URI newURI = UriBuilder
					.fromUri(uri.getPath())
					.path(new Integer(review.getId()).toString())
					.build();
			
			response = Response.created(newURI).build();
			
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (ValidationException ex) {
			response = ERROR_UTILS.validationErrorResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
		
	}
	
	@POST
	@Path("{reviewId}")
	public Response updateReview(
			@PathParam("storeName") String storeName, 
			@PathParam("reviewId") int reviewId,
			Review review) {
		
		Response response;
		
		try {
			
			// When the object is saved in the database, the ID is automatically set
			// so we can use it.
			storeBo.updateReview(storeName, reviewId, review);
			
			response = Response.ok().build();
			
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException | ReviewNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (ValidationException ex) {
			response = ERROR_UTILS.validationErrorResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
	}
	
	@GET
	public Response getReviews(
			@PathParam("storeName") String storeName,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max,
			@DefaultValue("id") @QueryParam("orderBy") String orderBy,
			@DefaultValue("false") @QueryParam("desc") boolean desc,
			@DefaultValue("false") @QueryParam("detailed") boolean detailed) {
		
		Response response;
		
		try {
			
			if (offset < 0 || max <= 0) {
				// Offset and Max should be checked
				response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
			} else {
			
				List<Review> reviews = storeBo.getReviewsPage(storeName, offset, max, orderBy, desc);
				
				// Replace reviews by detailed reviews when query param is set
				if (detailed) {
					for (int i = 0; i < reviews.size(); i++) {
						reviews.set(i, new DetailedReview(reviews.get(i)));
					}
				}
				
				response = Response.ok().entity(new Reviews(reviews)).build();
			}

		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (QueryException | SQLGrammarException ex) {
			response = ERROR_UTILS.badRequestResponse("Reviews cannot be ordered by " + orderBy + ".");
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
		
	}
	
	@GET
	@Path("{reviewId}")
	public Response getReview(
			@PathParam("storeName") String storeName, 
			@PathParam("reviewId") int reviewId,
			@DefaultValue("false") @QueryParam("detailed") boolean detailed) {
		
		Response response;
		
		try {
			
			Review review = storeBo.getReview(storeName, reviewId);
			
			if (detailed) {
				review = new DetailedReview(review);
			}
			
			response = Response.ok().entity(review).build();
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException | ReviewNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		} 
		
		return response;
	}
	
	@DELETE
	@Path("{reviewId}")
	public Response deleteReview(			
			@PathParam("storeName") String storeName, 
			@PathParam("reviewId") int reviewId) {

		Response response;
		
		try {
			storeBo.deleteReview(storeName, reviewId);
			response = Response.status(Status.NO_CONTENT).build();	
		} catch (NotAuthorizedException ex) {
			response = ERROR_UTILS.notAuthorizedResponse(ex);
		} catch (StoreNotFoundException | ReviewNotFoundException ex) {
			response = ERROR_UTILS.entityNotFoundResponse(ex);
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		} 
		
		return response;
	}

}
