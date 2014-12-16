package org.fiware.apps.marketplace.security.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class UserRegistrationAuthTest {

	public static class UserAuthTest extends UserRegistrationAuthTest {

		@Mock private AuthUtils authUtils;
		@InjectMocks private static UserRegistrationAuth authHelper;
		
		///////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////// BASIC METHODS ////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		private User createBasicUser(int id) {
			User user = new User();
			user.setId(id);
			return user;
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////// TEST CREATE /////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		@Test
		public void canCreateUser() throws UserNotFoundException {
			assertThat(authHelper.canCreate()).isTrue();
		}


		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////// TEST UPDATE /////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		private void testUpdate(User creator, User updater, boolean canUpdate)  {
			try {
				when(authUtils.getLoggedUser()).thenReturn(updater);
			} catch (UserNotFoundException e) {
				// Nothing to do...
			}
			
			// Execute the test
			boolean result = authHelper.canUpdate(creator);

			// Check the result
			assertThat(result).isEqualTo(canUpdate);

		}

		@Test
		public void canUpdateUserSameUser() throws UserNotFoundException {
			User creator = createBasicUser(1);
			testUpdate(creator, creator, true);
		}
		
		@Test
		public void canUpdateUser() throws UserNotFoundException {
			User creator = createBasicUser(1);
			User updater = createBasicUser(1);
			testUpdate(creator, updater, true);
		}

		@Test
		public void canNotUpdateUserNotSameUser() throws UserNotFoundException {			
			User creator = createBasicUser(1);
			User updater = createBasicUser(2);
			testUpdate(creator, updater, false);
		}
		
		@Test
		public void canNotUpdateUserNotLoggedIn() throws UserNotFoundException {			
			User creator = createBasicUser(1);
			User updater = null;
			testUpdate(creator, updater, false);
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////// TEST DELETE /////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		private void testDelete(User creator, User updater, boolean canDelete)  {
			try {
				when(authUtils.getLoggedUser()).thenReturn(updater);
			} catch (UserNotFoundException e) {
				// Nothing to do...
			}
			
			// Execute the test
			boolean result = authHelper.canDelete(creator);

			// Check the result
			assertThat(result).isEqualTo(canDelete);

		}
		
		@Test
		public void canDeleteUserSameUser() {
			User creator = createBasicUser(1);
			testDelete(creator, creator, true);
		}
		
		@Test
		public void canDeleteUser() {
			User creator = createBasicUser(1);
			User updater = createBasicUser(1);
			testDelete(creator, updater, true);
		}

		@Test
		public void canNotDeleteUserNotSameUser() {			
			User creator = createBasicUser(1);
			User updater = createBasicUser(2);
			testDelete(creator, updater, false);
		}
		
		@Test
		public void canNotDeleteUserNotLoggedIn() {			
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

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
}
