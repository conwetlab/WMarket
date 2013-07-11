package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.model.Offering;
import org.junit.Test;

public class OfferingResolverTest {

	private static final String uri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Amazon_EC2_001";

	@Test
	public void testResolveOfferingsFromServiceDescription_Basic() {
		List<Offering> offerings = OfferingResolver.resolveOfferingsFromServiceDescription(uri, "");
		assertNotNull(offerings);
		assertEquals(6, offerings.size());
	}

	@Test
	public void testResolveOfferingsFromServiceDescription_ExpectedOfferings() {
		List<Offering> offerings = OfferingResolver.resolveOfferingsFromServiceDescription(uri, "");

		List<String> expectedUris = new ArrayList<String>();
		expectedUris.add(uri + "#offering_EC2_Small");
		expectedUris.add(uri + "#offering_Support_Basic");
		expectedUris.add(uri + "#offering_Support_Bronze");
		expectedUris.add(uri + "#offering_Support_Silver");
		expectedUris.add(uri + "#offering_Support_Gold");
		expectedUris.add(uri + "#offering_Support_Premium");

		for (Offering offering : offerings) {
			assertNotNull(offering.getOfferingUri());
			for (int i = expectedUris.size() - 1; i >= 0; i--) {
				if (offering.getOfferingUri().equals(expectedUris.get(i))) {
					expectedUris.remove(i);
				}
			}
		}
		assertEquals(0, expectedUris.size());
	}

	@Test
	public void testResolveOfferingsFromServiceDescription_OfferingContent() {
		List<Offering> offerings = OfferingResolver.resolveOfferingsFromServiceDescription(uri, "");
		for (Offering offering : offerings) {
			if (offering.getOfferingUri().equals(uri + "#offering_EC2_Small")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(14, offering.getPricePlanUris().size());				
			} else if (offering.getOfferingUri().equals(uri + "#offering_Support_Basic")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(1, offering.getPricePlanUris().size());	
			} else if (offering.getOfferingUri().equals(uri + "#offering_Support_Bronze")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(1, offering.getPricePlanUris().size());	
			} else if (offering.getOfferingUri().equals(uri + "#offering_Support_Silver")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(1, offering.getPricePlanUris().size());	
			} else if (offering.getOfferingUri().equals(uri + "#offering_Support_Gold")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(1, offering.getPricePlanUris().size());		
			} else if (offering.getOfferingUri().equals(uri + "#offering_Support_Premium")) {
				assertEquals(1, offering.getServiceUris().size());
				assertEquals(1, offering.getPricePlanUris().size());		
			} else
				fail("Unknown offering uri. This point should not be reached.");
		}
	}
}
