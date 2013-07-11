package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.fiware.apps.marketplace.helpers.AttributeTypeResolver;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.fiware.apps.marketplace.model.ServiceNominalAttributeType;
import org.fiware.apps.marketplace.model.ServiceOrdinalAttributeType;
import org.fiware.apps.marketplace.model.ServiceRatioAttributeType;
import org.junit.Test;

public class AttributeTypeResolverTest {

	private static final String amazonUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Amazon_EC2_001";
	private static final String cloudVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Cloud_004";
	private static final String generalVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_General_001";

	@Test
	public void testResolveAttributeTypesFromUri_GeneralVoc() {
		List<ServiceAttributeType> generalVocTypes = AttributeTypeResolver.resolveAttributeTypesFromUri(generalVocUri);
		assertEquals(7, generalVocTypes.size());

		int nominalCtr = 0;
		int ordinalCtr = 0;
		int ratioCtr = 0;
		for (ServiceAttributeType attributeType : generalVocTypes) {
			if (attributeType.getClass().equals(ServiceNominalAttributeType.class)) {
				nominalCtr++;
				assertNotNull(attributeType.getUri());

				if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#RecurringTimePeriod")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Recurring time period", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#AbsolutePointInTime")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals(null, attributeType.getBaseTypeUri());
					assertEquals("Absolute point in time", attributeType.getPreferedLabel());
				} else if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#GenericPointInTime")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Generic point in time", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#GeoReference")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Geo-Reference", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#LanguageReference")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Language", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else
					fail("Unknown nominal attribute type - this point should not be reached. ");

			} else if (attributeType.getClass().equals(ServiceOrdinalAttributeType.class)) {
				ordinalCtr++;
				assertNotNull(attributeType.getUri());

			} else if (attributeType.getClass().equals(ServiceRatioAttributeType.class)) {
				ratioCtr++;
				assertNotNull(attributeType.getUri());

				if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#AbsoluteTimePeriod")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Time period", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else if (attributeType.getUri().equalsIgnoreCase(generalVocUri + "#Quantity")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals("Quantity", attributeType.getPreferedLabel());
					assertEquals(null, attributeType.getBaseTypeUri());
				} else
					fail("Unknown ratio attribute type - this point should not be reached. ");

			} else
				fail("Unknown type category - this point should not be reached.");
		}
		assertEquals(5, nominalCtr);
		assertEquals(0, ordinalCtr);
		assertEquals(2, ratioCtr);
	}

	@Test
	public void testResolveAttributeTypesFromUri_CloudVoc() {
		List<ServiceAttributeType> generalVocTypes = AttributeTypeResolver.resolveAttributeTypesFromUri(cloudVocUri);
		assertEquals(36, generalVocTypes.size());

		int nominalCtr = 0;
		int ordinalCtr = 0;
		int ratioCtr = 0;
		for (ServiceAttributeType attributeType : generalVocTypes) {
			if (attributeType.getClass().equals(ServiceNominalAttributeType.class)) {
				nominalCtr++;
				assertNotNull(attributeType.getUri());

				if (attributeType.getUri().equalsIgnoreCase(cloudVocUri + "#CreditPeriodStart")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(2, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals(generalVocUri + "#GenericPointInTime", attributeType.getBaseTypeUri());
				}
			} else if (attributeType.getClass().equals(ServiceOrdinalAttributeType.class)) {
				ordinalCtr++;
				assertNotNull(attributeType.getUri());

				if (attributeType.getUri().equalsIgnoreCase(cloudVocUri + "#CreditPeriodStart")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals(null, attributeType.getBaseTypeUri());
					assertEquals(cloudVocUri + "#DataCentreTier1", ((ServiceOrdinalAttributeType) attributeType).getLeftSiblingUri());
					assertEquals(cloudVocUri + "#DataCentreTier3", ((ServiceOrdinalAttributeType) attributeType).getRightSiblingUri());
				}
			} else if (attributeType.getClass().equals(ServiceRatioAttributeType.class)) {
				ratioCtr++;
				assertNotNull(attributeType.getUri());

				if (attributeType.getUri().equalsIgnoreCase(cloudVocUri + "#EndOfDowntime")) {
					assertNotNull(attributeType.getBroaderTypeUri());
					assertEquals(cloudVocUri + "#CreditPeriodEnd", attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(0, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals(generalVocUri + "#GenericPointInTime", attributeType.getBaseTypeUri());
				} else if (attributeType.getUri().equalsIgnoreCase(cloudVocUri + "#DiskStorage")) {
					assertNull(attributeType.getBroaderTypeUri());
					assertNotNull(attributeType.getNarrowerTypeUris());
					assertEquals(2, attributeType.getNarrowerTypeUris().size());
					assertNull(attributeType.getDescription());
					assertEquals(null, attributeType.getBaseTypeUri());
				}
			} else
				fail("Unknown type category - this point should not be reached.");
		}
		assertEquals(17, nominalCtr);
		assertEquals(4, ordinalCtr);
		assertEquals(15, ratioCtr);
	}

	@Test
	public void testResolveOrdinalValue() {
		List<ServiceAttributeType> types = AttributeTypeResolver.resolveAttributeTypesFromUri(amazonUri);
		assertEquals(20, types.size());
		for (ServiceAttributeType type : types) {
			if (type.getUri().equals(amazonUri + "#Amazon_Support_ArchitectureSupport_ServiceReviews")) {
				assertEquals(ServiceOrdinalAttributeType.class, type.getClass());
				assertEquals(amazonUri + "#Amazon_Support_ArchitectureSupport_BuildingBlocks",
						((ServiceOrdinalAttributeType) type).getLeftSiblingUri());
				assertEquals(amazonUri + "#Amazon_Support_ArchitectureSupport_UseCaseGuidance",
						((ServiceOrdinalAttributeType) type).getRightSiblingUri());
			}
		}
	}
}
