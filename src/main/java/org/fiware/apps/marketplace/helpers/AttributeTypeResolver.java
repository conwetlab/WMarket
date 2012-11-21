package org.fiware.apps.marketplace.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceNominalAttributeType;
import org.fiware.apps.marketplace.model.ServiceOrdinalAttributeType;
import org.fiware.apps.marketplace.model.ServiceRatioAttributeType;
import org.fiware.apps.marketplace.rdf.RdfHelper;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Class to resolve attribute types in service description or vocabulary files.
 * 
 * @author D058352
 *
 */
public abstract class AttributeTypeResolver {

	/**
	 * Resolves all service attribute types that are contained under the given uri.
	 * @param uri
	 * @return
	 */
	public static List<ServiceAttributeType> resolveAttributeTypesFromUri(String uri) {
		Model model = RdfHelper.getModelFromUri(uri);
		if (model == null)
			return Collections.emptyList();

		List<ServiceAttributeType> types = new ArrayList<ServiceAttributeType>();
		StringBuilder queryTypes = new StringBuilder(RdfHelper.queryPrefixes);
		queryTypes.append("SELECT DISTINCT ?x WHERE { ");
		queryTypes.append("?x a skos:Concept . ");
		queryTypes.append("{ { ?x rdfs:subClassOf gr:QualitativeValue } UNION { ?x rdfs:subClassOf gr:QuantitativeValue } } } ");
		for (String typeUri : RdfHelper.queryUris(queryTypes.toString(), "x", model)) {
			types.add(createAttributeType(typeUri, model));
		}
		return types;
	}

	/**
	 * Creates an instance of ServiceAttributeType from the given typeUri and model.
	 * @param typeUri
	 * @param model
	 * @return Returns null if more than two parent classes have been found or the contained type is unknown. 
	 */
	protected static ServiceAttributeType createAttributeType(String typeUri, Model model) {
		List<String> baseTypeUris = RdfHelper.getObjectUris(typeUri, "rdfs:subClassOf", model);
		if (baseTypeUris.size() > 2) {
			System.out.println(AttributeTypeResolver.class.getName() + " - Too many base type uris: " + typeUri);
			return null;
		}

		String rightSiblingUri = RdfHelper.getObjectUri(typeUri, "genVoc:hasRightSibling", model);
		String leftSiblingUri = RdfHelper.getObjectUri(typeUri, "genVoc:hasLeftSibling", model);

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
		attributeType.setPreferedLabel(RdfHelper.getLiteral(typeUri, "skos:prefLabel", model));
		attributeType.setDescription(RdfHelper.getLiteral(typeUri, "dcterms:description", model));
		attributeType.setBroaderTypeUri(RdfHelper.getObjectUri(typeUri, "skos:broader", model));
		for (String narrowerTypeUri : RdfHelper.getObjectUris(typeUri, "skos:narrower", model)) {
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

	protected static boolean isRatioAttributeType(List<String> baseTypeUris) {
		return baseTypeUris.contains(ServiceRatioAttributeType.baseTypeUri);
	}

	protected static boolean isOrdinalAttributeType(List<String> baseTypeUris, String rightSiblingUri, String leftSiblingUri) {
		return baseTypeUris.contains(ServiceOrdinalAttributeType.baseTypeUri) && (null != rightSiblingUri || null != leftSiblingUri);
	}

	protected static boolean isNominalAttributeType(List<String> baseTypeUris, String rightSiblingUri, String leftSiblingUri) {
		return baseTypeUris.contains(ServiceNominalAttributeType.baseTypeUri) && null == rightSiblingUri && null == leftSiblingUri;
	}

	protected static ServiceRatioAttributeType createRatioAttributeType() {
		ServiceRatioAttributeType attributeType = new ServiceRatioAttributeType();
		return attributeType;
	}

	protected static ServiceOrdinalAttributeType createOrdinalAttributeType(String rightSiblingUri, String leftSiblingUri) {
		ServiceOrdinalAttributeType attributeType = new ServiceOrdinalAttributeType();
		attributeType.setLeftSiblingUri(leftSiblingUri);
		attributeType.setRightSiblingUri(rightSiblingUri);
		return attributeType;
	}

	protected static ServiceNominalAttributeType createNominalAttributeType() {
		ServiceNominalAttributeType attributeType = new ServiceNominalAttributeType();
		return attributeType;
	}
}
