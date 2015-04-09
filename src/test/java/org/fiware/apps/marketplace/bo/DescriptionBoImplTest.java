package org.fiware.apps.marketplace.bo;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.impl.DescriptionBoImpl;
import org.fiware.apps.marketplace.dao.DescriptionDao;
import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.DescriptionValidator;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.fiware.apps.marketplace.security.auth.DescriptionAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DescriptionBoImplTest {
	
	@Mock private DescriptionAuth descriptionAuthMock;
	@Mock private DescriptionValidator descriptionValidatorMock;
	@Mock private DescriptionDao descriptionDaoMock;
	@Mock private UserBo userBoMock;
	@Mock private OfferingResolver offeringResolverMock;
	@Mock private RdfIndexer rdfIndexer;
	@InjectMocks private DescriptionBoImpl descriptionBo;
	
	private static final String STORE_NAME = "store";
	private static final String DESCRIPTION_NAME = "description";
	private static final String DESCRIPTION_URL = "http://repo.lab.fiware.org/sampleUsdl.rdf";
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.descriptionBo = spy(this.descriptionBo);
	}
	
	private Description generateDescription(List<Offering> offerings) {
		Description description = new Description();
		description.setName(DESCRIPTION_NAME);
		description.setUrl(DESCRIPTION_URL);
		description.setDescription("Sample Description");
		description.setOfferings(offerings);

		return description;
	}
	
	private Offering generateOffering(String url, Description describedIn, String name, String displayName,
			String description, String imageUrl, String version) {
		Offering offering = new Offering();
		offering.setUri(url);
		offering.setDescribedIn(describedIn);
		offering.setName(name);
		offering.setDisplayName(displayName);
		offering.setDescription(description);
		offering.setImageUrl(imageUrl);
		offering.setVersion(version);
		
		return offering;
	}
	
	// TODO: Add more tests
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// UPDATE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateDescriptionField(Description newDescription) {
		try {
			
			User user = mock(User.class);
			
			Description description = generateDescription(new ArrayList<Offering>());
			
			// Mock
			doReturn(user).when(userBoMock).getCurrentUser();
			doReturn(description).when(descriptionBo).findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
			when(descriptionAuthMock.canUpdate(description)).thenReturn(true);

			// Get the 
			String previousName = description.getName();
			
			// Call the method
			descriptionBo.update(STORE_NAME, DESCRIPTION_NAME, newDescription);

			// Verify mocks
			verify(descriptionDaoMock).update(description);
			
			// Assert that description name has not changed
			assertThat(description.getName()).isEqualTo(previousName);

			// New values
			String newStoreName = newDescription.getName() != null ? 
					newDescription.getName() : description.getName();
			assertThat(description.getName()).isEqualTo(newStoreName);

			String newStoreUrl = newDescription.getUrl() != null ? 
					newDescription.getUrl() : description.getUrl();
			assertThat(description.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = newDescription.getDescription() != null ? 
					newDescription.getDescription() : description.getDescription();
			assertThat(description.getDescription()).isEqualTo(newStoreDescription);
		} catch (Exception ex) {
			ex.printStackTrace();
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateDescriptionName() {
		Description newDescription = new Description();
		newDescription.setDisplayName("new_name");
		testUpdateDescriptionField(newDescription);
	}
	
	@Test
	public void testUpdateDescriptionUrl() {
		Description newDescription = new Description();
		newDescription.setUrl(DESCRIPTION_URL + "a");
		testUpdateDescriptionField(newDescription);
	}

	@Test
	public void testUpdateDescriptionDescription() {
		Description newDescription = new Description();
		newDescription.setDescription("New Description");
		testUpdateDescriptionField(newDescription);
	}
	
	private void compareOfferings(Offering offering1, Offering offering2) {
		
		assertThat(offering1.getDescription()).isEqualTo(offering2.getDescription());
		assertThat(offering1.getDisplayName()).isEqualTo(offering2.getDisplayName());
		assertThat(offering1.getName()).isEqualTo(offering2.getName());
		assertThat(offering1.getImageUrl()).isEqualTo(offering2.getImageUrl());
		assertThat(offering1.getVersion()).isEqualTo(offering2.getVersion());

	}
	
	private void testUpdateDescriptionURL(String storedOfferingURI, String newOfferingURI) {
		
		try {
			
			// Create the description stored in the Database
			Description storedDescription = generateDescription(new ArrayList<Offering>());

			Offering storedOffering = generateOffering(storedOfferingURI, storedDescription, "offering-1", 
					"Offering 1", "Example Description", "http://marketplace.com/link_to_img.jpg", "1.0");
			storedOffering.setId(1); 	// Stored offerings contain an ID
			
			storedDescription.addOffering(storedOffering);
					
			// New description
			Description updatedDescription = generateDescription(new ArrayList<Offering>());
			
			// Descriptions returned by offeringResolver when the updatedDescription is parsed
			Offering newOffering = generateOffering(newOfferingURI, storedDescription, "cool-offering", 
					"Cool Offering", "New Description", "http://marketplace.com/link_to_new_img.jpg", "1.2");
				
			List<Offering> newOfferings = new ArrayList<Offering>();
			newOfferings.add(newOffering);
			
			// Configure the mocks
			doReturn(storedDescription).when(descriptionBo).findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
			when(descriptionAuthMock.canUpdate(storedDescription)).thenReturn(true);	// User can update
			when(offeringResolverMock.resolveOfferingsFromServiceDescription(storedDescription))
					.thenReturn(newOfferings);
			
			// Call the method
			descriptionBo.update(STORE_NAME, DESCRIPTION_NAME, updatedDescription);
			
			// Verify that the database has been updated
			verify(descriptionDaoMock).update(storedDescription);
			
			// Check that the URL has been updated properly
			assertThat(storedDescription.getUrl()).isEqualTo(updatedDescription.getUrl());
			
			// Check the offerings. The description should contain one
			assertThat(storedDescription.getOfferings().size()).isEqualTo(1);
			Offering updatedOffering = storedDescription.getOfferings().get(0);
						
			// updatedOffering should always contain the information returned by offeringResolver
			compareOfferings(updatedOffering, newOffering);
			
			if (storedOfferingURI.equals(newOfferingURI)) {
				// If the offering existed previously, the instance should not change
				assertThat(updatedOffering).isSameAs(storedOffering);
				assertThat(updatedOffering).isNotSameAs(newOffering);
				// ID should be retained when the offering is updated
				assertThat(updatedOffering.getId()).isEqualTo(storedOffering.getId());
			} else {
				// If the offering did not exist previously, the instance should change
				assertThat(updatedOffering).isNotSameAs(storedOffering);
				assertThat(updatedOffering).isSameAs(newOffering);
			}
			
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}
	}
	
	@Test
	public void testUpdateDescriptionOfferingExist() throws Exception {
		String offeringURI = "http://store.lab.fiware.org/OFFERING_URI";
		testUpdateDescriptionURL(offeringURI, offeringURI);
	}
	
	@Test
	public void testUpdateDescriptionOfferingNotExist() throws Exception {
		String storedOfferingURI = "http://store.lab.fiware.org/OFFERING_URI";
		String newOfferingURI = "http://store.lab.fiware.org/NEW_OFFERING_URI";
		testUpdateDescriptionURL(storedOfferingURI, newOfferingURI);
	}
	
	@Test
	public void testUpdateComplexOffering() throws Exception {
		
		String repeatedOfferingURI = "http://store.lab.fiware.org/OFFERING_URI";
		String nonRepeatedOfferingURI = "http://store.lab.fiware.org/NEW_OFFERING_URI";
		
		// Create the description stored in the Database
		Description storedDescription = generateDescription(new ArrayList<Offering>());
		
		Offering storedOffering = generateOffering(repeatedOfferingURI, storedDescription, "offering-1", 
				"Offering 1", "Example Description", "http://marketplace.com/link_to_img.jpg", "1.0");
		storedOffering.setId(1); 	// Stored offerings contain an ID
		
		storedDescription.addOffering(storedOffering);
				
		// New description
		Description newDescription = generateDescription(new ArrayList<Offering>());
		
		// Offerings contained in the new URL
		Offering newOffering1 = generateOffering(repeatedOfferingURI, storedDescription, "cool-offering", 
				"Cool Offering", "New Description", "http://marketplace.com/link_to_new_img.jpg", "1.2");
		Offering newOffering2 = generateOffering(nonRepeatedOfferingURI, storedDescription, "my-new-offering", 
				"My New Offering", "New Description 2", "http://marketplace.com/link_to_new_img2.jpg", "1.1");		
		
		List<Offering> newOfferings = new ArrayList<Offering>();
		newOfferings.add(newOffering1);
		newOfferings.add(newOffering2);
		
		// Configure the mocks
		doReturn(storedDescription).when(descriptionBo).findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
		when(descriptionAuthMock.canUpdate(storedDescription)).thenReturn(true);	// User can update
		when(offeringResolverMock.resolveOfferingsFromServiceDescription(storedDescription))
				.thenReturn(newOfferings);
		
		// Call the method
		descriptionBo.update(STORE_NAME, DESCRIPTION_NAME, newDescription);
		
		// Verify that the database has been updated
		verify(descriptionDaoMock).update(storedDescription);
		
		// Check that the URL has been updated properly
		assertThat(storedDescription.getUrl()).isEqualTo(newDescription.getUrl());
		
		// Check the offerings. The description should contain two
		assertThat(storedDescription.getOfferings().size()).isEqualTo(2);
		
		// Check first offering (it previously exist in the system so they're the same instance)
		Offering updatedOffering = storedDescription.getOfferings().get(0);
		compareOfferings(updatedOffering, newOffering1);
		assertThat(updatedOffering).isSameAs(storedOffering);
		assertThat(updatedOffering).isNotSameAs(newOffering1);
		
		// Check second offering (it does not exist in the system so they are not the same instance)
		updatedOffering = storedDescription.getOfferings().get(1);
		compareOfferings(updatedOffering, newOffering2);
		assertThat(updatedOffering).isNotSameAs(storedOffering);
		assertThat(updatedOffering).isSameAs(newOffering2);
	}
	
	
	// TODO: Add more tests changing the results returned by OfferingResolver

}
