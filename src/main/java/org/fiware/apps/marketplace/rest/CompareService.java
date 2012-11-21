package org.fiware.apps.marketplace.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.CompareBo;
import org.fiware.apps.marketplace.bo.MaintenanceBo;
import org.fiware.apps.marketplace.model.ComparisonResult;
import org.fiware.apps.marketplace.util.ApplicationContextProvider;
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
