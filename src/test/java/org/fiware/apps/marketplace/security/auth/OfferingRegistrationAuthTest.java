package org.fiware.apps.marketplace.security.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class OfferingRegistrationAuthTest {

	public static class FinalTest extends OfferingRegistrationAuthTest {

		@Mock private AuthUtils authUtils;
		@InjectMocks private static OfferingRegistrationAuth authHelper;
		
		///////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////// BASIC METHODS ////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		private User createBasicUser(int id) {
			User user = new User();
			user.setId(id);
			return user;
		}
		
		private Service setUpTestUpdateAndDelete(User creator, User updater) {
			// Set up the test		
			Service service = new Service();
			service.setCreator(creator);

			try {
				when(authUtils.getLoggedUser()).thenReturn(updater);
			} catch (UserNotFoundException e) {
				// never happens
			}
			
			return service;
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////// TEST CREATE /////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		@Test
		public void canCreateOffering() throws UserNotFoundException {
			when(authUtils.getLoggedUser()).thenReturn(new User());
			assertThat(authHelper.canCreate()).isTrue();
		}

		@Test
		public void canNotCreateOffering() throws UserNotFoundException {
			doThrow(new UserNotFoundException("")).when(authUtils).getLoggedUser();
			assertThat(authHelper.canCreate()).isFalse();
		}


		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////// TEST UPDATE /////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
		
		private void testUpdate(User creator, User updater, boolean canUpdate)  {
			Service service = setUpTestUpdateAndDelete(creator, updater);

			// Execute the test
			boolean result = authHelper.canUpdate(service);

			// Check the result
			assertThat(result).isEqualTo(canUpdate);

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
			Service service = setUpTestUpdateAndDelete(creator, updater);

			// Execute the test
			boolean result = authHelper.canDelete(service);

			// Check the result
			assertThat(result).isEqualTo(canDelete);

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
		///////////////////////////////////// TEST LIST /////////////////////////////////////
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
