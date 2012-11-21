package org.fiware.apps.marketplace.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.rdf.RdfHelper;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Class to resolve offerings from store or service instances.
 * 
 * @author D058352
 *
 */
public abstract class OfferingResolver {
	
	/**
	 * Gets all offerings contained in the service descriptions in the given list of stores.
	 * @param stores
	 * @return
	 */
	public static List<Offering> resolveOfferingsFromStores(List<Store> stores) {
		List<Offering> offerings = new ArrayList<Offering>();
		for (Store store : stores) {
			offerings.addAll(resolveOfferingsFromStore(store));
		}
		return offerings;
	}

	/**
	 * Gets all offerings contained in the service descriptions in the given store.
	 * @param store
	 * @return
	 */
	public static List<Offering> resolveOfferingsFromStore(Store store) {
		return resolveOfferingsFromServiceDescriptions(store.getServices(), store.getUrl());
	}

	/**
	 * Gets all offerings contained in the given service descriptions.
	 * @param services
	 * @return
	 */
	public static List<Offering> resolveOfferingsFromServiceDescriptions(List<Service> services, String storeUrl) {
		List<Offering> offerings = new ArrayList<Offering>();
		for (Service service : services) {
			offerings.addAll(resolveOfferingsFromServiceDescription(service, storeUrl));
		}
		return offerings;
	}

	/**
	 * Gets all offerings contained in the given service description.
	 * @param service
	 * @return
	 */
	public static List<Offering> resolveOfferingsFromServiceDescription(Service service, String storeUrl) {
		return resolveOfferingsFromServiceDescription(service.getUrl(), storeUrl);
	}

	/**
	 * Gets all offerings contained in the file in the given uri.
	 * @param uri
	 * @return
	 */
	public static List<Offering> resolveOfferingsFromServiceDescription(String uri, String storeUrl) {
		Model model = RdfHelper.getModelFromUri(uri);
		if (model == null)
			return Collections.emptyList();

		List<Offering> offerings = new ArrayList<Offering>();
		List<String> offeringUris = getOfferingUris(model);
		for (String offeringUri : offeringUris) {
			Offering offering = new Offering();
			offering.setOfferingUri(offeringUri);
			for (String serviceUri : getServiceUris(offeringUri, model)) {
				offering.addServiceUri(serviceUri);
			}
			for (String pricePlanUri : getPricePlanUris(offeringUri, model)) {
				offering.addPricePlanUri(pricePlanUri);
			}
			offering.setTitle(getOfferingTitle(offeringUri, model));
			offering.setStoreUrl(storeUrl);
			
			if (offering.getPricePlanUris().size() <= 0)
				System.out.println("Offering has no pricePlan: " + offeringUri);
			else if (offering.getServiceUris().size() <= 0)
				System.out.println("Offering has no service: " + offeringUri);
			else
				offerings.add(offering);
		}
		return offerings;
	}

	private static List<String> getOfferingUris(Model model) {
		String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { ?x a usdl:ServiceOffering . } ";
		return RdfHelper.queryUris(query, "x", model);
	}

	private static List<String> getServiceUris(String offeringUri, Model model) {
		String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { <" + offeringUri + "> usdl:includes ?x . } ";
		return RdfHelper.queryUris(query, "x", model);
	}

	private static List<String> getPricePlanUris(String offeringUri, Model model) {
		String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { <" + offeringUri + "> usdl:hasPricePlan ?x . } ";
		return RdfHelper.queryUris(query, "x", model);
	}
	
	private static String getOfferingTitle(String offeringUri, Model model) {
		String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { <" + offeringUri + "> dcterms:title ?x . } ";
		return RdfHelper.queryLiteral(query, "x", model);
	}
}
