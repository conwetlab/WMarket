package org.fiware.apps.marketplace.helpers;

import java.io.IOException;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fiware.apps.marketplace.bo.CategoryBo;
import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.exceptions.ParseException;
import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.PriceComponent;
import org.fiware.apps.marketplace.model.PricePlan;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.rdf.RdfHelper;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class to resolve offerings from store or service instances.
 * 
 * @author D058352
 * @author aitor
 *
 */
@org.springframework.stereotype.Service("offeringResolver")
public class OfferingResolver {
	
	private static Logger logger = LoggerFactory.getLogger(DescriptionBo.class);

	@Autowired private CategoryBo classificationBo;
	@Autowired private ServiceBo serviceBo;

	/**
	 * Returns all the offerings from a USDL
	 * @param model The USDL
	 * @return The offerings list contained in the USDL
	 */
	private List<String> getOfferingUris(RdfHelper rdfHelper) {
		String query = "SELECT ?x WHERE { ?x a usdl:ServiceOffering . } ";
		return rdfHelper.queryUris(query, "x");
	}

	/**
	 * Returns all the services associated with the offering
	 * @param offeringUri The offering whose services want to be retrieved
	 * @param model The UDSL
	 * @return The list of services associated with the offering
	 */
	private List<String> getServiceUris(RdfHelper rdfHelper, String offeringUri) {
		return rdfHelper.getObjectUris(offeringUri, "usdl:includes");
	}

	/**
	 * Returns all the price plans URIs associated with the offering
	 * @param model UDSL
	 * @param offeringUri The offering whose price plans want to be retrieved
	 * @return The list of price plans URIs associated with the offering
	 */
	private List<Map<String, List<Object>>> getPricePlan(RdfHelper rdfHelper, String offeringUri) {
		return rdfHelper.getBlankNodesProperties(offeringUri, "usdl:hasPricePlan");
	}

	/**
	 * Returns all the classifications associated to a service
	 * @param model USDL
	 * @param serviceURI The service whose classifications want to be retrieved
	 * @return The list of classifications associated to the service
	 */
	private List<String> getServiceClassifications(RdfHelper rdfHelper, String serviceURI) {
		return rdfHelper.getBlankNodesLabels(serviceURI, "usdl:hasClassification");
	}

	/**
	 * Returns the title of an entity
	 * @param model USDL
	 * @param entityURI The entity URI whose title wants to be retrieved
	 * @return The title of the entity
	 */
	private String getTitle(RdfHelper rdfHelper, String entityURI) {
		return rdfHelper.getLiteral(entityURI, "dcterms:title");
	}

	/**
	 * Returns the description of an entity
	 * @param model USDL
	 * @param entityURI The entity URI whose description wants to be retrieved
	 * @return The description of the entity
	 */
	private String getDescription(RdfHelper rdfHelper, String entityURI) {
		return rdfHelper.getLiteral(entityURI, "dcterms:description");
	}

	/**
	 * Returns the version of an offering 
	 * @param model USDL
	 * @param offeringUri The offering URI whose version wants to be retrieved
	 * @return The version of the offering
	 */
	private String getOfferingVersion(RdfHelper rdfHelper, String offeringUri) {
		return rdfHelper.getLiteral(offeringUri, "pav:version");
	}

	/**
	 * Returns the image of an offering
	 * @param model USDL
	 * @param offeringUri The offering URI whose image URL wants to be retrieved
	 * @return The image URL of the offering
	 */
	private String getOfferingImageUrl(RdfHelper rdfHelper, String offeringUri) {
		String url = rdfHelper.getObjectUri(offeringUri, "foaf:depiction");
		// Remove '<' from the beginning and '>' from the end
		return url.substring(1, url.length() - 1);
	}

	/**
	 * Returns all offerings contained in the service descriptions in the given list of stores
	 * @param stores The list of stores whose offerings want to be extracted
	 * @return The list of offerings contained in the given list of stores
	 * @throws IOException If one of the models cannot be read
	 * @throws ParseException If one of the models contains errors
	 */
	public List<Offering> resolveOfferingsFromStores(List<Store> stores) throws IOException, ParseException {
		
		List<Offering> offerings = new ArrayList<Offering>();
		for (Store store : stores) {
			offerings.addAll(resolveOfferingsFromStore(store));
		}
		
		return offerings;
	}

