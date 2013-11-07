package org.fiware.apps.marketplace.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;
import org.fiware.apps.marketplace.util.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@Path("/rating")
public class RatingService {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	
	RatingBo ratingBo = (RatingBo)appContext.getBean("ratingBo");


	@GET
	@PUT
	@POST
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/rating")	
	public Rating createRating(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId) {	
		RatingObject ratobj = ratingBo.getRatingObject(objectCategoryId, objectId);		
		Rating rat = new Rating();
		rat.setDate(new Date());
		rat.setRatingObject(ratobj);		
		ratingBo.saveRating(rat);		
		return rat;
	}

	@GET
	@PUT
	@POST
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/rating/{ratingId}/category/{categoryId}/stars/{stars}")	
	public Response createRating(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId, @PathParam("ratingId") int ratingId, @PathParam("categoryId") String categoryId, @PathParam("stars") int stars) {	
		
		Rating rat = ratingBo.getRating(ratingId);
		RatingCategory ratCat = ratingBo.getRatingCategory(objectCategoryId, categoryId);		
		RatingObjectCategory objCat = ratingBo.getRatingObjectCategory(objectCategoryId);			

	
		boolean found = false;

		for (RatingCategory dd :objCat.getRatingCategorys()){
			if(dd.getName().equals(ratCat.getName())){
				found = true;
			}			
		}

		if(!found || rat==null)		{
			return Response.status(Status.NOT_FOUND).build();
		}

		RatingCategoryEntry entry = ratingBo.getRatingCategoryEntry(ratingId, categoryId);
		if(entry == null){		
			entry = new RatingCategoryEntry();
		}

		switch (stars){
		case 1:entry.setValue(1); break;
		case 2:entry.setValue(2); break;
		case 3:entry.setValue(3); break;
		case 4:entry.setValue(4); break;
		case 5:entry.setValue(5); break;
		default: return Response.status(Status.PRECONDITION_FAILED).build();
		}

		entry.setRating(rat);
		entry.setRatingCategory(ratCat);

		ratingBo.saveRatingCategoryEntry(entry);
		return Response.status(Status.CREATED).build();	


	}
	
	
	
	@GET
	@PUT
	@POST
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/rating/{ratingId}/textualReview/{reviewText}")	
	public Response createReview(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId, @PathParam("ratingId") int ratingId, @PathParam("reviewText") String reviewText) {	
		
		Rating rat = ratingBo.getRating(ratingId);	
		
		
		if(rat==null){
			return Response.status(Status.NOT_FOUND).build();
		}

		rat.setFeedback(reviewText);
		ratingBo.saveRating(rat);
		return Response.status(Status.CREATED).build();	


	}
	
	


	@PUT
	@POST
	@Path("/objectCategory/{objectCategoryId}/category/{categoryId}")	
	public Response createRatingCategory(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("categoryId") String categoryId) {

		createRatingObjectCategory(objectCategoryId);		
		RatingObjectCategory objCat = ratingBo.getRatingObjectCategory(objectCategoryId);		
		RatingCategory obj = ratingBo.getRatingCategory(objectCategoryId, categoryId);
		if(obj == null){
			obj = new RatingCategory();
			obj.setRatingObjectCategory(objCat);
			obj.setName(categoryId);
			ratingBo.saveRatingCategory(obj);
			return Response.status(Status.CREATED).build();	
		}else{
			return Response.status(Status.OK).build();
		}

	}



	@PUT
	@POST
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/category/{categoryId}/stars/{value}")	
	public Response rateObject(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId, @PathParam("categoryId") String categoryId, @PathParam("value") double value) {	

		RatingObjectCategory objCat = ratingBo.getRatingObjectCategory(objectCategoryId);	
		RatingCategory ratingCat = ratingBo.getRatingCategory(objectCategoryId, categoryId);

		return Response.status(Status.CREATED).build();		
	}


