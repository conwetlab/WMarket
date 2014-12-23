package org.fiware.apps.marketplace.tests;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.helpers.AttributeTypeResolver;
import org.fiware.apps.marketplace.helpers.AttributeTypeStatisticsResolver;
import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.helpers.ServiceManifestationComparator;
import org.fiware.apps.marketplace.helpers.ServiceManifestationResolver;
import org.fiware.apps.marketplace.model.ComparisonResult;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.ServiceAttribute;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.ServiceOrdinalAttributeType;
import org.fiware.apps.marketplace.model.ServiceQualitativeAttribute;
import org.fiware.apps.marketplace.model.ServiceQuantitativeAttribute;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ServiceManifestationComparatorTest {

	private static final String amazonUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Amazon_EC2_001";
	private static final String rackspaceUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Rackspace_CloudServers_001";
	private static final String elasticHostsUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/ElasticHosts_CloudHosting_001";
	private static final String reliaCloudUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Visi_ReliaCloud_001";
	private static final String cloudVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Cloud_004";
	private static final String supportVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Support_003";
	private static final String osVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_OperatingSystem_003";
	private static final String generalVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_General_001";
	private static final double epsilon = 1.0 / 1000000000.0;

	private static HashMap<String, ServiceAttributeType> typeMap = new HashMap<String, ServiceAttributeType>();
	private static List<ServiceManifestation> serviceManifestationList = new ArrayList<ServiceManifestation>();
	private static HashMap<String, ServiceAttributeTypeStatistics> statisticsMap = new HashMap<String, ServiceAttributeTypeStatistics>();

	private static class ServiceManifestationComparatorAccessor extends ServiceManifestationComparator {
		public static ServiceAttributeType testGetNearestCommonAncestorType(ServiceAttributeType sourceAttributeType,
				ServiceAttributeType targetAttributeType, HashMap<String, ServiceAttributeType> types) {
			return getNearestCommonAncestorType(sourceAttributeType, targetAttributeType, types);
		}
		
		public static List<ServiceAttributeType> testGetAffectedSiblings(ServiceOrdinalAttributeType sourceType,
				ServiceOrdinalAttributeType targetType, HashMap<String, ServiceAttributeType> typeMap) {
			return getAffectedSiblings(sourceType, targetType, typeMap);
		}

		public static double testCalculateAttributeScore(ServiceAttribute source, ServiceAttribute target,
				HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
			return calculateAttributeScore(source, target, typeMap, statisticsMap);
		}

		public static Double testCalculateReferencedAttributesScore(ServiceAttribute source, ServiceAttribute target,
				HashMap<String, ServiceAttributeType> typeMap, HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
			return calculateReferencedAttributesScore(source, target, typeMap, statisticsMap);
		}

		public static Double testCalculateNumericValueScore(ServiceQuantitativeAttribute source, ServiceQuantitativeAttribute target,
				HashMap<String, ServiceAttributeTypeStatistics> statisticsMap) {
			return calculateNumericValueScore(source, target, statisticsMap);
		}
	}
		
	@BeforeClass
	public static void setUpBeforeClass() {
		List<String> uris = new ArrayList<String>();
		uris.add(generalVocUri);
		uris.add(cloudVocUri);
		uris.add(supportVocUri);
		uris.add(osVocUri);
		uris.add(amazonUri);
		uris.add(rackspaceUri);
		uris.add(elasticHostsUri);
		uris.add(reliaCloudUri);

		for (String uri : uris) {
			for (ServiceAttributeType type : AttributeTypeResolver.resolveAttributeTypesFromUri(uri)) {
				typeMap.put(type.getUri(), type);
			}
		}
		
		List<Offering> offerings = new ArrayList<Offering>();
		offerings.addAll(OfferingResolver.resolveOfferingsFromServiceDescription(amazonUri, ""));
		offerings.addAll(OfferingResolver.resolveOfferingsFromServiceDescription(rackspaceUri, ""));
		offerings.addAll(OfferingResolver.resolveOfferingsFromServiceDescription(elasticHostsUri, ""));
		offerings.addAll(OfferingResolver.resolveOfferingsFromServiceDescription(reliaCloudUri, ""));
		for (Offering offering : offerings) {
			serviceManifestationList.addAll(ServiceManifestationResolver.resolveServiceManifestations(offering));
		}

		statisticsMap = AttributeTypeStatisticsResolver.resolveStatistics(typeMap, serviceManifestationList);
	}

	@Test
	@Ignore
	public void testCompare_IdenticalManifestations() {
		ComparisonResult result = ServiceManifestationComparator.compare(serviceManifestationList.get(0), serviceManifestationList.get(0),
				typeMap, statisticsMap);
		assertEquals(1, result.getTargets().size());
		assertEquals(true, Math.abs(result.getTargets().get(0).getTotalScore() - 1.0) < epsilon);
	}

	@Test
	@Ignore
	public void testCompare_UnidenticalManifestations() {
		ComparisonResult result = ServiceManifestationComparator.compare(serviceManifestationList.get(0), serviceManifestationList.get(1),
				typeMap, statisticsMap);
		assertEquals(1, result.getTargets().size());
		assertEquals(true, result.getTargets().get(0).getTotalScore() > epsilon);
	}

	@Test
	@Ignore
	public void testGetNearestCommonAncestor() {
		ServiceAttributeType ancestor1 = ServiceManifestationComparatorAccessor.testGetNearestCommonAncestorType(
				typeMap.get(osVocUri + "#WindowsServer2008R2Standard"), typeMap.get(osVocUri + "#WindowsServer2008R2Enterprise"), typeMap);
		assertNotNull(ancestor1);
		assertEquals(osVocUri + "#WindowsServer2008R2", ancestor1.getUri());

		ServiceAttributeType ancestor2 = ServiceManifestationComparatorAccessor.testGetNearestCommonAncestorType(
				typeMap.get(osVocUri + "#WindowsServer2008R2Standard"), typeMap.get(osVocUri + "#WindowsServer2008SP2Enterprise"), typeMap);
		assertNotNull(ancestor2);
		assertEquals(osVocUri + "#WindowsServer2008", ancestor2.getUri());

		ServiceAttributeType ancestor3 = ServiceManifestationComparatorAccessor.testGetNearestCommonAncestorType(
				typeMap.get(osVocUri + "#WindowsServer2008R2Standard"), typeMap.get(osVocUri + "#WindowsServer2008"), typeMap);
		assertNotNull(ancestor3);
		assertEquals(osVocUri + "#WindowsServer2008", ancestor3.getUri());
	}

	@Test
	@Ignore
	public void testGetAffectedSiblings() {
		ServiceOrdinalAttributeType s1 = (ServiceOrdinalAttributeType) typeMap.get(amazonUri
				+ "#Amazon_Support_ArchitectureSupport_BuildingBlocks");
		ServiceOrdinalAttributeType s2 = (ServiceOrdinalAttributeType) typeMap.get(amazonUri
				+ "#Amazon_Support_ArchitectureSupport_ServiceReviews");
		ServiceOrdinalAttributeType s3 = (ServiceOrdinalAttributeType) typeMap.get(amazonUri
				+ "#Amazon_Support_ArchitectureSupport_UseCaseGuidance");
		ServiceOrdinalAttributeType s4 = (ServiceOrdinalAttributeType) typeMap.get(amazonUri
				+ "#Amazon_Support_ArchitectureSupport_ApplicationArchitecture");

		List<ServiceAttributeType> siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s1, s1, typeMap);
		assertTrue(siblings.contains(s1));
		assertEquals(1, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s1, s2, typeMap);
		assertTrue(siblings.contains(s1));
		assertTrue(siblings.contains(s2));
		assertEquals(2, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s1, s3, typeMap);
		assertTrue(siblings.contains(s1));
		assertTrue(siblings.contains(s2));
		assertTrue(siblings.contains(s3));
		assertEquals(3, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s1, s4, typeMap);
		assertTrue(siblings.contains(s1));
		assertTrue(siblings.contains(s2));
		assertTrue(siblings.contains(s3));
		assertTrue(siblings.contains(s4));
		assertEquals(4, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s4, s4, typeMap);
		assertTrue(siblings.contains(s4));
		assertEquals(1, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s4, s3, typeMap);
		assertTrue(siblings.contains(s4));
		assertTrue(siblings.contains(s3));
		assertEquals(2, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s4, s2, typeMap);
		assertTrue(siblings.contains(s4));
		assertTrue(siblings.contains(s3));
		assertTrue(siblings.contains(s2));
		assertEquals(3, siblings.size());

		siblings = ServiceManifestationComparatorAccessor.testGetAffectedSiblings(s4, s1, typeMap);
		assertTrue(siblings.contains(s4));
		assertTrue(siblings.contains(s3));
		assertTrue(siblings.contains(s2));
		assertTrue(siblings.contains(s1));
		assertEquals(4, siblings.size());
	}

	@Test
	@Ignore
	public void testOrdinalSimilarity() {
		ServiceQualitativeAttribute s1 = new ServiceQualitativeAttribute();
		s1.setTypeUri(amazonUri + "#Amazon_Support_ArchitectureSupport_BuildingBlocks");

		ServiceQualitativeAttribute s2 = new ServiceQualitativeAttribute();
		s2.setTypeUri(cloudVocUri + "#DataCentreTier");
		
		double score = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(s1, s2, typeMap, statisticsMap);
		assertTrue(score == 0.0);
	}

	@Test
	@Ignore
	public void testCalculateValueReferencesScore() {
		ServiceAttribute sourceAttribute = null;
		for (ServiceManifestation serviceManifestation : serviceManifestationList) {
			for (ServiceAttribute attribute : serviceManifestation.getAttributes()) {
				if (attribute.getUri().equals(amazonUri + "#resource_EC2_PublicIP")) {
					sourceAttribute = attribute;
					break;
				}
			}
			if (sourceAttribute != null)
				break;
		}

		ServiceAttribute targetAttribute = null;
		for (ServiceManifestation serviceManifestation : serviceManifestationList) {
			for (ServiceAttribute attribute : serviceManifestation.getAttributes()) {
				if (attribute.getUri().equals(amazonUri + "#resource_EC2_PrivateIP")) {
					targetAttribute = attribute;
					break;
				}
			}
			if (targetAttribute != null)
				break;
		}

		assertNotNull(sourceAttribute);
		assertNotNull(targetAttribute);
		assertTrue(statisticsMap.containsKey(sourceAttribute.getTypeUri()));
		assertTrue(statisticsMap.containsKey(targetAttribute.getTypeUri()));

		Double identicalRefsScore = ServiceManifestationComparatorAccessor.testCalculateReferencedAttributesScore(sourceAttribute, sourceAttribute,
				typeMap, statisticsMap);
		Double unidenticalRefsScore = ServiceManifestationComparatorAccessor.testCalculateReferencedAttributesScore(sourceAttribute, targetAttribute,
				typeMap, statisticsMap);
		assertEquals(true, Math.abs(identicalRefsScore - 1.0) < epsilon);
		assertEquals(true, Math.abs(unidenticalRefsScore - (1.0 / 3.0 * 2.0)) < epsilon);
	}

	@Test
	@Ignore
	public void testRatio() {
		ServiceAttribute ec2Memory = null;
		ServiceAttribute rackspaceMemory1 = null;
		ServiceAttribute rackspaceMemory2 = null;
		for (ServiceManifestation service : serviceManifestationList) {
			for (ServiceAttribute attribute : service.getAttributes()) {
				if (attribute.getUri().equals(amazonUri + "#resource_EC2_Small_MainMemory"))
					ec2Memory = attribute;
				if (attribute.getUri().equals(rackspaceUri + "#resource_CloudServers_Size1_Memory"))
					rackspaceMemory1 = attribute;
				if (attribute.getUri().equals(rackspaceUri + "#resource_CloudServers_Size2_Memory"))
					rackspaceMemory2 = attribute;
			}
		}

		assertNotNull(ec2Memory);
		assertEquals(ServiceQuantitativeAttribute.class, ec2Memory.getClass());
		assertTrue(((ServiceQuantitativeAttribute) ec2Memory).getValue() - 1700.0 < epsilon);
		assertEquals("4L", ((ServiceQuantitativeAttribute) ec2Memory).getUnit());

		assertNotNull(rackspaceMemory1);
		assertEquals(ServiceQuantitativeAttribute.class, rackspaceMemory1.getClass());
		assertTrue(((ServiceQuantitativeAttribute) rackspaceMemory1).getValue() - 256.0 < epsilon);
		assertEquals("4L", ((ServiceQuantitativeAttribute) rackspaceMemory1).getUnit());

		assertNotNull(rackspaceMemory2);
		assertEquals(ServiceQuantitativeAttribute.class, rackspaceMemory2.getClass());
		assertTrue(((ServiceQuantitativeAttribute) rackspaceMemory2).getValue() - 512.0 < epsilon);
		assertEquals("4L", ((ServiceQuantitativeAttribute) rackspaceMemory2).getUnit());

		assertTrue(ServiceManifestationComparatorAccessor.testCalculateAttributeScore(ec2Memory, ec2Memory, typeMap, statisticsMap) - 1.0 < epsilon);

		Double c1 = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(ec2Memory, rackspaceMemory1, typeMap, statisticsMap);
		Double c2 = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(ec2Memory, rackspaceMemory2, typeMap, statisticsMap);
		assertTrue(Math.abs(c1 - 1.0) > epsilon);
		assertTrue(Math.abs(c2 - 1.0) > epsilon);
		assertTrue(c2 > c1);
	}

	@Test
	@Ignore
	public void testNaNError_1() {

		ServiceAttribute sourceAttribute = null;
		for (ServiceManifestation serviceManifestation : serviceManifestationList) {
			for (ServiceAttribute attribute : serviceManifestation.getAttributes()) {
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_Disk_1")) {
					sourceAttribute = attribute;
					break;
				}
			}
			if (sourceAttribute != null)
				break;
		}

		ServiceAttribute targetAttribute = null;
		for (ServiceManifestation serviceManifestation : serviceManifestationList) {
			for (ServiceAttribute attribute : serviceManifestation.getAttributes()) {
				if (attribute.getUri().equals(elasticHostsUri + "#resource_CloudHosting_Disk")) {
					targetAttribute = attribute;
					break;
				}
			}
			if (targetAttribute != null)
				break;
		}

		assertNotNull(sourceAttribute);
		assertNotNull(targetAttribute);
		assertEquals(true, Math.abs(ServiceManifestationComparatorAccessor.testCalculateAttributeScore(sourceAttribute, targetAttribute, typeMap,
				statisticsMap) - 1.0) >= 0.0);
	}

	@Test
	@Ignore
	public void testNaNError_2() {
		// Ratio value comparison of the following attribute returned NaN
		// http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Visi_ReliaCloud_001#resource_CloudServer_CPU_1

		ServiceAttribute cpuVisi = null;
		ServiceAttribute cpuAmazon = null;
		for (ServiceManifestation service : serviceManifestationList) {
			for (ServiceAttribute attribute : service.getAttributes()) {
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_CPU_1"))
					cpuVisi = attribute;
				if (attribute.getUri().equals(amazonUri + "#resource_EC2_Small_ComputeUnit"))
					cpuAmazon = attribute;
			}
		}

		assertNotNull(cpuVisi);
		assertNotNull(cpuAmazon);

		double scoreVisiVisi = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(cpuVisi, cpuVisi, typeMap, statisticsMap);
		assertNotNull(scoreVisiVisi);
		assertFalse(Double.isNaN(scoreVisiVisi));

		double scoreVisiAmazon = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(cpuVisi, cpuAmazon, typeMap, statisticsMap);
		assertNotNull(scoreVisiAmazon);
		assertFalse(Double.isNaN(scoreVisiAmazon));

		double scoreAmazonVisi = ServiceManifestationComparatorAccessor.testCalculateAttributeScore(cpuAmazon, cpuVisi, typeMap, statisticsMap);
		assertNotNull(scoreAmazonVisi);
		assertFalse(Double.isNaN(scoreAmazonVisi));
	}

	@Test
	@Ignore
	public void testCalculateNumericValuesScore() {
		ServiceQuantitativeAttribute disk1 = null;
		ServiceQuantitativeAttribute disk2 = null;
		for (ServiceManifestation service : serviceManifestationList) {
			for (ServiceAttribute attribute : service.getAttributes()) {
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_Disk_1"))
					disk1 = (ServiceQuantitativeAttribute) attribute;
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_Disk_2"))
					disk2 = (ServiceQuantitativeAttribute) attribute;
			}
		}

		assertNotNull(disk1);
		assertNotNull(disk2);

		Double c1 = ServiceManifestationComparatorAccessor.testCalculateNumericValueScore(disk1, disk1, statisticsMap);
		Double c2 = ServiceManifestationComparatorAccessor.testCalculateNumericValueScore(disk1, disk2, statisticsMap);
		assertEquals(true, Math.abs(c1 - 1.0) < epsilon);
		assertTrue(c1 > c2);
		assertTrue(c1 > 0.0);
		assertTrue(c2 > 0.0);
	}

	@Test
	@Ignore
	public void testAttributeComparisonOfIPs() {
		ServiceAttribute ip = null;
		ServiceAttribute optionalIP = null;
		for (ServiceManifestation service : serviceManifestationList) {
			for (ServiceAttribute attribute : service.getAttributes()) {
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_StaticPublicIP"))
					ip = attribute;
				if (attribute.getUri().equals(reliaCloudUri + "#resource_CloudServer_OptionalStaticPublicIP"))
					optionalIP = attribute;
			}
		}
		assertNotNull(ip);
		assertNotNull(optionalIP);
		assertTrue(ServiceManifestationComparatorAccessor.testCalculateAttributeScore(ip, optionalIP, typeMap, statisticsMap) < 1.0);
	}
}
