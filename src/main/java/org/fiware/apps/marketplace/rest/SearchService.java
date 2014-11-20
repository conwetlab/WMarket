package org.fiware.apps.marketplace.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.SearchBo;
import org.fiware.apps.marketplace.model.SearchResult;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@Path("/search")
public class SearchService {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
	SearchBo searchBo = (SearchBo) appContext.getBean("searchBo");

	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/offerings/fulltext/{searchstring}")
	public SearchResult findStore(@PathParam("searchstring") String searchstring) {
		SearchResult searchresult = searchBo.searchByKeyword(searchstring);
		if (searchresult == null) {
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Something went wrong").build());
		}
		return searchresult;
	}
}
