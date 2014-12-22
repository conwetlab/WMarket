package org.fiware.apps.marketplace.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Services;
import org.fiware.apps.marketplace.security.auth.OfferingRegistrationAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/offerings")	
public class AllOfferingsService {
	
	// OBJECT ATTRIBUTES //
	@Autowired private ServiceBo serviceBo;
	@Autowired private OfferingRegistrationAuth offeringRegistrationAuth;
	
	// CLASS ATTRIBUTES //
	private static final ErrorUtils ERROR_UTILS = new ErrorUtils();

	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/")	
	public Response listServices(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("100") @QueryParam("max") int max) {
		Response response;

		if (offset < 0 || max <= 0) {
			// Offset and Max should be checked
			response = ERROR_UTILS.badRequestResponse("offset and/or max are not valid");
		} else {
			if (offeringRegistrationAuth.canList()) {
				try {
					List<Service> servicesPage = serviceBo.getServicesPage(offset, max);
					Services returnedServices = new Services();
					returnedServices.setServices(servicesPage);
					response = Response.status(Status.OK).entity(returnedServices).build();
				} catch (Exception ex) {
					response = ERROR_UTILS.internalServerError(ex);
				}
			} else {
				response = ERROR_UTILS.unauthorizedResponse("list offerings");
			}	
		}
		
		return response;
	}
}
