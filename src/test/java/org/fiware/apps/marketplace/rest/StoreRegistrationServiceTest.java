package org.fiware.apps.marketplace.rest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.StoreRegistrationAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class StoreRegistrationServiceTest {
	
	@Mock private StoreBo storeBoMock;
	@Mock private StoreRegistrationAuth storeRegistrationAuthMock;
	@Mock private StoreValidator storeValidatorMock;
	@Mock private AuthUtils authUtilsMock;

	@InjectMocks private StoreRegistrationService storeRegistrationService;
	
	// Default store
	private User user;
	private Store store;
	
	// Other useful constants
	private static final String VALIDATION_ERROR = "Validation Exception";
	private static final String DESCRIPTION = "This is a basic description";
	private static final String NAME = "store";
	private static final String URL = "https://store.lab.fi-ware.org";
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
	public void generateValidStore() {
		store = new Store();
		store.setDescription(DESCRIPTION);
		store.setName(NAME);
		store.setUrl(URL);
	}
	
	@Before
	public void initAuthUtils() throws UserNotFoundException {
		when(authUtilsMock.getLoggedUser()).thenReturn(user);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreateStoreNotAllowed() {
		// Mocks
		when(storeRegistrationAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = storeRegistrationService.createStore(store);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to create store");

		// Verify mocks
		verify(storeBoMock, never()).save(store);
	}
	
	@Test
	public void testCreateStoreNoErrors() throws ValidationException {
		// Mocks
		when(storeRegistrationAuthMock.canCreate()).thenReturn(true);

		//Call the method
		Response res = storeRegistrationService.createStore(store);

		// Verify mocks
		verify(storeValidatorMock).validateStore(store, true);
		verify(storeBoMock).save(store);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);
		
		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		assertThat(store.getRegistrationDate()).isNotNull();
		assertThat(store.getName()).isEqualTo(NAME);
		assertThat(store.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(store.getUrl()).isEqualTo(URL);
		assertThat(store.getCreator()).isEqualTo(user);
		assertThat(store.getLasteditor()).isEqualTo(user);
	}
	
	private void testCreateStoreGenericError(int statusCode, ErrorType errorType, String errorMsg, boolean saveInvoked) {
		int saveTimes = saveInvoked ? 1 : 0;
		
		// Call the method
		Response res = storeRegistrationService.createStore(store);
		
		// Verify mocks
		try {
			verify(storeValidatorMock).validateStore(store, true);
			verify(storeBoMock, times(saveTimes)).save(store);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, errorMsg);
		} catch (ValidationException e) {
			// Impossible...
			fail("exception " + e + " not expected");
		}
	}
	
	@Test
	public void testCreateStoreValidationException() throws ValidationException {
		// Mocks
		when(storeRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(storeValidatorMock).validateStore(store, true);
		
		testCreateStoreGenericError(400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false);
	}
	
	@Test
	public void testCreateStoreUserNotFoundException() throws ValidationException, UserNotFoundException {
		// Mocks
		when(storeRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new UserNotFoundException("User Not Found exception")).when(authUtilsMock).getLoggedUser();

		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false);
		
	}
	
	private void testCreateStoreDataAccessException(Exception exception, String message) throws ValidationException {
		// Mock
		when(storeRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new DataIntegrityViolationException("", exception)).when(storeBoMock).save(store);

		testCreateStoreGenericError(400, ErrorType.BAD_REQUEST, message, true);
	}
	
	@Test
	public void testNotAllowedToCreateUserAlreadyExists() throws ValidationException {
		testCreateStoreDataAccessException(new MySQLIntegrityConstraintViolationException(), 
				"There is already a Store with that name/URL registered in the system");
	}
	
	@Test
	public void testNotAllowedToCreateUserOtherDataException() throws ValidationException {
		Exception exception = new Exception("Too much content");
		testCreateStoreDataAccessException(exception, exception.getMessage());
	}
	
	@Test
	public void testCreteNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(storeRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).save(store);
		
		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}

	

	
	


}
