package org.fiware.apps.marketplace.helpers;

import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.model.ServiceAttribute;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.ServiceQuantitativeAttribute;

import com.mysql.jdbc.StringUtils;

/**
 * Class to resolve statistics of attribute types including probability of occurrence and min/max values of specified (min-)/(max-)values.
 * These statistics are necessary to calculate service/attribute similarities. 
 * 
 * @author D058352
 * 
 */
public abstract class AttributeTypeStatisticsResolver {

	/**
	 * Creates a HashMap of statistics as resolved from the given types and service manifestations. This method includes the also increases
	 * the probabilities of broader types when a narrower type was found.
	 * 
	 * @param typeMap
	 * @param serviceManifestations
	 * @return Returns a HashMap with type uris as keys. Returns null if typMap or serviceManifestations are null or empty.
	 */
	public static HashMap<String, ServiceAttributeTypeStatistics> resolveStatistics(HashMap<String, ServiceAttributeType> typeMap,
			List<ServiceManifestation> serviceManifestations) {
		if (typeMap == null) {
			System.out.println(AttributeTypeStatisticsResolver.class.getName() + " - Type map is null.");
			return null;
		}
		if (typeMap.keySet().size() <= 0) {
			System.out.println(AttributeTypeStatisticsResolver.class.getName() + " - Type map is empty.");
			return null;
		}
		if (serviceManifestations == null) {
			System.out.println(AttributeTypeStatisticsResolver.class.getName() + " - List of service manifestations is null.");
			return null;
		}
		if (serviceManifestations.size() <= 0) {
			System.out.println(AttributeTypeStatisticsResolver.class.getName() + " - List of service manifestations is empty.");
			return null;
		}

		HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap = new HashMap<String, ServiceAttributeTypeStatistics>();
		for (String typeUri : typeMap.keySet()) {
			ServiceAttributeTypeStatistics stats = new ServiceAttributeTypeStatistics();
			stats.setUri(typeUri);
			typeStatsMap.put(typeUri, stats);
		}

		for (ServiceManifestation serviceManifestation : serviceManifestations) {
			getStatisticsFromAttributeList(serviceManifestation.getAttributes(), typeStatsMap, typeMap);
		}
		normalizeProbabilities(typeStatsMap);
		return typeStatsMap;
	}

	/**
	 * Includes the data of all given attributes in the given typeStatsMap.
	 * @param attributes
	 * @param typeStatsMap
	 * @param typeMap
	 */
	protected static void getStatisticsFromAttributeList(List<ServiceAttribute> attributes,
			HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap, HashMap<String, ServiceAttributeType> typeMap) {
		if (attributes == null)
			return;
		for (ServiceAttribute attribute : attributes) {
			ServiceAttributeTypeStatistics stats = getStatisticsContainer(typeStatsMap, attribute.getTypeUri());
			stats.setOccurrences(stats.getOccurrences() + 1);
			getValuesFromAttribute(attribute, stats);
			incrementBroaderTypes(typeStatsMap, attribute.getTypeUri(), typeMap);
			getStatisticsFromAttributeList(attribute.getValueReferences(), typeStatsMap.get(attribute.getTypeUri()).getTypeStatsMap(),
					typeMap);
		}
	}

	/**
	 * Creates a new instance of ServiceAttributeTypeStatistics if necessary and puts it in typStatsMap.
	 * 
	 * @param typeStatsMap
	 * @param typeUri
	 * @return
	 */
	protected static ServiceAttributeTypeStatistics getStatisticsContainer(HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap,
			String typeUri) {
		if (!typeStatsMap.containsKey(typeUri)) {
			ServiceAttributeTypeStatistics stats = new ServiceAttributeTypeStatistics();
			stats.setUri(typeUri);
			typeStatsMap.put(typeUri, stats);
			return stats;
		}
		return typeStatsMap.get(typeUri);
	}

	/**
	 * Includes the data of the given attribute in the given stats container.
	 * @param attribute
	 * @param stats
	 */
	protected static void getValuesFromAttribute(ServiceAttribute attribute, ServiceAttributeTypeStatistics stats) {
		if (!attribute.getClass().equals(ServiceQuantitativeAttribute.class))
			return;

		Double normalizationFactor = AttributeUnitFactorResolver.resolveNormalizationFactor(((ServiceQuantitativeAttribute) attribute)
				.getUnit());
		if (normalizationFactor == null)
			return;

		// Note: If an uncompatible unit is used (e.g. byte instead of kg), the values will be falsified!

		Double value = ((ServiceQuantitativeAttribute) attribute).getValue();
		if (value != null) {
			value = value * normalizationFactor;
			if (stats.getMinOfValue() == null || value < stats.getMinOfValue())
				stats.setMinOfValue(value);
			if (stats.getMaxOfValue() == null || value > stats.getMaxOfValue())
				stats.setMaxOfValue(value);
		}

		Double minValue = ((ServiceQuantitativeAttribute) attribute).getMinValue();
		if (minValue != null) {
			minValue = minValue * normalizationFactor;
			if (stats.getMinOfMinValue() == null || minValue < stats.getMinOfMinValue())
				stats.setMinOfMinValue(minValue);
			if (stats.getMaxOfMinValue() == null || minValue > stats.getMaxOfMinValue())
				stats.setMaxOfMinValue(minValue);
		}

		Double maxValue = ((ServiceQuantitativeAttribute) attribute).getMaxValue();
		if (maxValue != null) {
			maxValue = maxValue * normalizationFactor;
			if (stats.getMinOfMaxValue() == null || maxValue < stats.getMinOfMaxValue())
				stats.setMinOfMaxValue(maxValue);
			if (stats.getMaxOfMaxValue() == null || maxValue > stats.getMaxOfMaxValue())
				stats.setMaxOfMaxValue(maxValue);
		}
	}

	/**
	 * Increments the occurrence count for all broader types.
	 * @param typeStatsMap
	 * @param typeUri
	 * @param typeMap
	 */
	protected static void incrementBroaderTypes(HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap, String typeUri,
			HashMap<String, ServiceAttributeType> typeMap) {
		if (StringUtils.isNullOrEmpty(typeUri))
			return;

		String broaderTypeUri = typeMap.get(typeUri).getBroaderTypeUri();
		if (broaderTypeUri == null)
			return;

		ServiceAttributeTypeStatistics stats = getStatisticsContainer(typeStatsMap, broaderTypeUri);
		stats.setOccurrences(stats.getOccurrences() + 1);
		incrementBroaderTypes(typeStatsMap, broaderTypeUri, typeMap);
	}

	/**
	 * Normalizes the occurrence probability to a value between 0 and 1.
	 * 
	 * @param typeStatsMap
	 */
	protected static void normalizeProbabilities(HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap) {
		double divisor = 0.0;
		for (String key : typeStatsMap.keySet()) {
			divisor += typeStatsMap.get(key).getOccurrences();
		}
		for (String key : typeStatsMap.keySet()) {
			typeStatsMap.get(key).setOccurrenceProbability(typeStatsMap.get(key).getOccurrences() / divisor);
			normalizeProbabilities(typeStatsMap.get(key).getTypeStatsMap());
		}
	}
}
