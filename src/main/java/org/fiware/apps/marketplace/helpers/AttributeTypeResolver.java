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

import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceNominalAttributeType;
import org.fiware.apps.marketplace.model.ServiceOrdinalAttributeType;
import org.fiware.apps.marketplace.model.ServiceRatioAttributeType;
import org.fiware.apps.marketplace.rdf.RdfHelper;
import org.springframework.stereotype.Service;

/**
 * Class to resolve attribute types in service description or vocabulary files.
 * 
 * @author D058352
 *
 */
@Service("attributeTypeResolver")
public class AttributeTypeResolver {
	
	// FIXME: The code is not tested. It has been adapted to the new RdfHelper implementation
	
	/**
	 * Resolves all service attribute types that are contained under the given uri.
	 * @param uri
	 * @return
	 */
	public List<ServiceAttributeType> resolveAttributeTypesFromUri(String uri) {
				
		try {
			RdfHelper helper = new RdfHelper(uri);
			
			List<ServiceAttributeType> types = new ArrayList<ServiceAttributeType>();
			StringBuilder queryTypes = new StringBuilder();
			queryTypes.append("SELECT DISTINCT ?x WHERE { ");
			queryTypes.append("?x a skos:Concept . ");
			queryTypes.append("{ { ?x rdfs:subClassOf gr:QualitativeValue } UNION { ?x rdfs:subClassOf gr:QuantitativeValue } } } ");
			
			for (String typeUri : helper.queryUris(queryTypes.toString(), "x")) {
				types.add(createAttributeType(typeUri, helper));
			}
			
			return types;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	/**
	 * Creates an instance of ServiceAttributeType from the given typeUri and model.
	 * @param typeUri
	 * @param model
	 * @return Returns null if more than two parent classes have been found or the contained type is unknown. 
	 */
	protected ServiceAttributeType createAttributeType(String typeUri, RdfHelper helper) {
		List<String> baseTypeUris = helper.getObjectUris(typeUri, "rdfs:subClassOf");
		if (baseTypeUris.size() > 2) {
			System.out.println(AttributeTypeResolver.class.getName() + " - Too many base type uris: " + typeUri);
			return null;
		}

		String rightSiblingUri = helper.getObjectUri(typeUri, "genVoc:hasRightSibling");
		String leftSiblingUri = helper.getObjectUri(typeUri, "genVoc:hasLeftSibling");

		ServiceAttributeType attributeType = null;
		if (isRatioAttributeType(baseTypeUris))
			attributeType = createRatioAttributeType();
		else if (isOrdinalAttributeType(baseTypeUris, rightSiblingUri, leftSiblingUri))
			attributeType = createOrdinalAttributeType(rightSiblingUri, leftSiblingUri);
		else if (isNominalAttributeType(baseTypeUris, rightSiblingUri, leftSiblingUri))
			attributeType = createNominalAttributeType();
		else {
			System.out.println(AttributeTypeResolver.class.getName() + " - Basic attribute type could not be determined: " + typeUri);
			return null;
		}

		attributeType.setUri(typeUri);
		attributeType.setPreferedLabel(helper.getLiteral(typeUri, "skos:prefLabel"));
		attributeType.setDescription(helper.getLiteral(typeUri, "dcterms:description"));
		attributeType.setBroaderTypeUri(helper.getObjectUri(typeUri, "skos:broader"));
		for (String narrowerTypeUri : helper.getObjectUris(typeUri, "skos:narrower")) {
			attributeType.addNarrowerTypeUri(narrowerTypeUri);
		}
		for (String baseTypeUri : baseTypeUris) {
			if (!baseTypeUri.equalsIgnoreCase(ServiceNominalAttributeType.baseTypeUri)
					&& !baseTypeUri.equalsIgnoreCase(ServiceOrdinalAttributeType.baseTypeUri)
					&& !baseTypeUri.equalsIgnoreCase(ServiceRatioAttributeType.baseTypeUri))
				attributeType.setBaseTypeUri(baseTypeUri);
		}

		return attributeType;
	}

	protected boolean isRatioAttributeType(List<String> baseTypeUris) {
		return baseTypeUris.contains(ServiceRatioAttributeType.baseTypeUri);
	}

	protected boolean isOrdinalAttributeType(List<String> baseTypeUris, String rightSiblingUri, String leftSiblingUri) {
		return baseTypeUris.contains(ServiceOrdinalAttributeType.baseTypeUri) && (null != rightSiblingUri || null != leftSiblingUri);
	}

	protected boolean isNominalAttributeType(List<String> baseTypeUris, String rightSiblingUri, String leftSiblingUri) {
		return baseTypeUris.contains(ServiceNominalAttributeType.baseTypeUri) && null == rightSiblingUri && null == leftSiblingUri;
	}

	protected ServiceRatioAttributeType createRatioAttributeType() {
		ServiceRatioAttributeType attributeType = new ServiceRatioAttributeType();
		return attributeType;
	}

	protected ServiceOrdinalAttributeType createOrdinalAttributeType(String rightSiblingUri, String leftSiblingUri) {
		ServiceOrdinalAttributeType attributeType = new ServiceOrdinalAttributeType();
		attributeType.setLeftSiblingUri(leftSiblingUri);
		attributeType.setRightSiblingUri(rightSiblingUri);
		return attributeType;
	}

	protected ServiceNominalAttributeType createNominalAttributeType() {
		ServiceNominalAttributeType attributeType = new ServiceNominalAttributeType();
		return attributeType;
	}
}
