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

import static org.mockito.Mockito.*;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class StoreAuthTest {


	@Mock private UserBo userBoMock;
	@InjectMocks private static StoreAuth authHelper;

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private User createBasicUser(int id) {
		User user = new User();
		user.setId(id);
		return user;
	}

	private Store setUpTestUpdateAndDelete(User creator, User updater) {
		// Set up the test		
		Store store = new Store();
		store.setCreator(creator);

		try {
			when(userBoMock.getCurrentUser()).thenReturn(updater);
		} catch (UserNotFoundException e) {
			// never happens
		}

		return store;
	}

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST CREATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void canCreateStore() throws UserNotFoundException, NotAuthorizedException {
		Store store = mock(Store.class);
		when(userBoMock.getCurrentUser()).thenReturn(new User());
		authHelper.canCreate(store);
	}

	@Test(expected = NotAuthorizedException.class)
	public void canNotCreateStore() throws UserNotFoundException, NotAuthorizedException {
		Store store = mock(Store.class);
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		authHelper.canCreate(store);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST UPDATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testUpdate(User creator, User updater) throws NotAuthorizedException  {
		Store store = setUpTestUpdateAndDelete(creator, updater);
		authHelper.canUpdate(store);
	}

	@Test
	public void canUpdateStoreSameUser() 
			throws UserNotFoundException, NotAuthorizedException {
		User creator = createBasicUser(1);
		testUpdate(creator, creator);
	}

	@Test
	public void canUpdateStore() 
			throws UserNotFoundException, NotAuthorizedException {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testUpdate(creator, updater);
	}

	@Test(expected = NotAuthorizedException.class)
	public void canNotUpdateStoreNotSameUser() 
			throws UserNotFoundException, NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testUpdate(creator, updater);
	}

	@Test(expected = NotAuthorizedException.class)
	public void canNotUpdateStoreNotLoggedIn() 
			throws UserNotFoundException, NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = null;
		testUpdate(creator, updater);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST DELETE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDelete(User creator, User updater) throws NotAuthorizedException  {
		Store store = setUpTestUpdateAndDelete(creator, updater);
		authHelper.canDelete(store);
	}

	@Test
	public void canDeleteStoreSameUser() throws NotAuthorizedException {
		User creator = createBasicUser(1);
		testDelete(creator, creator);
	}

	@Test
	public void canDeleteStore() throws NotAuthorizedException {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testDelete(creator, updater);
	}

	@Test(expected = NotAuthorizedException.class)
	public void canNotDeleteStoreNotSameUser() throws NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testDelete(creator, updater);
	}

	@Test(expected = NotAuthorizedException.class)
	public void canNotDeleteStoreNotLoggedIn() throws NotAuthorizedException {			
		User creator = createBasicUser(1);
		User updater = null;
		testDelete(creator, updater);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// TEST LIST //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void canList() throws NotAuthorizedException {
		authHelper.canList();
	}

}
