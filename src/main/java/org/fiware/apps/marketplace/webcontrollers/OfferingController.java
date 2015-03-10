package org.fiware.apps.marketplace.webcontrollers;

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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.rest.v2.AllDescriptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

@Component
@Path("/offering/")
public class OfferingController {

	@Autowired private OfferingBo offeringBo;
	@Autowired private UserBo userBo;
	
	private Logger logger = LoggerFactory.getLogger(AllDescriptionsService.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response offeringListView() {
		
		Response response;
		ModelAndView view;
		ModelMap data = new ModelMap();

		try {
			data.addAttribute("user", userBo.getCurrentUser());
			data.addAttribute("title", "Catalogue - Marketplace");
			view = new ModelAndView("offering.list", data);
			response = Response.ok().entity(view).build();
		} catch (UserNotFoundException e) {
			logger.warn("User not found", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 500);
			data.addAttribute("statusPhrase", "Internal Server Error");
			data.addAttribute("content", "Sorry, we encountered an unexpected situation which related to the user.");
			view = new ModelAndView("core.httpresponse", data);
			response = Response.serverError().entity(view).build();
		}

		return response;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{storeName}/{descriptionName}/{offeringName}/")
	public Response offeringDetailView(
			@PathParam("storeName") String storeName,
			@PathParam("descriptionName") String descriptionName,
			@PathParam("offeringName") String offeringName) {

		Response response;
		ModelAndView view;
		ModelMap data = new ModelMap();
		Offering offering;

		try {
			data.addAttribute("user", userBo.getCurrentUser());
			offering = offeringBo.findOfferingByNameStoreAndDescription(
					storeName, descriptionName, offeringName);

			data.addAttribute("offering", offering);
			data.addAttribute("title", offering.getDisplayName() + " - Marketplace");
			
			view = new ModelAndView("offering.detail", data);
			response = Response.ok().entity(view).build();

		} catch (UserNotFoundException e) {
			logger.warn("User not found", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 500);
			data.addAttribute("statusPhrase", "Internal Server Error");
			data.addAttribute("content", "User not found");
			data.addAttribute("content", "Sorry, we encountered an unexpected situation related to the user.");
			view = new ModelAndView("core.httpresponse", data);
			response = Response.serverError().entity(view).build();
		} catch (NotAuthorizedException e) {
			logger.info("User not authorized", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 401);
			data.addAttribute("statusPhrase", "Unauthorized");
			data.addAttribute("content", "Sorry, you must sign in to view this page.");
			view = new ModelAndView("core.httpresponse", data);
			response = Response.status(Status.UNAUTHORIZED).entity(view).build();
		} catch (OfferingNotFoundException e) {
			logger.info("Offering not found", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 404);
			data.addAttribute("statusPhrase", "Not Found");
			data.addAttribute("content", "Sorry, we couldn't find the offering called '<name>'.".replaceFirst("<name>", offeringName));
			view = new ModelAndView("core.httpresponse", data);
			response = Response.status(Status.NOT_FOUND).entity(view).build();
		} catch (StoreNotFoundException e) {
			logger.info("Store not found", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 404);
			data.addAttribute("statusPhrase", "Not Found");
			data.addAttribute("content", "Sorry, we couldn't find the store called '<name>'.".replaceFirst("<name>", storeName));
			view = new ModelAndView("core.httpresponse", data);
			response = Response.status(Status.NOT_FOUND).entity(view).build();
		} catch (DescriptionNotFoundException e) {
			logger.info("Description not found", e);

			data.addAttribute("title", "Marketplace");
			data.addAttribute("statusCode", 404);
			data.addAttribute("statusPhrase", "Not Found");
			data.addAttribute("content", "Sorry, we couldn't find the description called '<name>'.".replaceFirst("<name>", descriptionName));
			view = new ModelAndView("core.httpresponse", data);
			response = Response.status(Status.NOT_FOUND).entity(view).build();
		}

		return response;
	}
}
