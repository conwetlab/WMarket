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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.model.Offering;
import org.junit.Ignore;
import org.junit.Test;

public class OfferingResolverTest {

	/*private static final String uri = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/cloudServices/Amazon_EC2_001";

	@Test
	@Ignore
	public void testResolveOfferingsFromServiceDescription_Basic() {
		List<Offering> offerings = OfferingResolver.resolveOfferingsFromServiceDescription(uri, "");
		assertNotNull(offerings);
		assertEquals(6, offerings.size());
	}

	@Test
	@Ignore
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
	@Ignore
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
	}*/
}
