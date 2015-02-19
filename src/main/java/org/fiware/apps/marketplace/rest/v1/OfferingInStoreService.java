package org.fiware.apps.marketplace.rest.v1;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Offerings;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.security.auth.OfferingAuth;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/api/v2/store/{storeName}/offering/")
public class OfferingInStoreService {
	
	// OBJECT ATTRIBUTES //
	@Autowired private OfferingBo offeringBo;
	@Autowired private OfferingAuth offeringAuth;
	@Autowired private StoreBo storeBo;
	
	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils(
			LoggerFactory.getLogger(DescriptionService.class));
	
	@GET
	@Produces({"application/xml", "application/json"})
	public Response listOfferingsInStore(
			@PathParam("storeName") String storeName,
			@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		
		Response response;
		
		try {
			Store store = storeBo.findByName(storeName);
			
			if (offeringAuth.canList(store)) {
				Offerings offerings = new Offerings(offeringBo.getStoreOfferingsPage(
						storeName, offset, max));
				response = Response.status(Status.OK).entity(offerings).build();
			} else {
				response = ERROR_UTILS.unauthorizedResponse(String.format(
						"list offerings in Store %s", storeName));
			}
		} catch (StoreNotFoundException e) {
			response = ERROR_UTILS.entityNotFoundResponse(e);
		} catch (Exception e) {
			response = ERROR_UTILS.internalServerError(e);
		}
		
		return response;
	}

}
