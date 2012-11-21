package org.fiware.apps.marketplace.bo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.fiware.apps.marketplace.bo.ServiceManifestationBo;
import org.fiware.apps.marketplace.model.ServiceManifestation;

@org.springframework.stereotype.Service("serviceManifestationBo")
public class ServiceManifestationBoImpl implements ServiceManifestationBo {

	// TODO Store data in a database or similar

	private List<ServiceManifestation> serviceManifestations;
	private static final AtomicInteger idCtr = new AtomicInteger(0);

	public ServiceManifestationBoImpl() {
		serviceManifestations = new ArrayList<ServiceManifestation>();
	}

	@Override
	public void save(ServiceManifestation serviceManifestation) {
		if (serviceManifestation == null)
			return;

		for (ServiceManifestation serv : serviceManifestations) {
			if (serv.getPricePlanUri().equals(serviceManifestation.getPricePlanUri())
					&& serv.getOfferingUri().equals(serviceManifestation.getOfferingUri()))
				return;
		}
		serviceManifestation.setId(idCtr.incrementAndGet());
		serviceManifestations.add(serviceManifestation);
	}

	@Override
	public void delete(ServiceManifestation serviceManifestation) {
		if (serviceManifestations.contains(serviceManifestation))
			serviceManifestations.remove(serviceManifestation);
	}

	@Override
	public ServiceManifestation getServiceManifestationById(Integer id) {
		for (ServiceManifestation serv : serviceManifestations) {
			if (serv.getId() == id)
				return serv;
		}
		return null;
	}

	@Override
	public List<ServiceManifestation> getAllServiceManifestations() {
		return serviceManifestations;
	}

	@Override
	public void sort() {
		Collections.sort(serviceManifestations, new Comparator<ServiceManifestation>() {
			public int compare(ServiceManifestation s1, ServiceManifestation s2) {
				return s1.getName().compareTo(s2.getName());
			}
		});
	}
}
