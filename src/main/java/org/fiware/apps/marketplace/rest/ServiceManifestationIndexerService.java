package org.fiware.apps.marketplace.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.fiware.apps.marketplace.bo.MaintenanceBo;
import org.fiware.apps.marketplace.bo.ServiceManifestationBo;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@Path("/serviceManifestationIndex")
public class ServiceManifestationIndexerService {
	
	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
	ServiceManifestationBo serviceManifestationBo = (ServiceManifestationBo) appContext.getBean("serviceManifestationBo");
	MaintenanceBo maintenanceBo = (MaintenanceBo) appContext.getBean("maintenanceBo");
	
	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/all")
	public List<ServiceManifestation> getAllServiceManifestations() {
		maintenanceBo.initialize();
		return serviceManifestationBo.getAllServiceManifestations();
	}
	
	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/id/{id}")
	public ServiceManifestation getServiceManifestationById(@PathParam("id") String idString) {
		maintenanceBo.initialize();
		Integer id = Integer.parseInt(idString);
		if(id == null)
			return null;
		return serviceManifestationBo.getServiceManifestationById(id);
	}
}
