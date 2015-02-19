package org.fiware.apps.marketplace.security.auth;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2014-2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class DescriptionAuthTest {


	@Mock private UserBo userBoMock;
	@InjectMocks private static DescriptionAuth authHelper;

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private User createBasicUser(int id) {
		User user = new User();
		user.setId(id);
		return user;
	}

	private Description setUpTestUpdateAndDelete(User creator, User updater) {
		// Set up the test		
		Description offeringsDescription = new Description();
		offeringsDescription.setCreator(creator);

		try {
			when(userBoMock.getCurrentUser()).thenReturn(updater);
		} catch (UserNotFoundException e) {
			// never happens
		}

		return offeringsDescription;
	}

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST CREATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void canCreateOffering() throws UserNotFoundException {
		when(userBoMock.getCurrentUser()).thenReturn(new User());
		assertThat(authHelper.canCreate()).isTrue();
	}

	@Test
	public void canNotCreateOffering() throws UserNotFoundException {
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		assertThat(authHelper.canCreate()).isFalse();
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST UPDATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testUpdate(User creator, User updater, boolean canUpdate)  {
		Description offeringsDescription = setUpTestUpdateAndDelete(creator, updater);

		// Execute the test
		boolean result = authHelper.canUpdate(offeringsDescription);

		// Check the result
		assertThat(result).isEqualTo(canUpdate);

	}

	@Test
	public void canUpdateOfferingSameUser() throws UserNotFoundException {
		User creator = createBasicUser(1);
		testUpdate(creator, creator, true);
	}

	@Test
	public void canUpdateOffering() throws UserNotFoundException {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testUpdate(creator, updater, true);
	}

	@Test
	public void canNotUpdateOfferingNotSameUser() throws UserNotFoundException {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testUpdate(creator, updater, false);
	}

	@Test
	public void canNotUpdateOfferingNotLoggedIn() throws UserNotFoundException {			
		User creator = createBasicUser(1);
		User updater = null;
		testUpdate(creator, updater, false);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST DELETE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	private void testDelete(User creator, User updater, boolean canDelete)  {
		Description offeringsDescription = setUpTestUpdateAndDelete(creator, updater);

		// Execute the test
		boolean result = authHelper.canDelete(offeringsDescription);

		// Check the result
		assertThat(result).isEqualTo(canDelete);

	}

	@Test
	public void canDeleteOfferingSameUser() {
		User creator = createBasicUser(1);
		testDelete(creator, creator, true);
	}

	@Test
	public void canDeleteOffering() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testDelete(creator, updater, true);
	}

	@Test
	public void canNotDeleteOfferingNotSameUser() {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testDelete(creator, updater, false);
	}

	@Test
	public void canNotDeleteOfferingNotLoggedIn() {			
		User creator = createBasicUser(1);
		User updater = null;
		testDelete(creator, updater, false);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// TEST LIST //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void canList() {
		assertThat(authHelper.canList()).isTrue();
	}
	
	@Test
	public void canListStore() {
		assertThat(authHelper.canList(new Store())).isTrue();
	}

}
