package org.fiware.apps.marketplace.bo.impl;

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
import org.fiware.apps.marketplace.util.ApplicationContextProvider;
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
			for (Store store : storeBo.findStores()) {
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
			for (Offering offering : OfferingResolver.resolveOfferingsFromStores(storeBo.findStores())) {
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
