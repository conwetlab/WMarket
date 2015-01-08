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

import org.fiware.apps.marketplace.model.ComparisonResult;
import org.fiware.apps.marketplace.model.ServiceAttribute;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.ServiceNominalAttributeType;
import org.fiware.apps.marketplace.model.ServiceOrdinalAttributeType;
import org.fiware.apps.marketplace.model.ServiceQuantitativeAttribute;
import org.fiware.apps.marketplace.model.ServiceRatioAttributeType;

import com.mysql.jdbc.StringUtils;

/**
 * Class to compare service manifestations to rate their similarity. Creates a comparison result containing all necessary information about
 * similarity on service and attribute level.
 * 
 * @author D058352
 * 
 */
public abstract class ServiceManifestationComparator {

	/**
	 * Compares two service manifestations (compares source with target).
	 * 
	 * @param source Service manifestation that is being compared
	 * @param target Service manifestation which is compared to source
	 * @param typeMap Map containing attribute type data
	 * @param statisticsMap Map containing the attribute statistics data
	 * @return Overall and per attribute similarity.
	 */
	public static ComparisonResult compare(ServiceManifestation source, ServiceManifestation target,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (source == null) {
			System.out.println("No comparison source.");
			return null;
		}
		if (target == null) {
			System.out.println("No comparison target.");
			return null;
		}
		if (typeMap == null || typeMap.size() <= 0) {
			System.out.println("No data in attribute type map.");
			return null;
		}
		if (statisticsMap == null || statisticsMap.size() <= 0) {
			System.out.println("No data in statistics map.");
			return null;
		}

		ComparisonResult result = new ComparisonResult(source, typeMap);
		calculateSimilarity(source, target, typeMap, statisticsMap, result);
		return result;
	}

	/**
	 * Compares a service manifestation (source) with a collection of other service manifestations (targets).
	 * 
	 * @param source Service manifestation that is being compared
	 * @param targets Collection of service manifestation which are compared with source
	 * @param typeMap Map containing attribute type data
	 * @param statisticsMap Map containing the attribute statistics data
	 * @return Overall and per attribute similarity for each target.
	 */
	public static ComparisonResult compare(ServiceManifestation source, Collection<ServiceManifestation> targets,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (source == null) {
			System.out.println("No comparison source.");
			return null;
		}
		if (targets == null || targets.size() <= 0) {
			System.out.println("No comparison target(s).");
			return null;
		}
		if (typeMap == null || typeMap.size() <= 0) {
			System.out.println("No data in attribute type map.");
			return null;
		}
		if (statisticsMap == null || statisticsMap.size() <= 0) {
			System.out.println("No data in statistics map.");
			return null;
		}

		ComparisonResult result = new ComparisonResult(source, typeMap);
		for (ServiceManifestation target : targets) {
			if (target != source) {
				calculateSimilarity(source, target, typeMap, statisticsMap, result);
			}
		}
		return result;
	}

	/**
	 * Calculates a number between 0 and 1 stating the similarity of the two service manifestations and includes the data in the result instance.
	 * @param source Service manifestation that is being compared
	 * @param target Service manifestation which is compared to source
	 * @param typeMap
	 * @param statisticsMap
	 * @param result
	 */
	protected static void calculateSimilarity(ServiceManifestation source, ServiceManifestation target,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap,
			ComparisonResult result) {
		if (target == null)
			return;
		if (source.getAttributes() == null || target.getAttributes() == null)
			return;
		if (source.getAttributes().size() <= 0 || target.getAttributes().size() <= 0)
			return;

		double[][] attributeScores = calculateAttributeScores(source.getAttributes(), target.getAttributes(), typeMap, statisticsMap);
		int[] bestAttributeAssignment = AttributeAssignmentResolver.getMaximalAttributeAssignments(attributeScores);

		result.addTarget(target);
		double totalScore = 0.0;
		for (int i = 0; i < bestAttributeAssignment.length; i++) {
			if (bestAttributeAssignment[i] != -1) {
				if (attributeScores[i][bestAttributeAssignment[i]] > 0) {
					totalScore += attributeScores[i][bestAttributeAssignment[i]];
					result.addAttributeToLastTarget(target.getAttributes().get(bestAttributeAssignment[i]), typeMap,
							attributeScores[i][bestAttributeAssignment[i]], i);
				}
			}
		}
		result.addTotalScoreToLastTarget(totalScore / bestAttributeAssignment.length);
	}

