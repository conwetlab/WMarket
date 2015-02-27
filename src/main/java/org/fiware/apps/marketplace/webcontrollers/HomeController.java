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

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Offering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

@Component
@Path("/")
public class HomeController {

	@Autowired private OfferingBo offeringBo;
	@Autowired private UserBo userBo;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView offeringListView() {
		ModelMap data = new ModelMap();

		try {
			data.addAttribute("user", userBo.getCurrentUser());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}

		data.addAttribute("title", "Catalogue - Marketplace");

		return new ModelAndView("offering.list", data);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{storeName}/{descriptionName}/{offeringName}/")
	public ModelAndView offeringDetailView(
			@PathParam("storeName") String storeName,
			@PathParam("descriptionName") String descriptionName,
			@PathParam("offeringName") String offeringName) {

		ModelMap data = new ModelMap();
		Offering offering;

		try {
			data.addAttribute("user", userBo.getCurrentUser());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}

		try {
			offering = offeringBo.findDescriptionByNameStoreAndDescription(
					storeName, descriptionName, offeringName);

			data.addAttribute("offering", offering);
			data.addAttribute("title", offering.getDisplayName() + " - Marketplace");
		} catch (OfferingNotFoundException | StoreNotFoundException | DescriptionNotFoundException e) {
			e.printStackTrace();
		}

		return new ModelAndView("offering.detail", data);
	}
}
