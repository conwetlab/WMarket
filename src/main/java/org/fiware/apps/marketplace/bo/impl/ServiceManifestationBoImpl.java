package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
