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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.helpers.ServiceManifestationResolver;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.ServiceAttribute;
import org.fiware.apps.marketplace.model.ServiceManifestation;
import org.fiware.apps.marketplace.model.ServiceQualitativeAttribute;
import org.fiware.apps.marketplace.model.ServiceQuantitativeAttribute;
import org.fiware.apps.marketplace.rdf.RdfHelper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class ServiceManifestationResolverTest {

	/*private static Model model;
	private static final String baseUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Amazon_EC2_001";
	private static final String cloudVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Cloud_004";
	private static final String supportVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Support_003";
	private static final String generalVocUri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_General_001";

	private static class ServiceManifestationResolverAccessor extends ServiceManifestationResolver {
		public static ServiceAttribute testCreateServiceAttribute(String attributeUri, Model model) {
			return createServiceAttribute(attributeUri, model);
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		model = RdfHelper.getModelFromUri(baseUri);
	}

	@Test
	@Ignore
	public void testResolveServiceManifestations() {
		Offering offering = new Offering();
		offering.setOfferingUri(baseUri + "#offering_Support_Premium");
		offering.addPricePlanUri(baseUri + "#pricing_Support_Premium");
		offering.addServiceUri(baseUri + "#service_Support_Premium");

		List<ServiceManifestation> serviceManifestations = ServiceManifestationResolver.resolveServiceManifestations(offering);
		assertNotNull(serviceManifestations);
		assertEquals(1, serviceManifestations.size());
		assertEquals(baseUri + "#pricing_Support_Premium", serviceManifestations.get(0).getPricePlanUri());
		assertNotNull(serviceManifestations.get(0).getServiceUris());
		assertEquals(1, serviceManifestations.get(0).getServiceUris().size());
		assertEquals(baseUri + "#service_Support_Premium", serviceManifestations.get(0).getServiceUris().get(0));
		assertEquals(1, serviceManifestations.get(0).getPriceComponentUris().size());
		assertEquals(baseUri + "#priceComponent_Support_Premium", serviceManifestations.get(0).getPriceComponentUris().get(0));
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_Basic1() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_EC2_Small_Storage", model);

		assertEquals(baseUri + "#resource_EC2_Small_Storage", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(cloudVocUri + "#PersistentDiskStorage", attr.getTypeUri());
		assertEquals(null, attr.getLabel());
		assertEquals(ServiceQuantitativeAttribute.class, attr.getClass());
		assertEquals("E34", ((ServiceQuantitativeAttribute) attr).getUnit());
		assertNotNull(((ServiceQuantitativeAttribute) attr).getValue());
		assertEquals(0, ((ServiceQuantitativeAttribute) attr).getValue().compareTo(160.0));
		assertNull(((ServiceQuantitativeAttribute) attr).getMinValue());
		assertNull(((ServiceQuantitativeAttribute) attr).getMaxValue());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_Basic2() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_EC2_Small_ComputeUnit", model);

		assertEquals(baseUri + "#resource_EC2_Small_ComputeUnit", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(cloudVocUri + "#CPU", attr.getTypeUri());
		assertEquals(null, attr.getLabel());
		assertEquals(ServiceQuantitativeAttribute.class, attr.getClass());
		assertEquals("A86", ((ServiceQuantitativeAttribute) attr).getUnit());
		assertNull(((ServiceQuantitativeAttribute) attr).getValue());
		assertNotNull(((ServiceQuantitativeAttribute) attr).getMinValue());
		assertEquals(0, ((ServiceQuantitativeAttribute) attr).getMinValue().compareTo(1.0));
		assertNotNull(((ServiceQuantitativeAttribute) attr).getMaxValue());
		assertEquals(0, ((ServiceQuantitativeAttribute) attr).getMaxValue().compareTo(1.2));
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_Basic3() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_EC2_Small_IO", model);

		assertEquals(baseUri + "#resource_EC2_Small_IO", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(baseUri + "#Amazon_EC2_IOPerformance", attr.getTypeUri());
		assertEquals(ServiceQualitativeAttribute.class, attr.getClass());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_Basic4() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_EC2_DataCentre_EU", model);

		assertEquals(baseUri + "#resource_EC2_DataCentre_EU", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(cloudVocUri + "#DataCentre", attr.getTypeUri());
		assertEquals("Dublin, Ireland", attr.getLabel());
		assertEquals(ServiceQualitativeAttribute.class, attr.getClass());
		// gn:locatedIn <http://sws.geonames.org/5025219> .
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_Basic5() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_Support_Gold_TechSupport", model);

		assertEquals(baseUri + "#resource_Support_Gold_TechSupport", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(supportVocUri + "#TechSupport", attr.getTypeUri());
		assertNull(attr.getLabel());
		assertEquals(ServiceQualitativeAttribute.class, attr.getClass());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_MaxDouble() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_Support_Premium_NamedContacts",
				model);

		assertEquals(baseUri + "#resource_Support_Premium_NamedContacts", attr.getUri());
		assertNotNull(attr.getTypeUri());
		assertEquals(baseUri + "#Amazon_Support_NamedContacts", attr.getTypeUri());
		assertNull(attr.getLabel());
		assertEquals(ServiceQuantitativeAttribute.class, attr.getClass());
		assertEquals("C62", ((ServiceQuantitativeAttribute) attr).getUnit());
		assertNotNull(((ServiceQuantitativeAttribute) attr).getValue());
		assertEquals(0, ((ServiceQuantitativeAttribute) attr).getValue().compareTo(Double.MAX_VALUE));
		assertNull(((ServiceQuantitativeAttribute) attr).getMinValue());
		assertNull(((ServiceQuantitativeAttribute) attr).getMaxValue());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_ReferencedAttributes1() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_EC2_Small_ComputeUnit", model);

		assertNotNull(attr.getValueReferences());
		assertEquals(1, attr.getValueReferences().size());
		assertEquals(cloudVocUri + "#NumberOfCores", attr.getValueReferences().get(0).getTypeUri());
		assertEquals(ServiceQuantitativeAttribute.class, attr.getValueReferences().get(0).getClass());
		assertNotNull(((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getUnit());
		assertNull(((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getMinValue());
		assertNull(((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getMaxValue());
		assertNotNull(((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getValue());
		assertEquals(0, ((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getValue().compareTo(1.0));
		assertNull(((ServiceQuantitativeAttribute) attr.getValueReferences().get(0)).getValueReferences());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_ReferencedAttributes2() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_Support_Gold_TechSupport", model);

		assertNotNull(attr.getValueReferences());
		assertEquals(4, attr.getValueReferences().size());

		List<String> expectedTypeUris = new ArrayList<String>();
		expectedTypeUris.add(supportVocUri + "#ForumSupport");
		expectedTypeUris.add(supportVocUri + "#ForumSupport");
		expectedTypeUris.add(supportVocUri + "#ChatSupport");
		expectedTypeUris.add(supportVocUri + "#PhoneSupport");

		for (ServiceAttribute referencedAttr : attr.getValueReferences()) {
			assertNotNull(referencedAttr);
			for (int i = expectedTypeUris.size() - 1; i >= 0; i--) {
				if (expectedTypeUris.get(i).equals(supportVocUri + "#ForumSupport")) {
					assertNotNull(referencedAttr.getLabel());
					assertTrue(referencedAttr.getLabel().equals("Discussion forums")
							|| referencedAttr.getLabel().equals("Community forums"));
				}
				if (expectedTypeUris.get(i).equals(referencedAttr.getTypeUri())) {
					expectedTypeUris.remove(i);
					i = -1;
				}
			}
		}
		assertEquals(0, expectedTypeUris.size());
	}

	@Test
	@Ignore
	public void testCreateServiceAttribute_NestedReferencedAttributes1() {
		ServiceAttribute attr = ServiceManifestationResolverAccessor.testCreateServiceAttribute(baseUri + "#resource_Support_Gold_TechSupport", model);

		assertNotNull(attr.getValueReferences());
		assertEquals(4, attr.getValueReferences().size());
		for (ServiceAttribute referencedAttr : attr.getValueReferences()) {
			assertNotNull(referencedAttr);
			assertEquals(2, referencedAttr.getValueReferences().size());

			List<String> expectedTypeUris = new ArrayList<String>();
			expectedTypeUris.add(supportVocUri + "#ContactHours");
			expectedTypeUris.add(generalVocUri + "#LanguageReference");
			for (ServiceAttribute nestedReferencedAttr : referencedAttr.getValueReferences()) {
				assertNotNull(nestedReferencedAttr);
				for (int i = expectedTypeUris.size() - 1; i >= 0; i--) {
					if (expectedTypeUris.get(i).equals(nestedReferencedAttr.getTypeUri())) {
						expectedTypeUris.remove(i);
						i = -1;
					}
					if (nestedReferencedAttr.getTypeUri().equalsIgnoreCase(supportVocUri + "#ContactHours"))
						assertEquals("Mo00:00..So24:00", nestedReferencedAttr.getLabel());
					else if (nestedReferencedAttr.getTypeUri().equalsIgnoreCase(generalVocUri + "#LanguageReference"))
						assertEquals("en", nestedReferencedAttr.getLabel());
					else
						fail("Bug in Test");
				}
			}
			assertEquals(0, expectedTypeUris.size());
		}
	}*/
}
