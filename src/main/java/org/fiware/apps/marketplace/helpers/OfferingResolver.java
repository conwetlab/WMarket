package org.fiware.apps.marketplace.helpers;

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
