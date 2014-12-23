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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.ServiceAttribute;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.ServiceQualitativeAttribute;
import org.fiware.apps.marketplace.model.ServiceQuantitativeAttribute;
import org.fiware.apps.marketplace.rdf.RdfHelper;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.mysql.jdbc.StringUtils;

/**
 * Class to create service manifestations from on offerings.
 * 
 * @author D058352
 * 
 */
public abstract class ServiceManifestationResolver {

	/**
	 * Creates a list of service manifestations from the given collection of offerings
	 * 
	 * @param offerings
	 * @return Returns an empty list if list of offerings is null or empty.
	 */
	public static List<ServiceManifestation> resolveServiceManifestations(Collection<Offering> offerings) {
		if (offerings == null)
			return Collections.emptyList();

		List<ServiceManifestation> serviceManifestations = new ArrayList<ServiceManifestation>();
		for (Offering offering : offerings) {
			serviceManifestations.addAll(resolveServiceManifestations(offering));
		}
		return serviceManifestations;
	}

	/**
	 * Creates a list of service manifestations from the given offering
	 * 
	 * @param offering
	 * @return Returns an empty list if list of offerings is null or empty.
	 */
	public static List<ServiceManifestation> resolveServiceManifestations(Offering offering) {
		if (offering == null)
			return Collections.emptyList();

		Model model = RdfHelper.getModelFromUri(offering.getOfferingUri());
		if (model == null)
			return Collections.emptyList();

		List<ServiceManifestation> serviceManifestations = new ArrayList<ServiceManifestation>();
		for (String pricePlanUri : offering.getPricePlanUris()) {
			String pricePlanTitle = getPricePlanTitle(pricePlanUri, model);
			ServiceManifestation serviceManifestation = new ServiceManifestation();
			serviceManifestation.setOfferingUri(offering.getOfferingUri());
			serviceManifestation.setOfferingTitle(offering.getTitle());
			serviceManifestation.setStoreUrl(offering.getStoreUrl());
			serviceManifestation.setPricePlanUri(pricePlanUri);
			serviceManifestation.setPricePlanTitle(pricePlanTitle);
			serviceManifestation.addPriceComponentUris(getPriceComponentUris(pricePlanUri, model));
			serviceManifestation.addServiceUris(offering.getServiceUris());
			serviceManifestation.addAttributes(getServiceAttributes(serviceManifestation, model));
			serviceManifestation.setName(offering.getTitle() + ", " + pricePlanTitle);
			
			serviceManifestations.add(serviceManifestation);
		}
		return serviceManifestations;
	}

	/**
	 * Gets the title of the given price plan
	 * 
	 * @param pricePlanUri
	 * @param model
	 * @return
	 */
	protected static String getPricePlanTitle(String pricePlanUri, Model model) {
		String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { <" + pricePlanUri + "> dcterms:title ?x . } ";
		return RdfHelper.queryLiteral(query, "x", model);
	}

	/**
	 * Gets a list of all referenced priceComponents in the given price plan as uris.
	 * 
	 * @param pricePlanUri
	 * @param model
	 * @return
	 */
	protected static List<String> getPriceComponentUris(String pricePlanUri, Model model) {
		return RdfHelper.getObjectUris(pricePlanUri, "price:hasPriceComponent", model);
	}