	@PUT
	@POST
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}")	
	public Response createRatingObject(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId) {

		createRatingObjectCategory(objectCategoryId);		
		RatingObjectCategory objCat = ratingBo.getRatingObjectCategory(objectCategoryId);		
		RatingObject obj = ratingBo.getRatingObject(objectCategoryId, objectId);		

		if(obj == null){
			obj = new RatingObject();
			obj.setRatingObjectCategory(objCat);
			obj.setObjectId(objectId);
			ratingBo.saveRatingObject(obj);
			return Response.status(Status.CREATED).build();	
		}else{
			return Response.status(Status.OK).build();
		}
	}

	@PUT
	@POST
	@Path("/objectCategory/{objectCategoryId}")	
	public Response createRatingObjectCategory(@PathParam("objectCategoryId") String objectCategoryId) {
	
		RatingObjectCategory obj = ratingBo.getRatingObjectCategory(objectCategoryId);
		if(obj == null){
			obj = new RatingObjectCategory();
			obj.setName(objectCategoryId);
			ratingBo.saveRatingObjectCategory(obj);
			return Response.status(Status.CREATED).build();	
		}else{
			return Response.status(Status.OK).build();
		}

	}

	
	@DELETE
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/rating/{ratingId}")	
	public Response deleteRating(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId, @PathParam("ratingId") int ratingId) {
		
		Rating rat = ratingBo.getRating(ratingId);
		if(rat == null){		
			return Response.status(Status.NOT_FOUND).build();
		}		
		ratingBo.deleteRating(rat);
		return Response.status(Status.OK).build();	
	}

	
	@DELETE
	@Path("/objectCategory/{objectCategoryId}/category/{categoryId}")	
	public Response deleteRatingCategory(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("categoryId") String categoryId) {
		RatingCategory obj = ratingBo.getRatingCategory(objectCategoryId, categoryId);
		if(obj == null){		
			return Response.status(Status.NOT_FOUND).build();
		}
		ratingBo.deleteRatingCategory(obj);		
		return Response.status(Status.OK).build();	
	}
	
	
	@DELETE
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}")	
	public Response deleteRatingObject(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId) {
		RatingObject ratobj = ratingBo.getRatingObject(objectCategoryId, objectId);		
		if(ratobj == null){		
			return Response.status(Status.NOT_FOUND).build();
		}
		ratingBo.deleteRatingObject(ratobj);		
		return Response.status(Status.OK).build();		
	}

	@DELETE
	@Path("/objectCategory/{objectCategoryId}")	
	public Response deleteRatings(@PathParam("objectCategoryId") String objectCategoryId) {		
		RatingObjectCategory obj = ratingBo.getRatingObjectCategory(objectCategoryId);
		if(obj == null){		
			return Response.status(Status.NOT_FOUND).build();
		}
		ratingBo.deleteRatingObjectCategory(obj);		
		return Response.status(Status.OK).build();		
		
	}

	
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/rating/{ratingId}")	
	public Rating getRating(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId, @PathParam("ratingId") int ratingId) {		
		Rating rat = ratingBo.getRating(ratingId);		
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;
	
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/objects")	
	public List<RatingObject> getRatingObjects(@PathParam("objectCategoryId") String objectCategoryId) {			
		
		List<RatingObject> rat = ratingBo.getRatingObjects(objectCategoryId);		
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;
	
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}/ratings")	
	public List<Rating> getRatings(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId) {			
		
		List<Rating> rat = ratingBo.getRatings(objectCategoryId, objectId);		
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;	
	}
	
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
	@Path("/objectCategory/{objectCategoryId}/categories/")	
	public List<RatingCategory> getCategories(@PathParam("objectCategoryId") String objectCategoryId) {			
		
		List<RatingCategory> rat = ratingBo.getRatingCategories(objectCategoryId);		
		if (rat==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}		
		return rat;
	
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/category/{categoryId}")	
	public RatingCategory getRatingCategory(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("categoryId") String categoryId) {
		RatingCategory obj = ratingBo.getRatingCategory(objectCategoryId, categoryId);
		if (obj==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}	
		return obj;
	}
	
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}/object/{objectId}")	
	public RatingObject getRatingObject(@PathParam("objectCategoryId") String objectCategoryId, @PathParam("objectId") String objectId) {
		RatingObject ratobj = ratingBo.getRatingObject(objectCategoryId, objectId);		
		if (ratobj==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}	
		return ratobj;	
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/objectCategory/{objectCategoryId}")	
	public RatingObjectCategory getRatings(@PathParam("objectCategoryId") String objectCategoryId) {		
		RatingObjectCategory obj = ratingBo.getRatingObjectCategory(objectCategoryId);
		if (obj==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}	
		return obj;		
		
	}
	
	




}
