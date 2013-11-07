package org.fiware.apps.marketplace.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;
import org.fiware.apps.marketplace.util.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;


@Path("/recommendation")
public class RecommendationService {
	
	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	
	RatingBo ratingBo = (RatingBo)appContext.getBean("ratingBo");
	
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategories")	
	public List<RatingObjectCategory> getObjectCategories() {			
		
		List<RatingObjectCategory> rat = ratingBo.getRatingObjectCategories();	
		
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;
	
	}
	
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}")	
	public List<RatingObject> getRecommendationsForObjectCategory(@PathParam("objectCategoryId") String objectCategoryId) {			
		
		List<RatingObject> rat = ratingBo.getRatingObjects(objectCategoryId);		
		Collections.sort(rat);
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;
	
	}
	

}