	/**
	 * Calculates the attribute similarity scores for the given attributes.
	 * 
	 * @param sourceAttributes List of source attributes that will be analyzed
	 * @param targetAttributes List of target attributes that will be compared with the source attributes
	 * @param typeMap
	 * @param statisticsMap
	 * @return Two-dimensional array with row indices representing source and column indices representing target attributes.
	 */
	protected static double[][] calculateAttributeScores(List<ServiceAttribute> sourceAttributes, List<ServiceAttribute> targetAttributes,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		double[][] scores = new double[sourceAttributes.size()][targetAttributes.size()];
		for (int sourceIndex = 0; sourceIndex < sourceAttributes.size(); sourceIndex++) {
			for (int targetIndex = 0; targetIndex < targetAttributes.size(); targetIndex++) {
				scores[sourceIndex][targetIndex] = calculateAttributeScore(sourceAttributes.get(sourceIndex),
						targetAttributes.get(targetIndex), typeMap, statisticsMap);
			}
		}
		return scores;
	}

	/**
	 * Calculates similarity score of the given two attributes.
	 * 
	 * @param source
	 * @param target
	 * @param typeMap
	 * @param statisticsMap
	 * @return
	 */
	protected static double calculateAttributeScore(ServiceAttribute source, ServiceAttribute target,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (source == null || target == null)
			return 0.0;

		if (!source.getClass().equals(target.getClass()))
			return 0.0;

		ServiceAttributeType sourceType = typeMap.get(source.getTypeUri());
		if (sourceType == null)
			return 0.0;

		ServiceAttributeType targetType = typeMap.get(target.getTypeUri());
		if (targetType == null)
			return 0.0;

		if (!sourceType.getClass().equals(targetType.getClass()))
			return 0.0;

		if (sourceType.getClass().equals(ServiceNominalAttributeType.class))
			return calculateNominalScore(source, sourceType, target, targetType, typeMap, statisticsMap);
		if (sourceType.getClass().equals(ServiceOrdinalAttributeType.class))
			return calculateOrdinalScore(source, sourceType, target, targetType, typeMap, statisticsMap);
		if (sourceType.getClass().equals(ServiceRatioAttributeType.class))
			return calculateRatioScore(source, sourceType, target, targetType, typeMap, statisticsMap);

		System.out
				.println(ServiceManifestationComparator.class.getName() + " - Unknown attribute type: " + sourceType.getClass().getName());
		return 0.0;
	}

	/**
	 * Calculates similarity score for nominal attributes.
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @param typeMap
	 * @param statisticsMap
	 * @return
	 */
	protected static double calculateNominalScore(ServiceAttribute source, ServiceAttributeType sourceType, ServiceAttribute target,
			ServiceAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap,
			HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		Double hierarchicalScore = calculateHierarchicalScore(sourceType, targetType, typeMap, statisticsMap);
		if (hierarchicalScore <= 0.0)
			return 0.0;

		Double referencedValuesScore = calculateReferencedAttributesScore(source, target, typeMap, statisticsMap);
		if (referencedValuesScore == null)
			return hierarchicalScore;

		return hierarchicalScore * 0.5 + referencedValuesScore * 0.5;
	}

	/**
	 * Calculates similarity score for ordinal attributes.
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @param typeMap
	 * @param statisticsMap
	 * @return
	 */
	private static double calculateOrdinalScore(ServiceAttribute source, ServiceAttributeType sourceType, ServiceAttribute target,
			ServiceAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap,
			HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		double siblingScore = calculateSiblingsScore(sourceType, targetType, typeMap, statisticsMap);
		if (siblingScore <= 0.0)
			return 0.0;

		Double referencedValuesScore = calculateReferencedAttributesScore(source, target, typeMap, statisticsMap);
		if (referencedValuesScore == null)
			return siblingScore;

		return siblingScore * 0.5 + referencedValuesScore * 0.5;
	}

	/**
	 * Calculates score for brotherly/sisterly similarity of two ordinal attributes
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param typeMap
	 * @param statisticsMap
	 * @return Returns 1.0 when identical.
	 */
	protected static double calculateSiblingsScore(ServiceAttributeType sourceType, ServiceAttributeType targetType,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (sourceType.equals(targetType))
			return 1.0;

		List<ServiceAttributeType> affectedSiblings = getAffectedSiblings((ServiceOrdinalAttributeType) sourceType,
				(ServiceOrdinalAttributeType) targetType, typeMap);

		double numerator = 0.0;
		for (ServiceAttributeType sibling : affectedSiblings) {
			numerator += statisticsMap.get(sibling.getUri()).getOccurrenceProbability();
		}
		double sourceProbability = statisticsMap.get(sourceType.getUri()).getOccurrenceProbability();
		double targetProbability = statisticsMap.get(targetType.getUri()).getOccurrenceProbability();

		if (sourceProbability + targetProbability == 0.0)
			return 0.0;
		return 2 * Math.log10(numerator) / (Math.log10(sourceProbability) + Math.log10(targetProbability));
	}

