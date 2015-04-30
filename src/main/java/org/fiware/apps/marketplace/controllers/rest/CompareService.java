package org.fiware.apps.marketplace.controllers.rest;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.CompareBo;
import org.fiware.apps.marketplace.bo.MaintenanceBo;
import org.fiware.apps.marketplace.model.ComparisonResult;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@Path("/compare")
public class CompareService {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
	CompareBo compareBo = (CompareBo) appContext.getBean("compareBo");
	MaintenanceBo maintenanceBo = (MaintenanceBo) appContext.getBean("maintenanceBo");
	
	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/{sourceId}")
	public ComparisonResult compareServiceManifestation(@PathParam("sourceId") String sourceIdString) {
		try {
			maintenanceBo.initialize();
			ComparisonResult result = compareBo.compareService(sourceIdString);
			if (result == null)
				throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Something went wrong").build());
			return result;
		} catch (Exception ex) {
			System.out.println("Comparison failed. " + ex.getMessage());
			return null;
		}
	}

	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/{sourceId}/{targetId}")
	public ComparisonResult compareServiceManifestation(@PathParam("sourceId") String sourceIdString,
			@PathParam("targetId") String targetIdString) {
		try {
			maintenanceBo.initialize();
			ComparisonResult result = compareBo.compareService(sourceIdString, targetIdString);
			if (result == null)
				throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Something went wrong").build());
			return result;

		} catch (Exception ex) {
			System.out.println("Comparison failed. " + ex.getMessage());
			return null;
		}
	}
}
