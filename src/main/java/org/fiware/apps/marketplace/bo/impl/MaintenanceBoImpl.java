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

import java.util.HashMap;

import org.fiware.apps.marketplace.bo.AttributeTypeBo;
import org.fiware.apps.marketplace.bo.AttributeTypeStatisticsBo;
import org.fiware.apps.marketplace.bo.MaintenanceBo;
import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.bo.ServiceManifestationBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.bo.VocabularyBo;
import org.fiware.apps.marketplace.helpers.AttributeTypeResolver;
import org.fiware.apps.marketplace.helpers.AttributeTypeStatisticsResolver;
import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.helpers.ServiceManifestationResolver;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

@org.springframework.stereotype.Service("maintenanceBo")
public class MaintenanceBoImpl implements MaintenanceBo {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();

	StoreBo storeBo = (StoreBo) appContext.getBean("storeBo");
	VocabularyBo vocabularyBo = (VocabularyBo) appContext.getBean("vocabularyBo");
	AttributeTypeBo attributeTypeBo = (AttributeTypeBo) appContext.getBean("attributeTypeBo");
	OfferingBo offeringBo = (OfferingBo) appContext.getBean("offeringBo");
	ServiceManifestationBo serviceManifestationBo = (ServiceManifestationBo) appContext.getBean("serviceManifestationBo");
	AttributeTypeStatisticsBo attributeTypeStatisticsBo = (AttributeTypeStatisticsBo) appContext.getBean("attributeTypeStatisticsBo");

	private boolean comparisonInitializationDone = false;
	
	@Override
	public synchronized void initialize() {
		if(isInitializationDone())
			return;
		
		comparisonInitializationDone = initializeComparisonData();
		if(!comparisonInitializationDone)
			System.out.println("Initialization of comparison data failed.");
	}

	@Override
	public boolean isInitializationDone() {
		return comparisonInitializationDone;
	}	
	
	private boolean initializeComparisonData() {
		if(!initializeAttributeTypes())
			return false;
		if(!initializeOfferings())
			return false;
		if(!initializeServiceManifestations())
			return false;
		if(!initializeServiceAttributeTypeStatistics())
			return false;
		return true;
	}

	private boolean initializeAttributeTypes() {
		try {
			long start = System.currentTimeMillis();
			for (String vocUri : vocabularyBo.getVocabularyUris()) {
				for (ServiceAttributeType attributeType : AttributeTypeResolver.resolveAttributeTypesFromUri(vocUri)) {
					attributeTypeBo.save(attributeType);
				}
			}
			for (Store store : storeBo.getAllStores()) {
				for (Service service : store.getServices()) {
					for (ServiceAttributeType attributeType : AttributeTypeResolver.resolveAttributeTypesFromUri(service.getUrl())) {
						attributeTypeBo.save(attributeType);
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("Resolved attribute types (" + (end - start) + "ms).");
			return true;
		} catch (Exception ex) {
			System.out.println("Error resolving attribute types. " + ex.getMessage());
			return false;
		}
	}

	private boolean initializeOfferings() {
		try {
			long start = System.currentTimeMillis();
			for (Offering offering : OfferingResolver.resolveOfferingsFromStores(storeBo.getAllStores())) {
				offeringBo.save(offering);
			}
			long end = System.currentTimeMillis();
			System.out.println("Resolved offerings (" + (end - start) + "ms).");
			return true;
		} catch (Exception ex) {
			System.out.println("Error resolving offerings. " + ex.getMessage());
			return false;
		}
	}
	
	private boolean initializeServiceManifestations() {
		try {
			long start = System.currentTimeMillis();
			for (ServiceManifestation serviceManifestation : ServiceManifestationResolver.resolveServiceManifestations(offeringBo
					.getAllOfferings())) {
				serviceManifestationBo.save(serviceManifestation);
			}
			serviceManifestationBo.sort();
			long end = System.currentTimeMillis();
			System.out.println("Resolved service manifestations (" + (end - start) + "ms).");
			return true;
		} catch (Exception ex) {
			System.out.println("Error resolving service manifestations. " + ex.getMessage());
			return false;
		}
	}
	
	private boolean initializeServiceAttributeTypeStatistics() {
		try {
			long start = System.currentTimeMillis();
			HashMap<String, ServiceAttributeTypeStatistics> statsMap = AttributeTypeStatisticsResolver.resolveStatistics(
					attributeTypeBo.getAllAttributeTypesAsMap(), serviceManifestationBo.getAllServiceManifestations());
			for (String key : statsMap.keySet()) {
				attributeTypeStatisticsBo.save(key, statsMap.get(key));
			}
			long end = System.currentTimeMillis();
			System.out.println("Resolved attribute type statistics (" + (end - start) + "ms).");
			return true;
		} catch (Exception ex) {
			System.out.println("Error resolving attribute type statistics. " + ex.getMessage());
			return false;
		}
	}
}
