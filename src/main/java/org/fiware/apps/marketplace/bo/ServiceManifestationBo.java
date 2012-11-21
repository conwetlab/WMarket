package org.fiware.apps.marketplace.bo;

import java.util.List;

import org.fiware.apps.marketplace.model.ServiceManifestation;

public interface ServiceManifestationBo {
	public void save(ServiceManifestation serviceManifestation);
	public void delete(ServiceManifestation serviceManifestation);
	public void sort();
	public ServiceManifestation getServiceManifestationById(Integer id);
	public List<ServiceManifestation> getAllServiceManifestations();
}
