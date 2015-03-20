package org.fiware.apps.marketplace.security.auth;

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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class OfferingAuthTest {
	
	@Mock private UserBo userBoMock;
	@InjectMocks private static OfferingAuth authHelper;

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private User createBasicUser(int id) {
		User user = new User();
		user.setId(id);
		return user;
	}

	private Offering setUpTestUpdateAndDelete(User creator, User updater) {
		// Set up the test
		Store store = new Store();
		
		Description description = new Description();
		description.setCreator(creator);
		description.setStore(store);
		
		Offering offering = new Offering();
		offering.setDescribedIn(description);
		
		try {
			when(userBoMock.getCurrentUser()).thenReturn(updater);
		} catch (UserNotFoundException e) {
			// never happens
		}

		return offering;
	}

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST CREATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void canCreateOffering() 
			throws UserNotFoundException, NotAuthorizedException {

		Offering description = mock(Offering.class);
		when(userBoMock.getCurrentUser()).thenReturn(new User());
		assertThat(authHelper.canCreate(description)).isTrue();
	}

	@Test
	public void canNotCreateOffering() throws UserNotFoundException, NotAuthorizedException {
		// Set up the test
		Store store = new Store();
		
		Description description = new Description();
		description.setStore(store);
		
		Offering offering = new Offering();
		offering.setDescribedIn(description);
		
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		assertThat(authHelper.canCreate(offering)).isFalse();
	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST UPDATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testUpdate(User creator, User updater, boolean canUpdate) throws NotAuthorizedException  {
		Offering offering = setUpTestUpdateAndDelete(creator, updater);
		assertThat(authHelper.canUpdate(offering)).isEqualTo(canUpdate);
	}

	@Test
	public void canUpdateOfferingSameUser() 
			throws UserNotFoundException, NotAuthorizedException {
		
		User creator = createBasicUser(1);
		testUpdate(creator, creator, true);
	}

	@Test
	public void canUpdateOffering() 
			throws UserNotFoundException, NotAuthorizedException {
		
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testUpdate(creator, updater, true);
	}

	@Test
	public void canNotUpdateOfferingNotSameUser() 
			throws UserNotFoundException, NotAuthorizedException {
		
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testUpdate(creator, updater, false);
	}

	@Test
	public void canNotUpdateOfferingNotLoggedIn() 
			throws UserNotFoundException, NotAuthorizedException {	
		
		User creator = createBasicUser(1);
		User updater = null;
		testUpdate(creator, updater, false);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST DELETE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDelete(User creator, User updater, boolean canDelete) throws NotAuthorizedException  {
		Offering offering = setUpTestUpdateAndDelete(creator, updater);
		assertThat(authHelper.canDelete(offering)).isEqualTo(canDelete);
	}

	@Test
	public void canDeleteOfferingSameUser() throws NotAuthorizedException {
		User creator = createBasicUser(1);
		testDelete(creator, creator, true);
	}

	@Test
	public void canDeleteOffering() throws NotAuthorizedException {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testDelete(creator, updater, true);
	}

	@Test
	public void canNotDeleteOfferingNotSameUser() throws NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testDelete(creator, updater, false);
	}

	@Test
	public void canNotDeleteOfferingNotLoggedIn() throws NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = null;
		testDelete(creator, updater, false);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// TEST LIST //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void canList() throws NotAuthorizedException {
		assertThat(authHelper.canList()).isTrue();
	}
	
	@Test
	public void canListStore() throws NotAuthorizedException {
		assertThat(authHelper.canList(new Store())).isTrue();
	}
	
	@Test
	public void canListDescription() throws NotAuthorizedException {
		assertThat(authHelper.canList(new Description())).isTrue();
	}

}
