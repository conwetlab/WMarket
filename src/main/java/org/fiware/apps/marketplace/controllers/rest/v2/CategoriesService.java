package org.fiware.apps.marketplace.controllers.rest.v2;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.CategoryBo;
import org.fiware.apps.marketplace.exceptions.ClassificationNotFoundException;
import org.fiware.apps.marketplace.model.Categories;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Offerings;
import org.hibernate.HibernateException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

@Component
@Path("/api/v2/category")
public class CategoriesService {
	
	@Autowired private CategoryBo categoryBo;
	
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(CategoriesService.class));

	
	@GET
	@Produces({"application/xml", "application/json"})
	public Response getObjectCategories(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		
		Response response;
		
		try {
			List<Category> categories = categoryBo.getCategoriesPage(offset, max);	
			response = Response.ok().entity(new Categories(categories)).build();
		} catch (Exception ex) {
			response = ERROR_UTILS.internalServerError(ex);
		}
		
		return response;
	
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("{categoryName}")	
	public Response getCategoryRecommendations(@PathParam("categoryName") String categoryName,
			@DefaultValue("averageScore") @QueryParam("orderBy") String orderBy,
			@DefaultValue("true") @QueryParam("desc") boolean desc) {
		
		Response response;
		
		try {
			List<Offering> offerings = categoryBo.getCategoryOfferingsSortedBy(categoryName, orderBy, desc);
			response = Response.ok().entity(new Offerings(offerings)).build();			
		} catch (ClassificationNotFoundException e) {
			response = ERROR_UTILS.entityNotFoundResponse(e);
		} catch (HibernateException e) {
			Throwable cause = e.getCause();
			
			if (cause instanceof MySQLSyntaxErrorException) {
				// Return a more human readable message
				response = ERROR_UTILS.badRequestResponse("Offerings cannot be ordered by " + orderBy + ".");
			} else {
				response = ERROR_UTILS.badRequestResponse(e);
			}
		} catch (Exception e) {
			response = ERROR_UTILS.internalServerError(e);
		}
		
		return response;
	}

}