	/**
	 * Returns all offerings contained in the service descriptions in the given store.
	 * @param store The store whose offerings want to be extracted
	 * @return All the offerings contained in the given store
	 * @throws IOException If one of the models cannot be read
	 * @throws ParseException If one of the models contains errors
	 */
	public List<Offering> resolveOfferingsFromStore(Store store) throws IOException, ParseException {
		return resolveOfferingsFromServiceDescriptions(store.getDescriptions());
	}

	/**
	 * Returns all offerings contained in the given descriptions.
	 * @param offeringDescriptions The descriptions to be parsed
	 * @return The list of offerings contained in the given descriptions 
	 * @throws ValidationException  If one of the models contains errors
	 * @throws ParseException  If one of the models cannot be read
	 */
	public List<Offering> resolveOfferingsFromServiceDescriptions(List<Description> offeringDescriptions) 
			throws IOException, ParseException {
		
		List<Offering> offerings = new ArrayList<Offering>();
		for (Description service : offeringDescriptions) {
			offerings.addAll(resolveOfferingsFromServiceDescription(service));
		}
		return offerings;
	}
	
	/**
	 * Returns the RdfHelper for the given description
	 * @param description The description whose RdfHelper want to be obtained
	 * @return The RdfHelper for the given description
	 * @throws IOException when the USDL cannot be read
	 */
	RdfHelper getRdfHelper(Description description) throws IOException {
		return new RdfHelper(description.getUrl());
	}

