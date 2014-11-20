package org.fiware.apps.marketplace.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
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

import org.fiware.apps.marketplace.bo.LocaluserBo;
import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Path("/registration")
public class StoreRegistrationService {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	

	StoreBo storeBo = (StoreBo)appContext.getBean("storeBo");
	ServiceBo serviceBo = (ServiceBo)appContext.getBean("serviceBo");
	LocaluserBo localuserBo = (LocaluserBo)appContext.getBean("localuserBo");

	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String actUser = auth.getName(); 

	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/store")	
	public Response saveStore(Store store) {	
		store.setRegistrationDate(new Date());
		store.setCreator(localuserBo.findByName(actUser));
		store.setLasteditor(localuserBo.findByName(actUser));
		storeBo.save(store);
		return Response.status(Status.CREATED).build();		
	}


	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/store/{storeName}")	
	public Response updateStore(@PathParam("storeName") String storeName, Store store) {

		Store storeDB = storeBo.findByName(storeName);		
		storeDB.setName(store.getName());
		storeDB.setUrl(store.getUrl());
		store.setLasteditor(localuserBo.findByName(actUser));
		
		storeBo.update(storeDB);
		return Response.status(Status.OK).build();		
	}

	@DELETE
	@Path("/store/{storeName}")	
	public Response deleteStore(@PathParam("storeName") String storeName) {	
		Store store = storeBo.findByName(storeName);
		storeBo.delete(store);
		return Response.status(Status.OK).build();		
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/store/{storeName}")	
	public Store findStore(@PathParam("storeName") String storeName) {		
		Store store = storeBo.findByName(storeName);
		if (store==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}
		return store;		

	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/stores/")	
	public List<Store> findStores() {				
		List<Store> stores = storeBo.findStores();
		if (stores==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}


		return stores;		

	}



	
}