	/**
	 * Returns a list of all attributes that are siblings of sourceType and targetType
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param typeMap
	 * @return Returns sourceType when sourceType is targetType. Returns null if sourceType or targetType is null.
	 */
	protected static List<ServiceAttributeType> getAffectedSiblings(ServiceOrdinalAttributeType sourceType,
			ServiceOrdinalAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap) {
		if (sourceType == null || targetType == null)
			return Collections.emptyList();

		List<ServiceAttributeType> siblings = new ArrayList<ServiceAttributeType>();
		siblings.add(sourceType);
		if (sourceType.equals(targetType))
			return siblings;
		siblings.add(targetType);
		siblings.addAll(getAffectedLeftSiblings(sourceType, targetType, typeMap));
		siblings.addAll(getAffectedRightSiblings(sourceType, targetType, typeMap));
		return siblings;
	}

	/**
	 * Gets a list of all siblings left of sourceType until targetType is found or no left siblings are available.
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param typeMap
	 * @return
	 */
	protected static List<ServiceAttributeType> getAffectedLeftSiblings(ServiceOrdinalAttributeType sourceType,
			ServiceOrdinalAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap) {
		List<ServiceAttributeType> siblings = new ArrayList<ServiceAttributeType>();
		String siblingUri = sourceType.getLeftSiblingUri();
		while (!StringUtils.isNullOrEmpty(siblingUri)) {
			if (typeMap.containsKey(siblingUri)) {
				ServiceOrdinalAttributeType sibling = (ServiceOrdinalAttributeType) typeMap.get(siblingUri);
				if (sibling.equals(targetType))
					return siblings;
				siblings.add(sibling);
				siblingUri = sibling.getLeftSiblingUri();
			} else {
				System.out.println(ServiceManifestationComparator.class.getName() + " - Unknown sibling type: " + siblingUri);
				return siblings;
			}
		}
		return siblings;
	}

	/**
	 * Gets a list of all siblings right of sourceType until targetType is found or no right siblings are available.
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param typeMap
	 * @return
	 */
	protected static List<ServiceAttributeType> getAffectedRightSiblings(ServiceOrdinalAttributeType sourceType,
			ServiceOrdinalAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap) {
		List<ServiceAttributeType> siblings = new ArrayList<ServiceAttributeType>();
		String siblingUri = sourceType.getRightSiblingUri();
		while (!StringUtils.isNullOrEmpty(siblingUri)) {
			if (typeMap.containsKey(siblingUri)) {
				ServiceOrdinalAttributeType sibling = (ServiceOrdinalAttributeType) typeMap.get(siblingUri);
				if (sibling.equals(targetType))
					return siblings;
				siblings.add(sibling);
				siblingUri = sibling.getRightSiblingUri();
			} else {
				System.out.println(ServiceManifestationComparator.class.getName() + " - Unknown sibling type: " + siblingUri);
				return siblings;
			}
		}
		return siblings;
	}

	/**
	 * Calculates similarity score for ratio attributes.
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @param typeMap
	 * @param statisticsMap
	 * @return
	 */
	protected static double calculateRatioScore(ServiceAttribute source, ServiceAttributeType sourceType, ServiceAttribute target,
			ServiceAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap,
			HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		Double hierarchicalScore = calculateHierarchicalScore(sourceType, targetType, typeMap, statisticsMap);
		if (hierarchicalScore <= 0.0)
			return 0.0;

		Double referencedValuesScore = calculateReferencedAttributesScore(source, target, typeMap, statisticsMap);
		Double numericValueScore = calculateNumericValueScore(source, target, statisticsMap);

		if (referencedValuesScore == null && numericValueScore == null)
			return hierarchicalScore;
		else if (referencedValuesScore == null)
			return hierarchicalScore * 0.5 + numericValueScore * 0.5;
		else if (numericValueScore == null)
			return hierarchicalScore * 0.5 + referencedValuesScore * 0.5;
		else
			return hierarchicalScore * (1.0 / 3.0) + referencedValuesScore * (1.0 / 3.0) + numericValueScore * (1.0 / 3.0);
	}