	/**
	 * Returns all offerings contained in the description given
	 * @param description The description that contains the offerings
	 * @return All the offerings contained in the description
	 * @throws ValidationException If there was an error parsing the contained URL
	 * @throws IOException If the model cannot be read
	 */
	public List<Offering> resolveOfferingsFromServiceDescription(Description description) throws IOException,
			ParseException {

		try {

			RdfHelper rdfHelper = getRdfHelper(description);

			List<Offering> offerings = new ArrayList<Offering>();
			List<String> offeringUris = getOfferingUris(rdfHelper);

			// Classifications cache: To avoid SQL Constraint errors when the description contains 
			// two or more offerings with the same classification
			Map<String, Category> createdClassifications = new HashMap<String, Category>();

			// Services cache: To avoid SQL Constraint errors when the description contains
			// two or more offerings with the same service
			Map<String, Service> createdServices = new HashMap<String, Service>();

			for (String offeringUri : offeringUris) {

				Offering offering = new Offering();
				offering.setDisplayName(getTitle(rdfHelper, offeringUri));
				// Maybe the name should depends on the creator and the version...
				offering.setName(NameGenerator.getURLName(offering.getDisplayName()));
				// Remove '<' from the beginning and '>' from the end
				offering.setUri(offeringUri.substring(1, offeringUri.length() - 1));
				offering.setDescribedIn(description);
				offering.setVersion(getOfferingVersion(rdfHelper, offeringUri));
				offering.setDescription(getDescription(rdfHelper, offeringUri));
				offering.setImageUrl(getOfferingImageUrl(rdfHelper, offeringUri));
				// offering.setAcquisitionUrl(description.getStore().getUrl());

				// PRICE PLANS (offerings contain one or more price plans)
				List<Map<String, List<Object>>> rawPricePlans = getPricePlan(rdfHelper, offeringUri);
				Set<PricePlan> pricePlans = new HashSet<>();

				for (Map<String, List<Object>> rawPricePlan: rawPricePlans) {

					PricePlan pricePlan = new PricePlan();
					pricePlan.setTitle((String) rawPricePlan.get("title").get(0));
					List<Object> ppDescriptions = rawPricePlan.get("description");
					String ppDescription = ppDescriptions.size() == 1 ? (String) ppDescriptions.get(0) : "";
					pricePlan.setComment(ppDescription);
					pricePlan.setOffering(offering);

					List<Object> rawPriceComponents = rawPricePlan.get("hasPriceComponent");
					Set<PriceComponent> priceComponents = new HashSet<>();
					
					// There are price plans without price components
					if (rawPriceComponents == null) {
						rawPriceComponents = Collections.emptyList();
					}

					for (Object objectPriceComponent: rawPriceComponents) {

						@SuppressWarnings("unchecked")
						Map<String, List<Object>> rawPriceComponent = (Map<String, List<Object>>) objectPriceComponent;

						PriceComponent priceComponent = new PriceComponent();
						priceComponent.setPricePlan(pricePlan);
						priceComponent.setTitle((String) rawPriceComponent.get("label").get(0));
						List<Object> pcDescriptions = rawPriceComponent.get("description");
						String pcDescription = pcDescriptions.size() == 1 ? (String) pcDescriptions.get(0) : "";
						priceComponent.setComment(pcDescription);

						@SuppressWarnings("unchecked")
						Map<String, List<Object>> rawPriceSpecification = (Map<String, List<Object>>) 
								rawPriceComponent.get("hasPrice").get(0);

						priceComponent.setCurrency((String) rawPriceSpecification.get("hasCurrency").get(0));
						priceComponent.setUnit((String) rawPriceSpecification.get("hasUnitOfMeasurement").get(0));
						priceComponent.setValue(Float.parseFloat(
								(String) rawPriceSpecification.get("hasCurrencyValue").get(0)));

						priceComponents.add(priceComponent);

					}

					// Update price components with the retrieved price components
					pricePlan.setPriceComponents(priceComponents);

					pricePlans.add(pricePlan);
				}

				// Update the price plans set with the retrieved price plans
				offering.setPricePlans(pricePlans);

				// SERVICES
				List<String> servicesUris = getServiceUris(rdfHelper, offeringUri);
				Set<Service> offeringServices = new HashSet<>();
				Set<Category> offeringClassification = new HashSet<>();

				for (String serviceUri: servicesUris) {

					Service service;

					// Remove '<' from the beginning and '>' from the end
					String parserServiceUri = serviceUri.substring(1, serviceUri.length() - 1);

					// Try to get the service from the database
					try {
						service = serviceBo.findByURI(parserServiceUri);
					} catch (ServiceNotFoundException e) {
						// Look for another offering in this description that contains the same service.
						// Otherwise, a new service is created
						service = createdServices.get(parserServiceUri);
					}

					// If service is still null, create a new one
					if (service == null) {
						service = new Service();
					}

					// Service basic properties
					service.setUri(parserServiceUri);
					service.setDisplayName(getTitle(rdfHelper, serviceUri));
					service.setComment(getDescription(rdfHelper, serviceUri));

					// Service classifications (a service can have more than one classification)
					Set<Category> serviceClassifications = new HashSet<>();
					List<String> classificationsDisplayNames = getServiceClassifications(rdfHelper, serviceUri);

					for (String classificationDisplayName: classificationsDisplayNames) {

						Category classification;
						String classificationName = NameGenerator.getURLName(classificationDisplayName);

						// Try to get the service from the database
						try {
							classification = classificationBo.findByName(classificationName);
						} catch (CategoryNotFoundException e1) {
							// Look for another offering/service in this description that contains
							// the same classification. Otherwise, a new classification is created.
							classification = createdClassifications.get(classificationName);
						}

						// If classification is still null, create a new one
						if (classification == null) {
							classification = new Category();
						}

						classification.setName(classificationName);
						classification.setDisplayName(classificationDisplayName);
						createdClassifications.put(classificationName, classification);

						serviceClassifications.add(classification);
					}

					service.setCategories(serviceClassifications);

					createdServices.put(parserServiceUri, service);

					offeringClassification.addAll(service.getCategories());
					offeringServices.add(service);
				}

				// Attach the services to the offering
				offering.setServices(offeringServices);
				offering.setCategories(offeringClassification);

				// Update the list of offerings
				offerings.add(offering);

			}

			return offerings;
			
		} catch (Exception ex) {
			
			// When an exception arises is because the USDL cannot be parsed. In these cases, we throw
			// a new exception that indicates that the USDL is not valid. 
			logger.warn("Unexpected exception parsing USDL", ex);
			throw new ParseException("There was an unexpected error parsing your USDL file.");
		}
	}
}
