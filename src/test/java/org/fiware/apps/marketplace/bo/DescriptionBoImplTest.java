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
	@Mock private OfferingResolver offeringResolver;
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
	
	// TODO: Add more tests
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// UPDATE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateDescriptionField(Description newDescription) {
		try {
			
			User user = mock(User.class);
			
			Description description = new Description();
			description.setName(DESCRIPTION_NAME);
			description.setUrl(DESCRIPTION_URL);
			description.setDescription("Sample Description");
			description.setOfferings(new ArrayList<Offering>());
			
			// Mock
			doReturn(user).when(userBoMock).getCurrentUser();
			doReturn(description).when(descriptionBo).findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);

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
	
	// TODO: Add more tests changing the results returned by OfferingResolver
	// TODO: Add more tests to check that offerings are retrieved again even if the same URL is sent.


}