	/**
	 * Calculates score for hierarchical similarity of two attribute types. The nearer they are hierarchically, the higher the score
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param typeMap
	 * @param statisticsMap
	 * @return Returns 0.0 if no hierarchical connection exists. Returns 1.0 if they are identical.
	 */
	protected static double calculateHierarchicalScore(ServiceAttributeType sourceType, ServiceAttributeType targetType,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		ServiceAttributeType nearestCommonAncestorType = getNearestCommonAncestorType(sourceType, targetType, typeMap);
		if (nearestCommonAncestorType == null)
			return 0.0;

		double ancestorTypeProbability = statisticsMap.get(nearestCommonAncestorType.getUri()).getOccurrenceProbability();
		double sourceTypeProbability = statisticsMap.get(sourceType.getUri()).getOccurrenceProbability();
		double targetTypeProbability = statisticsMap.get(targetType.getUri()).getOccurrenceProbability();

		if (sourceTypeProbability + targetTypeProbability <= 0.0)
			return 0.0;

		if (ancestorTypeProbability == 1.0 || sourceTypeProbability == 1.0 || targetTypeProbability == 1.0)
			return 1.0;

		return 2 * Math.log10(ancestorTypeProbability) / (Math.log10(sourceTypeProbability) + Math.log10(targetTypeProbability));
	}

	/**
	 * Gets the nearest common ancestor in hierarchical terms of the two given attribute types
	 * 
	 * @param sourceAttributeType
	 * @param targetAttributeType
	 * @param types
	 * @return Returns nearest common ancestor type or null if no common ancestor available.
	 */
	protected static ServiceAttributeType getNearestCommonAncestorType(ServiceAttributeType sourceAttributeType,
			ServiceAttributeType targetAttributeType, HashMap<String, ServiceAttributeType> types) {

		List<ServiceAttributeType> sourceTypesToCheck = new ArrayList<ServiceAttributeType>();
		ServiceAttributeType sourceTypeToCheck = sourceAttributeType;
		while (sourceTypeToCheck != null) {
			sourceTypesToCheck.add(sourceTypeToCheck);
			sourceTypeToCheck = types.get(sourceTypeToCheck.getBroaderTypeUri());
		}

		ServiceAttributeType targetTypeToCheck = targetAttributeType;
		while (targetTypeToCheck != null) {
			if (sourceTypesToCheck.contains(targetTypeToCheck))
				return targetTypeToCheck;
			targetTypeToCheck = types.get(targetTypeToCheck.getBroaderTypeUri());
		}

		return null;
	}

	/**
	 * Calculates similarity score of the referenced attributes of the two given attributes
	 * 
	 * @param source
	 * @param target
	 * @param typeMap
	 * @param statisticsMap
	 * @return Returns null is source has no referenced attributes. Returns 0.0 when no match could be found. Returns 1.0 when every
	 *         referenced attribute is identical (also the nested ones).
	 */
	protected static Double calculateReferencedAttributesScore(ServiceAttribute source, ServiceAttribute target,
			HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (source.getValueReferences() == null || source.getValueReferences().size() <= 0)
			return null;

		if (target.getValueReferences() == null || target.getValueReferences().size() <= 0)
			return 0.0;

		double[][] attributeReferenceScores = calculateAttributeScores(source.getValueReferences(), target.getValueReferences(), typeMap,
				statisticsMap.get(source.getTypeUri()).getTypeStatsMap());
		if (attributeReferenceScores.length == 0)
			return null;

		// resolves the n:m attribute assignment issue
		int[] bestMatch = AttributeAssignmentResolver.getMaximalAttributeAssignments(attributeReferenceScores);

		double score = 0.0;
		for (int i = 0; i < bestMatch.length; i++) {
			if (bestMatch[i] != -1)
				score += attributeReferenceScores[i][bestMatch[i]];
		}
		return score / Math.max(1, source.getValueReferences().size());
	}

	/**
	 * Calculates similarity value of two quantitative attributes
	 * 
	 * @param source
	 * @param target
	 * @param statisticsMap
	 * @return Returns null if source is not quantitative, data is not available or units are not comparable.
	 */
	protected static Double calculateNumericValueScore(ServiceAttribute source, ServiceAttribute target,
			HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (!source.getClass().equals(ServiceQuantitativeAttribute.class))
			return null;

		if (!target.getClass().equals(ServiceQuantitativeAttribute.class))
			return 0.0;

		return calculateNumericValueScore((ServiceQuantitativeAttribute) source, (ServiceQuantitativeAttribute) target, statisticsMap);
	}