	/**
	 * Returns a list of service attributes as resolved from the given service manifestation.
	 * 
	 * @param serviceManifestation
	 * @param model
	 * @return
	 */
	protected static List<ServiceAttribute> getServiceAttributes(ServiceManifestation serviceManifestation, Model model) {
		List<String> attributeUris = new ArrayList<String>();
		for (String priceComponentUri : serviceManifestation.getPriceComponentUris()) {
			for (String attributeUri : RdfHelper.getObjectUris(priceComponentUri, "price:isLinkedTo", model)) {
				if (!attributeUris.contains(attributeUri))
					attributeUris.add(attributeUri);
			}
		}
		for (String serviceUri : serviceManifestation.getServiceUris()) {
			String query = RdfHelper.queryPrefixes + "SELECT ?x WHERE { { <" + serviceUri
					+ "> gr:quantitativeProductOrServiceProperty ?x } " + "UNION { <" + serviceUri
					+ "> gr:qualitativeProductOrServiceProperty ?x } } ";
			for (String attributeUri : RdfHelper.queryUris(query, "x", model)) {
				if (!attributeUris.contains(attributeUri))
					attributeUris.add(attributeUri);
			}
		}

		List<ServiceAttribute> attributes = new ArrayList<ServiceAttribute>();
		for (String attributeUri : attributeUris) {
			ServiceAttribute attr = createServiceAttribute(attributeUri, model);
			if (attr != null)
				attributes.add(attr);
		}

		return attributes;
	}

	/**
	 * Creates a service attribute from the given attribute uri.
	 * 
	 * @param attributeUri
	 * @param model
	 * @return Returns null if attribute or attribute type uri is null or empty.
	 */
	protected static ServiceAttribute createServiceAttribute(String attributeUri, Model model) {
		if (StringUtils.isNullOrEmpty(attributeUri))
			return null;
		if (null == model)
			return null;

		StringBuilder queryAttributeData = new StringBuilder(RdfHelper.queryPrefixes);
		queryAttributeData.append("SELECT DISTINCT ?type ?label ?value ?min ?max ?unit WHERE { ");
		queryAttributeData.append("<" + attributeUri + "> a ?type . { ");
		queryAttributeData.append("OPTIONAL { <" + attributeUri + "> rdfs:label ?label . } ");
		queryAttributeData.append("OPTIONAL { <" + attributeUri + "> gr:hasValue ?value . } ");
		queryAttributeData.append("OPTIONAL { <" + attributeUri + "> gr:hasMinValue ?min . } ");
		queryAttributeData.append("OPTIONAL { <" + attributeUri + "> gr:hasMaxValue ?max . } ");
		queryAttributeData.append("OPTIONAL { <" + attributeUri + "> gr:hasUnitOfMeasurement ?unit . } } } ");

		List<QuerySolution> solutions = RdfHelper.query(queryAttributeData.toString(), model);
		if (solutions.size() != 1) {
			System.out.println(ServiceManifestationResolver.class.getName() + " - Too many/few query solutions: " + attributeUri);
			return null;
		}

		String typeUri = solutions.get(0).getResource("type") != null ? solutions.get(0).getResource("type").getURI() : null;
		String label = solutions.get(0).getLiteral("label") != null ? solutions.get(0).getLiteral("label").toString() : null;
		String value = solutions.get(0).getLiteral("value") != null ? solutions.get(0).getLiteral("value").toString() : null;
		String min = solutions.get(0).getLiteral("min") != null ? solutions.get(0).getLiteral("min").toString() : null;
		String max = solutions.get(0).getLiteral("max") != null ? solutions.get(0).getLiteral("max").toString() : null;
		String unit = solutions.get(0).getLiteral("unit") != null ? solutions.get(0).getLiteral("unit").toString() : null;

		ServiceAttribute attribute = null;
		if (StringUtils.isNullOrEmpty(typeUri)) {
			return null;
		} else if (!StringUtils.isNullOrEmpty(value) || !StringUtils.isNullOrEmpty(min) || !StringUtils.isNullOrEmpty(max)
				|| !StringUtils.isNullOrEmpty(unit)) {
			attribute = new ServiceQuantitativeAttribute();
			((ServiceQuantitativeAttribute) attribute).setValue(convertStringToDouble(value));
			((ServiceQuantitativeAttribute) attribute).setMinValue(convertStringToDouble(min));
			((ServiceQuantitativeAttribute) attribute).setMaxValue(convertStringToDouble(max));
			((ServiceQuantitativeAttribute) attribute).setUnit(unit);
		} else {
			attribute = new ServiceQualitativeAttribute();
		}
		attribute.setLabel(label);
		attribute.setTypeUri(typeUri);
		attribute.setUri(attributeUri);
		attribute.setReferencedAttributes(getReferencedAttributes(attribute, model));
		return attribute;
	}

