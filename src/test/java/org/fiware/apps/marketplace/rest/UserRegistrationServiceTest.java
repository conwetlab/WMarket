package org.fiware.apps.marketplace.rest;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserRegistrationAuth;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserRegistrationServiceTest {

	@Mock private UserBo userBoMock;
	@Mock private UserRegistrationAuth userRegistrationAuthMock;
	@Mock private UserValidator userValidator;
	@Mock private PasswordEncoder encoder;

	@InjectMocks private UserRegistrationService userRegistrationService;

	// User to be created...
	private User user;

	// Other useful constants
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String VALIDATION_ERROR = "Validation Error";
	private static final String ENCODED_PASSWORD = "ENCODED_PASSWORD";
	private static final String USER_NAME = "example-name";
	private static final String PASSWORD = "12345678";
	private static final String EMAIL = "example@example.com";
	private static final String COMPANY = "Example";
	private static final String DISPLAY_NAME = "Example Name";
	
	private static final ConstraintViolationException VIOLATION_EXCEPTION = 
			new ConstraintViolationException("", new SQLException(), "");

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void generateValidUser() {
		user = new User();
		user.setPassword(PASSWORD);
		user.setEmail(EMAIL);
		user.setCompany(COMPANY);
		user.setDisplayName(DISPLAY_NAME);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void checkMocksCreateCalledOnce() {
		verify(encoder).encode(PASSWORD);
		verify(userBoMock).save(user);
	}
	
	private void checkMocksCreateNotCalled() {
		verify(encoder, never()).encode(anyString());
		verify(userBoMock, never()).save(user);
	}

	@Test
	public void testCreateUserNotAllowed() {
		// Mocks
		when(userRegistrationAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to create user");

		// Verify mocks
		verify(encoder, never()).encode(PASSWORD);
		verify(userBoMock, never()).save(user);

	}

	@Test
	public void testCreateUserNoErrors() throws ValidationException {
		// Mocks
		when(userRegistrationAuthMock.canCreate()).thenReturn(true);
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		//Call the method
		Response res = userRegistrationService.createUser(user);

		// Verify mocks
		verify(userValidator).validateUser(user, true);
		checkMocksCreateCalledOnce();

		// Check
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(user.getRegistrationDate()).isNotNull();
		assertThat(user.getUserName()).isEqualTo(USER_NAME);
		assertThat(user.getDisplayName()).isEqualTo(DISPLAY_NAME);
		assertThat(user.getEmail()).isEqualTo(EMAIL);
		assertThat(user.getCompany()).isEqualTo(COMPANY);
		assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
	}
	
	@Test
	public void testCreateUserValidationError() throws ValidationException {
		// Mock
		when(userRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(userValidator).validateUser(user, true);
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Check
		verify(userValidator).validateUser(user, true);
		checkMocksCreateNotCalled();
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, VALIDATION_ERROR);
	}
	
	private void testCreateUserHibernateException(Exception ex, String message) throws ValidationException {
		// Mock
		when(userRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(ex).when(userBoMock).save(isA(User.class));
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Checks
		verify(userValidator).validateUser(user, true);
		checkMocksCreateCalledOnce();
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, message);
	}

	@Test
	public void testCreateUserAlreadyExists() throws ValidationException {
		testCreateUserHibernateException(VIOLATION_EXCEPTION, 
				"The user and/or the email introduced are already registered in the system");
	}
	
	@Test
	public void testCreateUserOtherDataException() throws ValidationException {
		Exception exception = new HibernateException(new Exception("too much content"));
		testCreateUserHibernateException(exception, exception.getCause().getMessage());
	}
	
	@Test
	public void testCreteUpdateNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(userRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).save(user);
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Checks
		verify(userValidator).validateUser(user, true);
		checkMocksCreateCalledOnce();
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateUserNotAllowed() throws UserNotFoundException {
		User newUser = new User();
		
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to update user " + USER_NAME);

		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(encoder, never()).encode(PASSWORD);
		verify(userBoMock, never()).save(user);
	}
	
	private void testUpdateGenericUserNoErrors(User newUser) {
		// Mocks
		String NEW_ENCODED_PASSWORD = "NEW_PASSWORD";
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);
		when(encoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
		when(encoder.encode(newUser.getPassword())).thenReturn(NEW_ENCODED_PASSWORD);

		try {
			when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		} catch (UserNotFoundException e) {
			// It isn't going to happen
		}

		//Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);
		
		// Verify mocks
		try {
			verify(userBoMock).update(user);
			verify(userValidator).validateUser(newUser, false);
			verify(userBoMock).findByName(USER_NAME);
		} catch (Exception ex) {
			// It isn't going to happen...
		}
		
		if (newUser.getPassword() != null) {
			verify(encoder).encode(newUser.getPassword());
		} else {
			verify(encoder, never()).encode(anyString());
		}

		// Check
		assertThat(res.getStatus()).isEqualTo(200);
		
		// User names cannot be changed now...
		/*if (newUser.getUserName() != null) {
			assertThat(user.getUserName()).isEqualTo(newUser.getUserName());
		} else {
			assertThat(user.getUserName()).isEqualTo(USER_NAME);
		}*/
		
		if (newUser.getDisplayName() != null) {
			assertThat(user.getDisplayName()).isEqualTo(newUser.getDisplayName());
		} else {
			assertThat(user.getDisplayName()).isEqualTo(DISPLAY_NAME);
		}
		
		if (newUser.getEmail() != null) {
			assertThat(user.getEmail()).isEqualTo(newUser.getEmail());
		} else {
			assertThat(user.getEmail()).isEqualTo(EMAIL);
		}
		
		if (newUser.getCompany() != null) {
			assertThat(user.getCompany()).isEqualTo(newUser.getCompany());
		} else {
			assertThat(user.getCompany()).isEqualTo(COMPANY);
		}
		
		if (newUser.getPassword() != null) {
			assertThat(user.getPassword()).isEqualTo(NEW_ENCODED_PASSWORD);
		} else {
			assertThat(user.getPassword()).isEqualTo(PASSWORD);
		}
	}
	
	@Test 
	public void testUpdateUserName() throws UserNotFoundException {
		User newUser = new User();
		newUser.setUserName("new_user_name");
		
		// Mocks
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);

		try {
			when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		} catch (UserNotFoundException e) {
			// It isn't going to happen
		}

		//Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);
		
		// Checks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock, never()).update(user);
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, "userName cannot be changed");
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
	
	@Test
	public void testUpdateUserValidationError() throws ValidationException, UserNotFoundException {
		User newUser = new User();
		
		// Mock
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(userValidator).validateUser(newUser, false);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);

		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);

		// Check
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator).validateUser(newUser, false);
		verify(userBoMock, never()).update(user);
		
		//checkMocksUpdateNotCalled();
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, VALIDATION_ERROR);
	}
	
	@Test
	public void testUpdateUserNonExistingUser() throws UserNotFoundException, ValidationException {
		User newUser = new User();
		String userNotFoundMsg = "user_name does not exist";
		
		// Mock
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);
		doThrow(new UserNotFoundException(userNotFoundMsg)).when(userBoMock).findByName(USER_NAME);
		
		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);
		
		// Check mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator, never()).validateUser(newUser, false);
		verify(userBoMock, never()).update(user);
		GenericRestTestUtils.checkAPIError(res, 404, ErrorType.NOT_FOUND, userNotFoundMsg);
	}
	
	private void testUpdateUserHibernateException(Exception ex, String message) {
		User newUser = new User();
		
		// Mock
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);
		doThrow(ex).when(userBoMock).update(user);
		
		try {
			when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		} catch (UserNotFoundException e) {
			// It isn't going to happen
		}

		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);

		// Checks
		try {
			verify(userValidator).validateUser(user, false);
			verify(userBoMock).findByName(USER_NAME);
			verify(userBoMock).update(user);
			GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, message);
		} catch (Exception e) {
			// It isn't going to happen
		}
		

	}
	
	@Test
	public void testUpdateUserViolationIntegration() throws ValidationException {
		testUpdateUserHibernateException(VIOLATION_EXCEPTION, 
				"The user and/or the email introduced are already registered in the system");
	}
	
	@Test
	public void testUpdateUserOtherDataException() throws ValidationException {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testUpdateUserHibernateException(exception, exception.getCause().getMessage());
	}
	
	@Test
	public void testUpdateUserNotKnowException() throws ValidationException, UserNotFoundException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(userRegistrationAuthMock.canUpdate(user)).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).update(user);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		
		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, user);

		// Checks
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator).validateUser(user, false);
		verify(userBoMock).update(user);
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testDeleteUserNotAllowed() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canDelete(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to delete user " + USER_NAME);

		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock, never()).delete(user);
	}
	
	@Test
	public void testDeleteUserNoErrors() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canDelete(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);
		
		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock).delete(user);
	}
	
	@Test
	public void testDeleteUserNotExisting() throws UserNotFoundException {
		// Mocks
		String msg = "User user_name not found";
		doThrow(new UserNotFoundException(msg)).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuthMock.canDelete(user)).thenReturn(true);
		
		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);
		
		// Assertions
		GenericRestTestUtils.checkAPIError(res, 404, ErrorType.NOT_FOUND, msg);
		
		//Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock, never()).delete(isA(User.class));
	}
	
	@Test
	public void testDeleteUserException() throws UserNotFoundException {
		// Mocks
		String exceptionMsg = "DB is down!";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).delete(user);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canDelete(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
		
		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock).delete(user);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// FIND ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testGetUserNotAllowed() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canGet(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to get user " + USER_NAME);
	}
	
	@Test
	public void testGetUserNoErrors() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuthMock.canGet(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((User) res.getEntity()).isEqualTo(user);
	}
	
	@Test
	public void testGetUserUserNotFound() throws UserNotFoundException {
		// Mocks
		String msg = "User user_name not found";
		doThrow(new UserNotFoundException(msg)).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuthMock.canGet(user)).thenReturn(true);
		
		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);
		
		// Assertions
		GenericRestTestUtils.checkAPIError(res, 404, ErrorType.NOT_FOUND, msg);
	}
	
	@Test
	public void testGetUserException() throws UserNotFoundException {
		// Mocks
		String exceptionMsg = "DB is down!";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuthMock.canGet(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
		
		// Verify
		verify(userBoMock).findByName(USER_NAME);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testListUsersNotAllowed() {
		// Mocks
		when(userRegistrationAuthMock.canList()).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.listUsers(0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to list users");
	}
	
	private void testListUsersInvalidParams(int offset, int max) {
		// Mocks
		when(userRegistrationAuthMock.canList()).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.listUsers(offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListUsersInvalidOffset() {
		testListUsersInvalidParams(-1, 100);
	}
	
	@Test
	public void testListUsersInvalidMax() {
		testListUsersInvalidParams(0, -1);
	}
	
	@Test
	public void testListUsersInvalidOffsetMax() {
		testListUsersInvalidParams(-1, -1);
	}
	
	@Test
	public void testListUsersGetNoErrors() {
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < 3; i++) {
			User user = new User();
			user.setId(i);
			users.add(user);
		}
		
		// Mocks
		when(userRegistrationAuthMock.canList()).thenReturn(true);
		when(userBoMock.getUsersPage(anyInt(), anyInt())).thenReturn(users);
		
		// Call the method
		int offset = 0;
		int max = 100;
		Response res = userRegistrationService.listUsers(offset, max);
		
		// Verify
		verify(userBoMock).getUsersPage(offset, max);
		
		// Assertations
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Users) res.getEntity()).getUsers()).isEqualTo(users);
	}
	
	@Test
	public void testListUsersException() {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).getUsersPage(anyInt(), anyInt());
		when(userRegistrationAuthMock.canList()).thenReturn(true);

		// Call the method
		int offset = 0;
		int max = 100;
		Response res = userRegistrationService.listUsers(offset, max);
		
		// Verify
		verify(userBoMock).getUsersPage(offset, max);
		
		// Check exception
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
}
