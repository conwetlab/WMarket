package org.fiware.apps.marketplace.security.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class StoreRegistrationAuthTest {


	@Mock private AuthUtils authUtils;
	@InjectMocks private static StoreRegistrationAuth authHelper;

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
			when(authUtils.getLoggedUser()).thenReturn(updater);
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
	public void canCreateStore() throws UserNotFoundException {
		when(authUtils.getLoggedUser()).thenReturn(new User());
		assertThat(authHelper.canCreate()).isTrue();
	}

	@Test
	public void canNotCreateStore() throws UserNotFoundException {
		doThrow(new UserNotFoundException("")).when(authUtils).getLoggedUser();
		assertThat(authHelper.canCreate()).isFalse();
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST UPDATE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testUpdate(User creator, User updater, boolean canUpdate)  {
		Store store = setUpTestUpdateAndDelete(creator, updater);

		// Execute the test
		boolean result = authHelper.canUpdate(store);

		// Check the result
		assertThat(result).isEqualTo(canUpdate);

	}

	@Test
	public void canUpdateStoreSameUser() throws UserNotFoundException {
		User creator = createBasicUser(1);
		testUpdate(creator, creator, true);
	}

	@Test
	public void canUpdateStore() throws UserNotFoundException {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testUpdate(creator, updater, true);
	}

	@Test
	public void canNotUpdateStoreNotSameUser() throws UserNotFoundException {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testUpdate(creator, updater, false);
	}

	@Test
	public void canNotUpdateStoreNotLoggedIn() throws UserNotFoundException {			
		User creator = createBasicUser(1);
		User updater = null;
		testUpdate(creator, updater, false);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// TEST DELETE /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	private void testDelete(User creator, User updater, boolean canDelete)  {
		Store store = setUpTestUpdateAndDelete(creator, updater);

		// Execute the test
		boolean result = authHelper.canDelete(store);

		// Check the result
		assertThat(result).isEqualTo(canDelete);

	}

	@Test
	public void canDeleteStoreSameUser() {
		User creator = createBasicUser(1);
		testDelete(creator, creator, true);
	}

	@Test
	public void canDeleteStore() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		testDelete(creator, updater, true);
	}

	@Test
	public void canNotDeleteStoreNotSameUser() {			
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		testDelete(creator, updater, false);
	}

	@Test
	public void canNotDeleteStoreNotLoggedIn() {			
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

}