	/**
	 * Converts the given string to a double value in respect of MAX_VALUE and MIN_VALUE.
	 * 
	 * @param value
	 * @return
	 */
	protected static Double convertStringToDouble(String value) {
		if (StringUtils.isNullOrEmpty(value))
			return null;

		if (value.toString().equalsIgnoreCase("Double.MAX_VALUE"))
			return Double.MAX_VALUE;

		if (value.toString().equalsIgnoreCase("Double.MIN_VALUE"))
			return Double.MIN_VALUE;

		return Double.parseDouble(value);
	}

	/**
	 * Gets a list of all service attributes which are referenced as gr:valueReferences in the given attribute.
	 * 
	 * @param attribute
	 * @param model
	 * @return
	 */
	protected static List<ServiceAttribute> getReferencedAttributes(ServiceAttribute attribute, Model model) {
		if (attribute == null || model == null)
			return Collections.emptyList();
		if (StringUtils.isNullOrEmpty(attribute.getUri()))
			return Collections.emptyList();

		StringBuilder query = new StringBuilder(RdfHelper.queryPrefixes);
		query.append("SELECT DISTINCT ?ref ?refType ?refLabel ?refValue ?refMinValue ?refMaxValue ?refUnit "
				+ "?nested ?nestedType ?nestedLabel ?nestedValue ?nestedMinValue ?nestedMaxValue ?nestedUnit WHERE { ");
		query.append("<" + attribute.getUri() + "> gr:valueReference ?ref . ?ref a ?refType . ");
		query.append("OPTIONAL { ?ref rdfs:label ?refLabel . } ");
		query.append("OPTIONAL { ?ref gr:hasValue ?refValue . } ");
		query.append("OPTIONAL { ?ref gr:hasMinValue ?refMinValue . } ");
		query.append("OPTIONAL { ?ref gr:hasMaxValue ?refMaxValue . } ");
		query.append("OPTIONAL { ?ref gr:hasUnitOfMeasurement ?refUnit . } ");
		query.append("OPTIONAL { ?ref gr:valueReference ?nested . ?nested a ?nestedType . ");
		query.append("	OPTIONAL { ?nested rdfs:label ?nestedLabel . } ");
		query.append("	OPTIONAL { ?nested gr:hasValue ?nestedValue . } ");
		query.append("	OPTIONAL { ?nested gr:hasMinValue ?nestedMinValue . } ");
		query.append("	OPTIONAL { ?nested gr:hasMaxValue ?nestedMaxValue . } ");
		query.append("	OPTIONAL { ?nested gr:hasUnitOfMeasurement ?nestedUnit . } } } ");

		List<ServiceAttribute> referencedAttributes = new ArrayList<ServiceAttribute>();
		HashMap<String, ServiceAttribute> firstLevelRefs = new HashMap<String, ServiceAttribute>();

		for (QuerySolution solution : RdfHelper.query(query.toString(), model)) {
			String refTypeUri = solution.getResource("refType") != null ? solution.getResource("refType").getURI() : null;
			String refString = solution.getResource("ref") == null ? null : solution.getResource("ref").toString();
			String refLabel = solution.getLiteral("refLabel") == null ? null : solution.getLiteral("refLabel").getLexicalForm();
			String refValue = solution.getLiteral("refValue") == null ? null : solution.getLiteral("refValue").getLexicalForm();
			String refMinValue = solution.getLiteral("refMinValue") == null ? null : solution.getLiteral("refMinValue").getLexicalForm();
			String refMaxValue = solution.getLiteral("refMaxValue") == null ? null : solution.getLiteral("refMaxValue").getLexicalForm();
			String refUnit = solution.getLiteral("refUnit") == null ? null : solution.getLiteral("refUnit").getLexicalForm();

			if (!StringUtils.isNullOrEmpty(refTypeUri) && !StringUtils.isNullOrEmpty(refString)) {
				ServiceAttribute refAttribute = null;
				if (firstLevelRefs.containsKey(refString))
					refAttribute = firstLevelRefs.get(refString);
				else {
					if (!StringUtils.isNullOrEmpty(refValue) || !StringUtils.isNullOrEmpty(refMinValue)
							|| !StringUtils.isNullOrEmpty(refMaxValue) || !StringUtils.isNullOrEmpty(refUnit)) {
						refAttribute = new ServiceQuantitativeAttribute();
						((ServiceQuantitativeAttribute) refAttribute).setValue(convertStringToDouble(refValue));
						((ServiceQuantitativeAttribute) refAttribute).setMinValue(convertStringToDouble(refMinValue));
						((ServiceQuantitativeAttribute) refAttribute).setMaxValue(convertStringToDouble(refMaxValue));
						((ServiceQuantitativeAttribute) refAttribute).setUnit(refUnit);
					} else
						refAttribute = new ServiceQualitativeAttribute();
					referencedAttributes.add(refAttribute);
					firstLevelRefs.put(refString, refAttribute);
				}
				if (refAttribute != null) {
					refAttribute.setTypeUri(refTypeUri);
					refAttribute.setLabel(refLabel);
				}

				String nestedTypeUri = solution.getResource("nestedType") != null ? solution.getResource("nestedType").getURI() : null;
				if (!StringUtils.isNullOrEmpty(nestedTypeUri)) {
					String nestedLabel = solution.getLiteral("nestedLabel") == null ? null : solution.getLiteral("nestedLabel")
							.getLexicalForm();
					String nestedValue = solution.getLiteral("nestedValue") == null ? null : solution.getLiteral("nestedValue")
							.getLexicalForm();
					String nestedMinValue = solution.getLiteral("nestedMinValue") == null ? null : solution.getLiteral("nestedMinValue")
							.getLexicalForm();
					String nestedMaxValue = solution.getLiteral("nestedMaxValue") == null ? null : solution.getLiteral("nestedMaxValue")
							.getLexicalForm();
					String nestedUnit = solution.getLiteral("nestedUnit") == null ? null : solution.getLiteral("nestedUnit")
							.getLexicalForm();

					ServiceAttribute nestedRefAttribute = null;
					if (!StringUtils.isNullOrEmpty(nestedValue) || !StringUtils.isNullOrEmpty(nestedMinValue)
							|| !StringUtils.isNullOrEmpty(nestedMaxValue) || !StringUtils.isNullOrEmpty(nestedUnit)) {
						nestedRefAttribute = new ServiceQuantitativeAttribute();
						((ServiceQuantitativeAttribute) nestedRefAttribute).setValue(convertStringToDouble(nestedValue));
						((ServiceQuantitativeAttribute) nestedRefAttribute).setMinValue(convertStringToDouble(nestedMinValue));
						((ServiceQuantitativeAttribute) nestedRefAttribute).setMaxValue(convertStringToDouble(nestedMaxValue));
						((ServiceQuantitativeAttribute) nestedRefAttribute).setUnit(nestedUnit);
					} else
						nestedRefAttribute = new ServiceQualitativeAttribute();
					if (nestedRefAttribute != null) {
						nestedRefAttribute.setLabel(nestedLabel);
						nestedRefAttribute.setTypeUri(nestedTypeUri);
						refAttribute.addValueReference(nestedRefAttribute);
					}
				}
			}
		}
		if (referencedAttributes.size() <= 0)
			return null;

		return referencedAttributes;
	}
}