	/**
	 * Calculates similarity value of two quantitative attributes
	 * 
	 * @param source
	 * @param target
	 * @param statisticsMap
	 * @return
	 */
	protected static Double calculateNumericValueScore(ServiceQuantitativeAttribute source, ServiceQuantitativeAttribute target,
			HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
		if (!AttributeUnitFactorResolver.unitsComparable(source.getUnit(), target.getUnit()))
			return null;

		Double sourceNormalizationFactor = AttributeUnitFactorResolver.resolveNormalizationFactor(source.getUnit());
		if (sourceNormalizationFactor == null)
			return null;

		Double targetNormalizationFactor = AttributeUnitFactorResolver.resolveNormalizationFactor(target.getUnit());
		if (targetNormalizationFactor == null)
			return null;

		ServiceAttributeTypeStatistics sourceStatistics = statisticsMap.get(source.getTypeUri());
		if (sourceStatistics == null)
			return null;

		ServiceAttributeTypeStatistics targetStatistics = statisticsMap.get(target.getTypeUri());
		if (targetStatistics == null)
			return null;

		Double valueScore = calculateNumericValueScore(source.getValue(), sourceNormalizationFactor, target.getValue(),
				targetNormalizationFactor, sourceStatistics.getMinOfValue(), targetStatistics.getMinOfValue(), 
				sourceStatistics.getMaxOfValue(), targetStatistics.getMaxOfValue());

		Double minValueScore = calculateNumericValueScore(source.getMinValue(), sourceNormalizationFactor, target.getMinValue(),
				targetNormalizationFactor, sourceStatistics.getMinOfMinValue(), targetStatistics.getMinOfMinValue(), 
				sourceStatistics.getMaxOfMinValue(), targetStatistics.getMaxOfMinValue());

		Double maxValueScore = calculateNumericValueScore(source.getMaxValue(), sourceNormalizationFactor, target.getMaxValue(),
				targetNormalizationFactor,sourceStatistics.getMinOfMaxValue(), targetStatistics.getMinOfMaxValue(), 
				sourceStatistics.getMaxOfMaxValue(), targetStatistics.getMaxOfMaxValue());

		if (valueScore == null && minValueScore == null && maxValueScore == null)
			return null;

		double relationFactor = 1.0 / ((valueScore == null ? 0 : 1) + (minValueScore == null ? 0 : 1) + (maxValueScore == null ? 0 : 1));
		return (valueScore == null ? 0 : valueScore * relationFactor) + (minValueScore == null ? 0 : minValueScore * relationFactor)
				+ (maxValueScore == null ? 0 : maxValueScore * relationFactor);
	}

	/**
	 * Calculates the similarity of two given values.
	 * 
	 * @param sourceVal
	 * @param sourceNormalizationFactor Factor which is applied on sourceVal to normalize the unit.
	 * @param targetValue
	 * @param targetNormalizationFactor Factor which is applied on targetVal to normalize the unit.
	 * @param minVal Minimum value of all values to proportion score
	 * @param maxVal Maximum value of all values to proportion score
	 * @return Returns null if sourceVal is null.
	 */
	protected static Double calculateNumericValueScore(Double sourceVal, Double sourceNormalizationFactor, Double targetValue,
			Double targetNormalizationFactor, Double minValSource, Double minValTarget, Double maxValSource, Double maxValTarget) {
		if (sourceVal == null)
			return null;
		if (targetValue == null)
			return 0.0;

		if(minValSource == null && minValTarget == null)
			return null;
		Double minVal = null;
		if(minValSource == null && minValTarget != null)
			minVal = minValTarget;
		else if(minValSource != null && minValTarget == null)
			minVal = minValSource;
		else
			minVal = Math.min(minValSource, minValTarget);
			
		if(maxValSource == null && maxValTarget == null)
			return null;
		Double maxVal = null;
		if(maxValSource == null && maxValTarget != null)
			maxVal = maxValTarget;
		else if(maxValSource != null && maxValTarget == null)
			maxVal = maxValSource;
		else
			maxVal = Math.max(maxValSource, maxValTarget);		
		
		if (maxVal - minVal == 0.0)
			return 1.0;
		
		return 1 - Math.abs((sourceVal * sourceNormalizationFactor - targetValue * targetNormalizationFactor) / (minVal - maxVal));
	}
}
