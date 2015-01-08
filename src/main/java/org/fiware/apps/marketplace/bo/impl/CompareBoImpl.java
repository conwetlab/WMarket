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
import java.util.List;

import org.fiware.apps.marketplace.bo.AttributeTypeBo;
import org.fiware.apps.marketplace.bo.AttributeTypeStatisticsBo;
import org.fiware.apps.marketplace.bo.CompareBo;
import org.fiware.apps.marketplace.bo.ServiceManifestationBo;
import org.fiware.apps.marketplace.helpers.ServiceManifestationComparator;
import org.fiware.apps.marketplace.model.ComparisonResult;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import com.mysql.jdbc.StringUtils;

@org.springframework.stereotype.Service("compareBo")
public class CompareBoImpl implements CompareBo {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
	AttributeTypeBo attributeTypeBo = (AttributeTypeBo) appContext.getBean("attributeTypeBo");
	ServiceManifestationBo serviceManifestationBo = (ServiceManifestationBo) appContext.getBean("serviceManifestationBo");
	AttributeTypeStatisticsBo attributeTypeStatisticsBo = (AttributeTypeStatisticsBo) appContext.getBean("attributeTypeStatisticsBo");
			
	@Override
	public ComparisonResult compareService(String sourceIdString) {
		ServiceManifestation source = getServiceManifestationFromString(sourceIdString);
		if (source == null) {
			System.out.println("Error resolving service manifestations: Source could not be resolved (" + sourceIdString + ").");
			return null;
		}

		return ServiceManifestationComparator.compare(source, serviceManifestationBo.getAllServiceManifestations(),
				attributeTypeBo.getAllAttributeTypesAsMap(), attributeTypeStatisticsBo.getAllAttributeTypeStatistics());
	}

	@Override
	public ComparisonResult compareService(String sourceIdString, String targetIdString) {
		ServiceManifestation source = getServiceManifestationFromString(sourceIdString);
		if (source == null) {
			System.out.println("Error resolving service manifestations: Source could not be resolved (" + sourceIdString + ").");
			return null;
		}

		List<ServiceManifestation> targets = null;
		if(targetIdString.contains(",")) {
			targets = new ArrayList<ServiceManifestation>();
			for(String targetId : targetIdString.split(",")) {
				ServiceManifestation target = getServiceManifestationFromString(targetId);
				if (target == null) {
					System.out.println("Error resolving service manifestations: Target could not be resolved (" + targetId + ").");
					return null;
				}
				targets.add(target);
			}
			return ServiceManifestationComparator.compare(source, targets, attributeTypeBo.getAllAttributeTypesAsMap(),
					attributeTypeStatisticsBo.getAllAttributeTypeStatistics());
		} else {
			ServiceManifestation target = getServiceManifestationFromString(targetIdString);
			if (target == null) {
				System.out.println("Error resolving service manifestations: Target could not be resolved (" + targetIdString + ").");
				return null;
			}
			return ServiceManifestationComparator.compare(source, target, attributeTypeBo.getAllAttributeTypesAsMap(),
					attributeTypeStatisticsBo.getAllAttributeTypeStatistics());
		}
	}

	private ServiceManifestation getServiceManifestationFromString(String idString) {
		if (StringUtils.isNullOrEmpty(idString)) {
			System.out.println("Error resolving service manifestation: Id string is null or empty.");
			return null;
		}

		Integer id = null;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException ex) {
			System.out.println("Error resolving service manifestation: Id is not a number.");
			return null;
		}

		return serviceManifestationBo.getServiceManifestationById(id);
	}
}
