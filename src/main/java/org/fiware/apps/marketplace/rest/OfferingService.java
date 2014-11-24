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
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Path("/offering")
public class OfferingService {
	
	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	

	StoreBo storeBo = (StoreBo)appContext.getBean("storeBo");
	ServiceBo serviceBo = (ServiceBo)appContext.getBean("serviceBo");
	LocaluserBo localuserBo = (LocaluserBo)appContext.getBean("localuserBo");

	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String actUser = auth.getName(); 
	
	
	@PUT
	@Consumes({"application/xml", "application/json"})
	@Path("/store/{storeName}/offering")	
	public Response saveService(@PathParam("storeName") String storeName, Service service) throws UserNotFoundException, StoreNotFoundException {	
		//FIXME: Temporal solution. Exception should be caught

		Store store = storeBo.findByName(storeName);
		if (store==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not Found").build());
		}
		service.setRegistrationDate(new Date());
		service.setStore(store);	
		service.setCreator(localuserBo.findByName(actUser));
		service.setLasteditor(localuserBo.findByName(actUser));
		
		serviceBo.save(service);
		return Response.status(Status.CREATED).build();		
	}


	@POST
	@Consumes({"application/xml", "application/json"})
	@Path("/store/{storeName}/offering/{serviceName}")	
	public Response updateService(@PathParam("storeName") String storeName, @PathParam("serviceName") String serviceName, Service service) throws UserNotFoundException {
		//FIXME: Temporal solution. Exception should be caught
		Service serviceDB = serviceBo.findByNameAndStore(serviceName, storeName);		

		serviceDB.setName(service.getName());
		serviceDB.setUrl(service.getUrl());
		serviceDB.setLasteditor(localuserBo.findByName(actUser));
		//TODO: merger in model	
		serviceBo.update(serviceDB);
		return Response.status(Status.OK).build();		
	}

	@DELETE
	@Path("/store/{storeName}/offering/{serviceName}")	
	public Response deleteService(@PathParam("storeName") String storeName, @PathParam("serviceName") String serviceName) {	
		Service service = serviceBo.findByNameAndStore(serviceName, storeName);
		serviceBo.delete(service);
		return Response.status(Status.OK).build();		
	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/store/{storeName}/offering/{serviceName}")	
	public Service findService(@PathParam("storeName") String storeName, @PathParam("serviceName") String serviceName) {		
		Service service = serviceBo.findByNameAndStore(serviceName, storeName);
		if (service==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Service Not Found").build());
		}
		return service;		

	}

	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/store/{storeName}/offerings/")	
	public List<Service> findServices(@PathParam("storeName") String storeName) throws StoreNotFoundException {		
		Store store = storeBo.findByName(storeName);
		if (store==null){
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Service Not Found").build());
		}
		return store.getServices();		

	}


}
