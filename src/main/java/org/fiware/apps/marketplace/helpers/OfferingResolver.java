package org.fiware.apps.marketplace.helpers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

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

import com.hp.hpl.jena.shared.JenaException;

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

	@Autowired private CategoryBo categoryBo;
	@Autowired private ServiceBo serviceBo;
	
	// AUXILIAR
	
	/**
	 * Generates the readable string message 
	 * @param rdfHelper
	 * @param offeringUri
	 * @param field
	 * @return
	 */
	private String generateExceptionMessage(RdfHelper rdfHelper, String offeringUri, String field) {
		
		String offeringTitle = cleanRdfUrl(offeringUri);
		try {
			// Try to get title so message will be more readable
			offeringTitle = getTitle(rdfHelper, offeringUri);
		} catch (ParseException ex) {
			// Nothing to do...
		}
		
		return field + " for offering " + offeringTitle + " cannot be retrieved";
		
	}
	
	/**
	 * Given an URL from the RDF, returns the one without the angle brackets
	 * @param url The URL to be cleaned
	 * @return The cleaned URL or an empty string if the URL is null
	 */
	private String cleanRdfUrl(String url) {
		// Remove '<' from the beginning and '>' from the end
		return url != null ? url.substring(1, url.length() - 1) : "";
	}
	
	
	// METHODS TO RETRIEVE ENTITY VALUES

	/**
	 * Returns all the offerings from a USDL
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @return The offerings list contained in the USDL
	 */
	private List<String> getOfferingUris(RdfHelper rdfHelper) {
		String query = "SELECT ?x WHERE { ?x a usdl:ServiceOffering . } ";
		return rdfHelper.queryUris(query, "x");
	}

	/**
	 * Returns all the services associated with the offering
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param offeringUri The offering whose services want to be retrieved
	 * @return The list of services associated with the offering
	 */
	private List<String> getServiceUris(RdfHelper rdfHelper, String offeringUri) {
		return rdfHelper.getObjectUris(offeringUri, "usdl:includes");
	}

	/**
	 * Returns all the price plans URIs associated with the offering
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param offeringUri The offering whose price plans want to be retrieved
	 * @return The list of price plans URIs associated with the offering
	 */
	private List<Map<String, List<Object>>> getPricePlans(RdfHelper rdfHelper, String offeringUri) {
		return rdfHelper.getBlankNodesProperties(offeringUri, "usdl:hasPricePlan");
	}

	/**
	 * Returns all the classifications associated to a service
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param serviceURI The service whose classifications want to be retrieved
	 * @return The list of classifications associated to the service
	 */
	private List<String> getServiceClassifications(RdfHelper rdfHelper, String serviceURI) {
		return rdfHelper.getBlankNodesLabels(serviceURI, "usdl:hasClassification");
	}

	/**
	 * Returns the title of an entity
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param entityUri The entity URI whose title wants to be retrieved
	 * @return The title of the entity
	 * @throws ParseException when the name of the offering cannot be retrieved
	 */
	private String getTitle(RdfHelper rdfHelper, String entityUri) throws ParseException {
		String name = rdfHelper.getLiteral(entityUri, "dcterms:title");
		
		if (name == null || name.isEmpty()) {
			throw new ParseException("Name for entity " + cleanRdfUrl(entityUri) + " cannot be retrieved");
		} else {
			return name;
		}
	}

	/**
	 * Returns the description of an entity
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param entityURI The entity URI whose description wants to be retrieved
	 * @return The description of the entity
	 */
	private String getDescription(RdfHelper rdfHelper, String entityURI) {
		return rdfHelper.getLiteral(entityURI, "dcterms:description");
	}

	/**
	 * Returns the version of an offering 
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param offeringUri The offering URI whose version wants to be retrieved
	 * @return The version of the offering
	 * @throws ParseException When the version cannot be retrieved
	 */
	private String getOfferingVersion(RdfHelper rdfHelper, String offeringUri) throws ParseException {
		String version = rdfHelper.getLiteral(offeringUri, "pav:version");
		
		if (version == null || version.isEmpty()) {
			throw new ParseException(generateExceptionMessage(rdfHelper, offeringUri, "Version"));
		} else {
			return version;
		}
	}

	/**
	 * Returns the image of an offering
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param offeringUri The offering URI whose image URL wants to be retrieved
	 * @return The image URL of the offering
	 * @throws ParseException when the image url cannot be retrieved
	 */
	private String getOfferingImageUrl(RdfHelper rdfHelper, String offeringUri) throws ParseException {
		String imageUrl = cleanRdfUrl(rdfHelper.getObjectUri(offeringUri, "foaf:depiction"));
		
		if (imageUrl == null || imageUrl.isEmpty()) {
			throw new ParseException(generateExceptionMessage(rdfHelper, offeringUri, "Image URL"));
		} else {
			return imageUrl;
		}
	}
	
	/**
	 * Returns the acquisition URL of an offering
	 * @param rdfHelper The helper to parse the description that includes the given offering
	 * @param offeringUri The offering whose acquisition URL wants to be retrieved
	 * @return Returns the acquisition URL of the given offering
	 * @throws ParseException when the acquisition URL cannot be retrieved
	 */
	private String getOfferingAcquisitionUrl(RdfHelper rdfHelper, String offeringUri) throws ParseException {
		String acquisitionUrl =  cleanRdfUrl(rdfHelper.getObjectUri(offeringUri, "gr:availableDeliveryMethods"));
		
		if (acquisitionUrl == null || acquisitionUrl.isEmpty()) {
			throw new ParseException(generateExceptionMessage(rdfHelper, offeringUri, "Acquisition URL"));
		} else {
			return acquisitionUrl;
		}
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
	 * Checks if another service with the same URI exists (in the database or in the ones created when the
	 * description is being analyzed). If so, the existing instance is returned. Otherwise, a new service is created
	 * @param previousServices Map that contains all the services that have been obtained from the same description
	 * @param rdfHelper The rdfHelper that contains the model with the service
	 * @param serviceUri The URI of the service to obtain
	 * @return The service contained in the serviceUri given
	 * @throws ParseException When the service is not valid
	 */
	private Service getService(Map<String, Service> previousServices, RdfHelper rdfHelper, String serviceUri) 
			throws ParseException {

		String parserServiceUri = cleanRdfUrl(serviceUri);
		
		// Obtain the service from the previous ones created for this description
		Service service = previousServices.get(parserServiceUri);
		
		// If it's the first this service is found in this description, the service is obtained from the database.
		// If the service does not exist in the database, a new one is created.
		if (service == null) {
			try {
				service = serviceBo.findByURI(parserServiceUri);
			} catch (ServiceNotFoundException e) {
				service = new Service();
				service.setCategories(new HashSet<Category>());
			}
		}

		// Service URI, display name and comment
		service.setUri(parserServiceUri);
		service.setDisplayName(getTitle(rdfHelper, serviceUri));
		service.setComment(getDescription(rdfHelper, serviceUri));
		
		previousServices.put(parserServiceUri, service);
		
		return service;
	}
	
	/**
	 * Checks if another category with the same name exists (in the database or in the ones created when the
	 * description is being analyzed). If so, the existing instance is returned. Otherwise, a new category is created
	 * and is added to the map of created categories.
	 * @param previousCategories Map that contains all the categories that have been obtained from the same description
	 * @param rdfHelper The rdfHelper that contains the model with the category
	 * @param categoryDisplayName The display name of the category
	 * @return The category contained in the serviceUri given
	 */
	private Category getCategory(Map<String, Category> previousCategories, RdfHelper rdfHelper, 
			String categoryDisplayName) {
		
		String categoryName = NameGenerator.getURLName(categoryDisplayName);
		
		// Obtain the category from the previous ones created for this description
		Category category = previousCategories.get(categoryName);

		// If it's the first this category is found in this description, the category is obtained from the database.
		// If the category does not exist is the database, a new one is created.
		if (category == null) {			
			try {
				category = categoryBo.findByName(categoryName);
			} catch (CategoryNotFoundException e1) {
				category = new Category();
			}
		}
		
		// Set (display) name
		category.setName(categoryName);
		category.setDisplayName(categoryDisplayName);
		previousCategories.put(categoryName, category);

		return category;
	}
	
	/**
	 * Returns all offerings contained in the description given
	 * @param description The description that contains the offerings
	 * @return All the offerings contained in the description
	 * @throws ParseException When the model cannot be parsed for any reason
	 */
	public List<Offering> resolveOfferingsFromServiceDescription(Description description) throws ParseException {

		try {

			RdfHelper rdfHelper = getRdfHelper(description);
			
			List<String> offeringUris = getOfferingUris(rdfHelper);
			List<Offering> offerings = new ArrayList<Offering>();
			
			if (offeringUris == null || offeringUris.size() == 0) {
				throw new ParseException("Offerings URLs cannot be retrieved");
			}

			// Classifications cache: To avoid SQL Constraint errors when the description contains 
			// two or more offerings with the same classification
			Map<String, Category> createdClassifications = new HashMap<String, Category>();

			// Services cache: To avoid SQL Constraint errors when the description contains
			// two or more offerings with the same service
			Map<String, Service> createdServices = new HashMap<String, Service>();

			for (String offeringUri : offeringUris) {

				// Offering basic fields
				Offering offering = new Offering();
				offering.setDisplayName(getTitle(rdfHelper, offeringUri));
				offering.setName(NameGenerator.getURLName(offering.getDisplayName()));
				offering.setUri(cleanRdfUrl(offeringUri));
				offering.setDescribedIn(description);
				offering.setVersion(getOfferingVersion(rdfHelper, offeringUri));
				offering.setDescription(getDescription(rdfHelper, offeringUri));
				offering.setImageUrl(getOfferingImageUrl(rdfHelper, offeringUri));
				offering.setAcquisitionUrl(getOfferingAcquisitionUrl(rdfHelper, offeringUri));
				offering.setServices(new HashSet<Service>());
				offering.setCategories(new HashSet<Category>());

				//////////////////////////////////////////////////////////////
				// PRICE PLANS (offerings contain zero or more price plans) //
				//////////////////////////////////////////////////////////////
				List<Map<String, List<Object>>> rawPricePlans = getPricePlans(rdfHelper, offeringUri);
				Set<PricePlan> pricePlans = new HashSet<>();

				for (Map<String, List<Object>> rawPricePlan: rawPricePlans) {

					PricePlan pricePlan = new PricePlan(rawPricePlan, offering);

					List<Object> rawPriceComponents = rawPricePlan.get("hasPriceComponent");
					
					// There are price plans without price components
					if (rawPriceComponents == null) {
						rawPriceComponents = Collections.emptyList();
					}

					for (Object objectPriceComponent: rawPriceComponents) {
						@SuppressWarnings("unchecked")
						Map<String, List<Object>> rawPriceComponent = (Map<String, List<Object>>) objectPriceComponent;
						pricePlan.getPriceComponents().add(new PriceComponent(rawPriceComponent, pricePlan));
					}

					pricePlans.add(pricePlan);
				}

				// Update the price plans set with the retrieved price plans
				offering.setPricePlans(pricePlans);
				
				/////////////////////////////////////////////////////////
				// SERVICES (offerings contains zero or more services) //
				/////////////////////////////////////////////////////////
				List<String> servicesUris = getServiceUris(rdfHelper, offeringUri);
				for (String serviceUri: servicesUris) {

					Service service = getService(createdServices, rdfHelper, serviceUri);

					// Service classifications (a service can have more than one classification)
					List<String> categoriesDisplayNames = getServiceClassifications(rdfHelper, serviceUri);

					for (String categoryDisplayName: categoriesDisplayNames) {
						service.getCategories().add(getCategory(createdClassifications, rdfHelper, categoryDisplayName));
					}

					// Attach the service and its categories to the offering
					offering.getServices().add(service);
					offering.getCategories().addAll(service.getCategories());
				}

				// Update the list of offerings
				offerings.add(offering);

			}

			return offerings;
			
		} catch (ConnectException ex) {
			throw new ParseException("The host cannot be reached");
		} catch (JenaException ex) {
			throw new ParseException("The file does not contains a valid USDL file");
		} catch(FileNotFoundException ex) {
			throw new ParseException("The file does not exist");
		} catch (ParseException ex) {
			throw ex;
		} catch (Exception ex) {
			
			// When an exception arises is because the USDL cannot be parsed. In these cases, we throw
			// a new exception that indicates that the USDL is not valid. 
			logger.warn("Unexpected exception parsing USDL " + description.getUrl(), ex);
			throw new ParseException("Unknown error parsing your USDL");
		}
	}
}
