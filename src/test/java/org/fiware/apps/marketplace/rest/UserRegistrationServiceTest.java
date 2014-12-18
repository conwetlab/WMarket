package org.fiware.apps.marketplace.rest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.APIError;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Users;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserRegistrationAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class UserRegistrationServiceTest {

	@Mock private UserBo userBoMock;
	@Mock private UserRegistrationAuth userRegistrationAuhtMock;
	@Mock private UserValidator userValidator;
	@Mock private PasswordEncoder encoder;

	@InjectMocks private UserRegistrationService userRegistrationService;

	// User to be created...
	private User user;

	// Other useful constants
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String VALIDATION_ERROR = "Validation Error";
	private static final String ENCODED_PASSWORD = "ENCODED_PASSWORD";
	private static final String USER_NAME = "user_name";
	private static final String PASSWORD = "12345678";
	private static final String EMAIL = "example@example.com";
	private static final String COMPANY = "Example";
	private static final String DISPLAY_NAME = "Example Name";

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
		user.setUserName(USER_NAME);
		user.setPassword(PASSWORD);
		user.setEmail(EMAIL);
		user.setCompany(COMPANY);
		user.setDisplayName(DISPLAY_NAME);
	}
	
	private void checkAPIError(Response res, int status, ErrorType type, String message) {
		assertThat(res.getStatus()).isEqualTo(status);
		APIError error = (APIError) res.getEntity();
		assertThat(error.getErrorType()).isEqualTo(type);
		assertThat(error.getErrorMessage()).isEqualTo(message);
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
	public void testCreateNotAllowed() {
		// Mocks
		when(userRegistrationAuhtMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Assertions
		checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to create user");

		// Verify mocks
		verify(encoder, never()).encode(PASSWORD);
		verify(userBoMock, never()).save(user);

	}

	@Test
	public void testCreateNoErrors() throws ValidationException {
		// Mocks
		when(userRegistrationAuhtMock.canCreate()).thenReturn(true);
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
	public void testCreateValidationError() throws ValidationException {
		// Mock
		when(userRegistrationAuhtMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(userValidator).validateUser(user, true);
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Check
		verify(userValidator).validateUser(user, true);
		checkMocksCreateNotCalled();
		checkAPIError(res, 400, ErrorType.BAD_REQUEST, VALIDATION_ERROR);
	}
	
	private void testCreateDataAccessException(Exception exception, String message) throws ValidationException {
		// Mock
		when(userRegistrationAuhtMock.canCreate()).thenReturn(true);
		doThrow(new DataIntegrityViolationException("", exception)).when(userBoMock).save(isA(User.class));
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Checks
		verify(userValidator).validateUser(user, true);
		checkMocksCreateCalledOnce();
		checkAPIError(res, 400, ErrorType.BAD_REQUEST, message);
	}

	@Test
	public void testNotAllowedToCreateUserAlreadyExists() throws ValidationException {
		testCreateDataAccessException(new MySQLIntegrityConstraintViolationException(), 
				"The user and/or the email introduced are already registered in the system");
	}
	
	@Test
	public void testNotAllowedToCreateUserOtherDataException() throws ValidationException {
		Exception exception = new Exception("Too much content");
		testCreateDataAccessException(exception, exception.getMessage());
	}
	
	@Test
	public void testCreteNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(userRegistrationAuhtMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).save(user);
		when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

		// Call the method
		Response res = userRegistrationService.createUser(user);

		// Checks
		verify(userValidator).validateUser(user, true);
		checkMocksCreateCalledOnce();
		checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateNotAllowed() throws UserNotFoundException {
		User newUser = new User();
		
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);

		// Assertions
		checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to update user " + USER_NAME);

		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(encoder, never()).encode(PASSWORD);
		verify(userBoMock, never()).save(user);
	}
	
	private void testUpdateGenericUserNoErrors(User newUser) {
		// Mocks
		String NEW_ENCODED_PASSWORD = "NEW_PASSWORD";
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);
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
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);

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
		checkAPIError(res, 400, ErrorType.BAD_REQUEST, "userName cannot be changed");
	}
	
	@Test
	public void testUpdateDisplayName() {
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
	public void testUpdateComapmy() {
		User newUser = new User();
		newUser.setEmail("New Awesome Company");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	@Test
	public void testUpdatePassword() {
		User newUser = new User();
		newUser.setPassword("my_new_super_secure_password123456789");
		testUpdateGenericUserNoErrors(newUser);
	}
	
	@Test
	public void testUpdateValidationError() throws ValidationException, UserNotFoundException {
		User newUser = new User();
		
		// Mock
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(userValidator).validateUser(newUser, false);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);

		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);

		// Check
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator).validateUser(newUser, false);
		verify(userBoMock, never()).update(user);
		
		//checkMocksUpdateNotCalled();
		checkAPIError(res, 400, ErrorType.BAD_REQUEST, VALIDATION_ERROR);
	}
	
	@Test
	public void testUpdateNonExistingUser() throws UserNotFoundException, ValidationException {
		User newUser = new User();
		String userNotFoundMsg = "user_name does not exist";
		
		// Mock
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);
		doThrow(new UserNotFoundException(userNotFoundMsg)).when(userBoMock).findByName(USER_NAME);
		
		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, newUser);
		
		// Check mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator, never()).validateUser(newUser, false);
		verify(userBoMock, never()).update(user);
		checkAPIError(res, 404, ErrorType.NOT_FOUND, userNotFoundMsg);
	}
	
	private void testDataAccessException(Exception exception, String message) {
		User newUser = new User();
		
		// Mock
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);
		doThrow(new DataIntegrityViolationException("",
				exception)).when(userBoMock).update(user);
		
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
			checkAPIError(res, 400, ErrorType.BAD_REQUEST, message);
		} catch (Exception e) {
			// It isn't going to happen
		}
		

	}
	
	@Test
	public void testUpdateViolationIntegration() throws ValidationException {
		testDataAccessException(new MySQLIntegrityConstraintViolationException(), 
				"The user and/or the email introduced are already registered in the system");
	}
	
	
	@Test
	public void testUpdateOtherDataException() throws ValidationException {
		Exception exception = new Exception("Too much content");
		testDataAccessException(exception, exception.getMessage());
	}
	
	@Test
	public void testUpdateNotKnowException() throws ValidationException, UserNotFoundException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(userRegistrationAuhtMock.canUpdate(user)).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).update(user);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		
		// Call the method
		Response res = userRegistrationService.updateUser(USER_NAME, user);

		// Checks
		verify(userBoMock).findByName(USER_NAME);
		verify(userValidator).validateUser(user, false);
		verify(userBoMock).update(user);
		checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testDeleteNotAllowed() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canDelete(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to delete user " + USER_NAME);

		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock, never()).delete(user);
	}
	
	@Test
	public void testDeleteNoErrors() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canDelete(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);
		
		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock).delete(user);
	}
	
	@Test
	public void testDeleteNotExisting() throws UserNotFoundException {
		// Mocks
		String msg = "User user_name not found";
		doThrow(new UserNotFoundException(msg)).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuhtMock.canDelete(user)).thenReturn(true);
		
		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);
		
		// Assertions
		checkAPIError(res, 404, ErrorType.NOT_FOUND, msg);
		
		//Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock, never()).delete(isA(User.class));
	}
	
	@Test
	public void testDeleteException() throws UserNotFoundException {
		// Mocks
		String exceptionMsg = "DB is down!";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).delete(user);
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canDelete(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);

		// Assertions
		checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
		
		// Verify mocks
		verify(userBoMock).findByName(USER_NAME);
		verify(userBoMock).delete(user);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// FIND ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testGetNotAllowed() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canGet(user)).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to get user " + USER_NAME);
	}
	
	@Test
	public void testGetNoErrors() throws UserNotFoundException {
		// Mocks
		when(userBoMock.findByName(USER_NAME)).thenReturn(user);
		when(userRegistrationAuhtMock.canGet(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((User) res.getEntity()).isEqualTo(user);
	}
	
	@Test
	public void testGetUserNotFound() throws UserNotFoundException {
		// Mocks
		String msg = "User user_name not found";
		doThrow(new UserNotFoundException(msg)).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuhtMock.canGet(user)).thenReturn(true);
		
		// Call the method
		Response res = userRegistrationService.deleteUser(USER_NAME);
		
		// Assertions
		checkAPIError(res, 404, ErrorType.NOT_FOUND, msg);
	}
	
	@Test
	public void testGetException() throws UserNotFoundException {
		// Mocks
		String exceptionMsg = "DB is down!";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).findByName(USER_NAME);
		when(userRegistrationAuhtMock.canGet(user)).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.getUser(USER_NAME);

		// Assertions
		checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
		
		// Verify
		verify(userBoMock).findByName(USER_NAME);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testListNotAllowed() {
		// Mocks
		when(userRegistrationAuhtMock.canList()).thenReturn(false);

		// Call the method
		Response res = userRegistrationService.listUsers(0, 100);

		// Assertions
		checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to list users");
	}
	
	private void testListInvalidParams(int offset, int max) {
		// Mocks
		when(userRegistrationAuhtMock.canList()).thenReturn(true);

		// Call the method
		Response res = userRegistrationService.listUsers(offset, max);

		// Assertions
		checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListInvalidOffset() {
		testListInvalidParams(-1, 100);
	}
	
	@Test
	public void testListInvalidMax() {
		testListInvalidParams(0, -1);
	}
	
	@Test
	public void testListInvalidOffsetMax() {
		testListInvalidParams(-1, -1);
	}
	
	@Test
	public void testListGetNoErrors() {
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < 3; i++) {
			User user = new User();
			user.setId(i);
			users.add(user);
		}
		
		// Mocks
		when(userRegistrationAuhtMock.canList()).thenReturn(true);
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
	public void testListException() {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(userBoMock).getUsersPage(anyInt(), anyInt());
		when(userRegistrationAuhtMock.canList()).thenReturn(true);

		// Call the method
		int offset = 0;
		int max = 100;
		Response res = userRegistrationService.listUsers(offset, max);
		
		// Verify
		verify(userBoMock).getUsersPage(offset, max);
		
		// Check exception
		checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
}
