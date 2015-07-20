package org.fiware.apps.marketplace.bo.impl;

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

import java.util.Collection;
import java.util.List;

import org.fiware.apps.marketplace.bo.impl.UserBoImpl;
import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserBoImplTest {
	
	@Mock private UserAuth userAuthMock;
	@Mock private UserValidator userValidatorMock;
	@Mock private UserDao userDaoMock;
	@Mock private PasswordEncoder passwordEncoder;
	@InjectMocks private UserBoImpl userBo;
	
	private static final String ENCODED_PASSWORD = "ENCODED PASSWORD";
	private static final String USER_NAME = "userName";
	private static final String NOT_AUTHORIZED_BASE = "You are not authorized to %s";
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.userBo = spy(this.userBo);
		when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// SAVE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testSaveException(User user) throws Exception {
		// Call the method
		try {
			userBo.save(user);
			fail("Exception expected");
		} catch (Exception e) {
			// Verify that the DAO has not been called
			verify(userDaoMock, never()).save(user);
			
			// Throw the exception
			throw e;
		}

	}

	@Test
	public void testSaveNotAuthorized() throws Exception {
		try {
			User user = mock(User.class);
			when(userAuthMock.canCreate(user)).thenReturn(false);
			
			// Call the method and check that DAO is not called
			testSaveException(user);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "create user"));
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testSaveInvalidUser() throws Exception {
		
		User user = mock(User.class);
		doReturn("display name").when(user).getDisplayName();
		doThrow(new ValidationException("a field", "invalid")).when(userValidatorMock).validateNewUser(user);
		when(userAuthMock.canCreate(user)).thenReturn(true);
		
		// Default string is available
		when(userDaoMock.isUserNameAvailable(anyString())).thenReturn(true);
		
		// Call the method and check that DAO is not called
		testSaveException(user);
	}
	
	private void testSave(User user, String expectedUserName) {
		
		when(userAuthMock.canCreate(user)).thenReturn(true);
		
		try {
			userBo.save(user);
			
			// Verify that the DAO has been called
			verify(userDaoMock).save(user);
			
			// Assert that the password has been encoded
			assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
			
			// Check user name
			assertThat(user.getUserName()).isEqualTo(expectedUserName);
			
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testSaveUserName() {
		User user = new User();
		user.setDisplayName("MaRkEt PlAcE&1??0");
		user.setPassword("password");
		String expectedUserName = "market-place10";
		
		// Default string is available
		when(userDaoMock.isUserNameAvailable(anyString())).thenReturn(true);
		
		// Save & check
		testSave(user, expectedUserName);
		
		// Check that the DAO has been properly called
		verify(userDaoMock).isUserNameAvailable(expectedUserName);
	}
	
	@Test
	public void testSaveUserNameNotAvailable() {
		int availableID = 3;
		User user = new User();
		user.setDisplayName("MaRkEt PlAcE&1??0");
		user.setPassword("password");
		String baseUserName = "market-place10";
		String expectedFinalUserName = baseUserName + "-" + availableID;
		
		// Default string is NOT available
		// First ID checked is 1
		for (int i = 1; i < availableID; i++) {
			String checkedUser = baseUserName + "-" + i;
			doReturn(false).when(userDaoMock).isUserNameAvailable(checkedUser);
		}
		
		// Only the third ID is available
		when(userDaoMock.isUserNameAvailable(baseUserName + "-" + availableID)).thenReturn(true);
		
		// Save & check
		testSave(user, expectedFinalUserName);
		
		// Check that the DAO has been properly called
		// Until and available ID is found
		for (int i = 1; i <= availableID; i++) {
			String checkedUser = baseUserName + "-" + i;
			verify(userDaoMock).isUserNameAvailable(checkedUser);
		}
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// UPDATE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateException(String userName, User updatedUser) throws Exception {		
		// Call the method
		try {
			userBo.update(userName, updatedUser);
			fail("Exception expected");
		} catch (Exception e) {
			verify(userDaoMock, never()).update(any(User.class));
			throw e;
		}

	}
	
	@Test(expected=UserNotFoundException.class)
	public void testUpdateNotFound() throws Exception {
		
		User user = mock(User.class);
		doThrow(new UserNotFoundException("user not found")).when(userDaoMock).findByName(USER_NAME);
		when(userAuthMock.canUpdate(user)).thenReturn(true);
		
		// Call the method and check that DAO is not called
		testUpdateException(USER_NAME, user);
	}

	@Test
	public void testUpdateNotAuthorized() throws Exception {
		try {
			User updatedUser = mock(User.class);
			User userToBeUpdated = mock(User.class);
			
			doReturn(userToBeUpdated).when(userBo).findByName(USER_NAME);
			when(userAuthMock.canUpdate(userToBeUpdated)).thenReturn(false);
			
			// Call the method and check that DAO is not called
			testUpdateException(USER_NAME, updatedUser);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "update user " + USER_NAME));
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateInvalidUser() throws Exception {
		
		User updatedUser = mock(User.class);
		User userToBeUpdated = mock(User.class);
		
		doThrow(new ValidationException("a field", "invalid")).when(userValidatorMock).validateUpdatedUser(userToBeUpdated, updatedUser);
		doReturn(userToBeUpdated).when(userDaoMock).findByName(USER_NAME);
		when(userAuthMock.canUpdate(userToBeUpdated)).thenReturn(true);
		
		// Call the method and check that DAO is not called
		testUpdateException(USER_NAME, updatedUser);
	}
	
	private void testUpdateGenericUserNoErrors(User newUser) {
		
		String displayName = "displayName";
		String email = "abc@def.com";
		String password = "password";
		String company = "company";
		
		User user = new User();
		user.setUserName(USER_NAME);
		user.setDisplayName(displayName);
		user.setEmail(email);
		user.setPassword(password);
		user.setCompany(company);
		
		// Mocks
		when(userAuthMock.canUpdate(user)).thenReturn(true);
		try {
			doReturn(user).when(userDaoMock).findByName(USER_NAME);
		} catch (Exception ex) {
			fail("Exeception not expected", ex);
		}
		
		// Get previous user name
		String previousUserName = user.getUserName();

		//Call the method
		try {
			userBo.update(USER_NAME, newUser);
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}
		
		// Verify mocks
		try {
			verify(userDaoMock).findByName(USER_NAME);
			verify(userDaoMock).update(user);
		} catch (Exception ex) {
			fail("Exeception not expected", ex);
		}
		
		// User name has not changed
		assertThat(user.getUserName()).isEqualTo(previousUserName);
		
		// Verify that all the included fields are updated		
		if (newUser.getDisplayName() != null) {
			assertThat(user.getDisplayName()).isEqualTo(newUser.getDisplayName());
		} else {
			assertThat(user.getDisplayName()).isEqualTo(displayName);
		}
		
		if (newUser.getEmail() != null) {
			assertThat(user.getEmail()).isEqualTo(newUser.getEmail());
		} else {
			assertThat(user.getEmail()).isEqualTo(email);
		}
		
		if (newUser.getCompany() != null) {
			assertThat(user.getCompany()).isEqualTo(newUser.getCompany());
		} else {
			assertThat(user.getCompany()).isEqualTo(company);
		}
		
		if (newUser.getPassword() != null) {
			verify(passwordEncoder).encode(newUser.getPassword());
			assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
		} else {
			verify(passwordEncoder, never()).encode(anyString());
			assertThat(user.getPassword()).isEqualTo(password);
		}
	}
	
	@Test 
	public void testUpdateUserName() throws Exception {
		User newUser = new User();
		newUser.setUserName("new_user_name");
		testUpdateGenericUserNoErrors(newUser);

	}
	
	@Test
	public void testUpdateUserDisplayName() {
		User newUser = new User();
		newUser.setDisplayName("New Display Name");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	@Test
	public void testUpdateEmail() {
		User newUser = new User();
		newUser.setEmail("newmail@newmail.com");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	@Test
	public void testUpdateUserComapmy() {
		User newUser = new User();
		newUser.setEmail("New Awesome Company");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	@Test
	public void testUpdateUserPassword() {
		User newUser = new User();
		newUser.setPassword("my_new_super_secure_password123456789");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// DELETE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteException(String userName) throws Exception {
		
		try {			
			// Call the method
			userBo.delete(userName);
			fail("Exception expected");
		} catch (Exception e) {
			// Verify that the DAO has not been called
			verify(userDaoMock, never()).delete(any(User.class));
			
			// Throw the exception
			throw e;
		}

	}
	
	@Test(expected=UserNotFoundException.class)
	public void testDeleteUserNotFoundException() throws Exception {
		
		String userName = "user";
		doThrow(new UserNotFoundException("userNotFound")).when(userDaoMock).findByName(userName);
		
		testDeleteException(userName);
		
	}
	
	@Test
	public void testDeleteNotAuthorizedException() throws Exception {
		try {
			User user = mock(User.class);
			doReturn(user).when(userBo).findByName(USER_NAME);
			when(userAuthMock.canDelete(user)).thenReturn(false);
			
			testDeleteException(USER_NAME);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "delete user " + USER_NAME));
		}

		
	}
	
	@Test
	public void testDelete() throws Exception {
		User user = mock(User.class);
		
		// Configure Mock
		doReturn(user).when(userDaoMock).findByName(USER_NAME);
		when(userAuthMock.canDelete(user)).thenReturn(true);
		
		// Call the method
		userBo.delete(USER_NAME);
		
		// Verify that the method has been called
		verify(userDaoMock).delete(user);
	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// FIND BY NAME /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=UserNotFoundException.class)
	public void testFindByNameException() throws Exception {
		String userName = "username";
		doThrow(new UserNotFoundException("user not found")).when(userDaoMock).findByName(userName);
		
		userBo.findByName(userName);
	}
	
	@Test
	public void testFinByNameNotAuthorized() throws Exception{
		String name = "username";
		User user = mock(User.class);
		
		// Set up mocks
		when(userDaoMock.findByName(name)).thenReturn(user);
		when(userAuthMock.canGet(user)).thenReturn(true);
		
		try {
			// Call the function
			userBo.findByName(name);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "find user"));
		}
		
		verify(userDaoMock).findByName(name);
	}

	@Test
	public void testFindByName() throws Exception {
		
		String name = "username";
		User user = mock(User.class);
		
		// Set up mocks
		when(userDaoMock.findByName(name)).thenReturn(user);
		when(userAuthMock.canGet(user)).thenReturn(true);
		
		// Call the function
		User returnedUser = userBo.findByName(name);
		// If an exception is risen, this check is not executed
		assertThat(returnedUser).isEqualTo(user);

		verify(userDaoMock).findByName(name);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// FIND BY MAIL /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=UserNotFoundException.class)
	public void testFindByEmailException() throws Exception {
		String email = "test@example.com";
		doThrow(new UserNotFoundException("user not found")).when(userDaoMock).findByEmail(email);
		
		userBo.findByEmail(email);
	}
	
	@Test
	public void testFinByMailNotAuthorized() throws Exception{
		String email = "test@example.com";
		User user = mock(User.class);
		
		// Set up mocks
		when(userDaoMock.findByEmail(email)).thenReturn(user);
		when(userAuthMock.canGet(user)).thenReturn(false);
		
		try {
			// Call the function
			userBo.findByEmail(email);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "find user"));
		}
		
		verify(userDaoMock).findByEmail(email);

	}

	@Test
	public void testFindByEmail() throws Exception {
		
		String email = "test@example.com";
		User user = mock(User.class);
		
		// Set up mocks
		when(userDaoMock.findByEmail(email)).thenReturn(user);
		when(userAuthMock.canGet(user)).thenReturn(true);
		
		// Call the function
		User returnedUser = userBo.findByEmail(email);
		// If an exception is risen, this check is not executed
		assertThat(returnedUser).isEqualTo(user);

		verify(userDaoMock).findByEmail(email);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// GET USERS PAGE ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetUsersPageNotAuthorized() throws Exception{
		try {
			
			when(userAuthMock.canList()).thenReturn(false);
			userBo.getUsersPage(0, 100);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "list users"));
		}
	}
	
	@Test
	public void testGetUsersPage() throws NotAuthorizedException {
		@SuppressWarnings("unchecked")
		List<User> users = mock(List.class);
		
		when(userDaoMock.getUsersPage(anyInt(), anyInt())).thenReturn(users);
		when(userAuthMock.canList()).thenReturn(true);
		
		// Call the function
		int offset = 0;
		int max = 100;
		assertThat(userBo.getUsersPage(offset, max)).isEqualTo(users);
		
		// Verify that the DAO is called
		verify(userDaoMock).getUsersPage(offset, max);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// GET ALL USERS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetAllUsersNotAuthorized() throws Exception{
		try {
			
			when(userAuthMock.canList()).thenReturn(false);
			userBo.getAllUsers();
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "list users"));
		}

	}
	
	@Test
	public void testGetAllUsers() throws NotAuthorizedException {
		@SuppressWarnings("unchecked")
		List<User> users = mock(List.class);
		
		when(userDaoMock.getAllUsers()).thenReturn(users);
		when(userAuthMock.canList()).thenReturn(true);
		
		// Call the function
		assertThat(userBo.getAllUsers()).isEqualTo(users);
		
		// Verify that the DAO is called
		verify(userDaoMock).getAllUsers();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// GET CURRENT USER ///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetCurrentUser(User user, SecurityContext context) {
		try {
			String userName = user.getUserName();

			// Configure the mocks
			when(userDaoMock.findByName(userName)).thenReturn(user);

			// Configure the context returned by the SecurityContextHolder
			SecurityContextHolder.setContext(context);

			// Call the function
			User returnedUser = userBo.getCurrentUser();

			verify(userDaoMock).findByName(userName);
			assertThat(returnedUser).isEqualTo(user);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
	}

	@Test
	public void testGetCurrentUserOAuth2() {
		// User
		final String userName = "oauth2username";
		User user = new User();
		user.setUserName(userName);

		// The context
		SecurityContext context = new SecurityContext() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setAuthentication(Authentication auth) {
			}

			@Override
			public Authentication getAuthentication() {
				UserProfile userProfile = new UserProfile();
				userProfile.setId(userName);
				ClientAuthenticationToken auth = new ClientAuthenticationToken(null, "FIWARE", userProfile, null);	
				return auth;
			}
		};

		testGetCurrentUser(user, context);
	}
	
	@Test
	public void testGetCurrentUserLocal() {
		// The user
		final String userName = "oauth2username";
		User user = new User();
		user.setUserName(userName);

		// The context
		SecurityContext context = new SecurityContext() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setAuthentication(Authentication arg0) {

			}

			@Override
			public Authentication getAuthentication() {
				UserProfile userProfile = new UserProfile();
				userProfile.setId(userName);
				Authentication auth = new Authentication() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public String getName() {
						return userName;
					}
					
					@Override
					public void setAuthenticated(boolean arg0) throws IllegalArgumentException {						
					}
					
					@Override
					public boolean isAuthenticated() {
						return true;
					}
					
					@Override
					public Object getPrincipal() {
						return null;
					}
					
					@Override
					public Object getDetails() {
						return null;
					}
					
					@Override
					public Object getCredentials() {
						return null;
					}
					
					@Override
					public Collection<? extends GrantedAuthority> getAuthorities() {
						return null;
					}
				};

				return auth;
			}
		};

		testGetCurrentUser(user, context);
	}

}
