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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.Stores;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.StoreAuth;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StoreServiceTest {

	@Mock private StoreBo storeBoMock;
	@Mock private StoreAuth storeAuthMock;
	@Mock private StoreValidator storeValidatorMock;
	@Mock private AuthUtils authUtilsMock;

	@InjectMocks private StoreService storeRegistrationService;

	// Default URI
	private User user;
	private Store store;
	private UriInfo uri;

	// Other useful constants
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String VALIDATION_ERROR = "Validation Exception";
	private static final String DESCRIPTION = "This is a basic description";
	private static final String DISPLAY_NAME = "stOre 1";
	private static final String NAME = "store-1";
	private static final String URL = "https://store.lab.fi-ware.org";
	private static final String PATH = "/api/store";
	
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
	public void generateValidStore() {
		store = new Store();
		store.setDescription(DESCRIPTION);
		store.setDisplayName(DISPLAY_NAME);
		store.setUrl(URL);
	}
	
	@Before
	public void setUpUri() {
		uri = mock(UriInfo.class);
		when(uri.getPath()).thenReturn(PATH);
	}

	@Before
	public void initAuthUtils() throws UserNotFoundException {
		user = new User();
		when(authUtilsMock.getLoggedUser()).thenReturn(user);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreateStoreNotAllowed() {
		// Mocks
		when(storeAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = storeRegistrationService.createStore(uri, store);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to create store");

		// Verify mocks
		verify(storeBoMock, never()).save(store);
	}

	@Test
	public void testCreateStoreNoErrors() throws ValidationException {
		// Mocks
		when(storeAuthMock.canCreate()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, Store.class).setName(NAME);
				return null;
			}
		}).when(storeBoMock).save(store);

		//Call the method
		Response res = storeRegistrationService.createStore(uri, store);

		// Verify mocks
		verify(storeValidatorMock).validateStore(store, true);
		verify(storeBoMock).save(store);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(res.getHeaders().get("Location").get(0).toString()).isEqualTo(PATH + "/" + NAME);

		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		assertThat(store.getRegistrationDate()).isNotNull();
		assertThat(store.getDisplayName()).isEqualTo(DISPLAY_NAME);
		assertThat(store.getName()).isEqualTo(NAME);
		assertThat(store.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(store.getUrl()).isEqualTo(URL);
		assertThat(store.getCreator()).isEqualTo(user);
		assertThat(store.getLasteditor()).isEqualTo(user);
	}

	private void testCreateStoreGenericError(int statusCode, ErrorType errorType, 
			String errorMsg, boolean saveInvoked) {
		int saveTimes = saveInvoked ? 1 : 0;

		// Call the method
		Response res = storeRegistrationService.createStore(uri, store);

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
		when(storeAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(storeValidatorMock).validateStore(store, true);

		testCreateStoreGenericError(400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false);
	}

	@Test
	public void testCreateStoreUserNotFoundException() throws ValidationException, UserNotFoundException {
		// Mocks
		when(storeAuthMock.canCreate()).thenReturn(true);
		doThrow(new UserNotFoundException("User Not Found exception")).when(authUtilsMock).getLoggedUser();

		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false);

	}

	private void testCreateStoreHibernateException(Exception exception, String message) throws ValidationException {
		// Mock
		when(storeAuthMock.canCreate()).thenReturn(true);
		doThrow(exception).when(storeBoMock).save(store);

		testCreateStoreGenericError(400, ErrorType.BAD_REQUEST, message, true);
	}

	@Test
	public void testCreateStoreAlreadyExists() throws ValidationException {
		testCreateStoreHibernateException(VIOLATION_EXCEPTION,
				"There is already a Store with that name/URL registered in the system");
	}

	@Test
	public void testCreateStoreOtherDataException() throws ValidationException {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testCreateStoreHibernateException(exception, exception.getCause().getMessage());
	}

	@Test
	public void testCreteStoreNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(storeAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).save(store);

		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateStoreNotAllowed() throws StoreNotFoundException {
		Store newStore = new Store();

		// Mocks
		when(storeAuthMock.canUpdate(store)).thenReturn(false);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.updateStore(DISPLAY_NAME, newStore);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to update store " + DISPLAY_NAME);

		// Verify mocks
		verify(storeBoMock, never()).update(store);	
	}

	private void testUpdateStoreField(Store newStore) {
		try {
			// Mock
			when(storeAuthMock.canUpdate(store)).thenReturn(true);
			when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);
			
			String previousStoreName = store.getName();

			// Call the method
			Response res = storeRegistrationService.updateStore(DISPLAY_NAME, newStore);

			// Verify mocks
			verify(storeValidatorMock).validateStore(newStore, false);
			verify(storeBoMock).update(store);

			// Assertions
			assertThat(res.getStatus()).isEqualTo(200);

			// New values
			String newStoreName = newStore.getName() != null ? newStore.getName() : store.getName();
			assertThat(store.getName()).isEqualTo(newStoreName);

			String newStoreUrl = newStore.getUrl() != null ? newStore.getUrl() : store.getUrl();
			assertThat(store.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = newStore.getDescription() != null ? 
					newStore.getDescription() : store.getDescription();
			assertThat(store.getDescription()).isEqualTo(newStoreDescription);
			
			// Assert that the name is not changed
			assertThat(store.getName()).isEqualTo(previousStoreName);
		} catch (Exception ex) {
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateStoreDisplayName() {
		Store newStore = new Store();
		newStore.setDisplayName("new_name");
		testUpdateStoreField(newStore);
	}

	@Test
	public void testUpdateStoreUrl() {
		Store newStore = new Store();
		newStore.setUrl("http://fi-ware.org");
		testUpdateStoreField(newStore);
	}

	@Test
	public void testUpdateStoreDescription() {
		Store newStore = new Store();
		newStore.setDescription("New Description");
		testUpdateStoreField(newStore);
	}

	private void testUpdateStoreGenericError(Store newStore, int status, ErrorType errorType, 
			String message, boolean updateInvoked, boolean verifyInvoked) {
		int updateTimes = updateInvoked ? 1 : 0;
		int verifyTimes = verifyInvoked ? 1 : 0;

		try {
			// Mocks
			when(storeAuthMock.canUpdate(store)).thenReturn(true);

			// Call the method
			Response res = storeRegistrationService.updateStore(DISPLAY_NAME, newStore);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(storeValidatorMock, times(verifyTimes)).validateStore(newStore, false);
			verify(storeBoMock, times(updateTimes)).update(store);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testUpdateStoreValidationException() throws ValidationException, StoreNotFoundException {
		Store newStore = new Store();
		
		// Mocks
		doThrow(new ValidationException(VALIDATION_ERROR)).when(storeValidatorMock).validateStore(newStore, false);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);
		
		// Test
		testUpdateStoreGenericError(newStore, 400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false, true);
	}
	
	@Test
	public void testUpdateStoreStoreNotFound() throws StoreNotFoundException {
		Store newStore = new Store();
		
		//Mocks
		String exceptionMsg = "Store not Found!";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(DISPLAY_NAME);
		
		testUpdateStoreGenericError(newStore, 404, ErrorType.NOT_FOUND, exceptionMsg, false, false);
	}
	
	@Test
	public void testUpdateStoreNotFoundException() throws UserNotFoundException, StoreNotFoundException {
		Store newStore = new Store();
		
		// Mocks
		doThrow(new UserNotFoundException("")).when(authUtilsMock).getLoggedUser();
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);
		
		// Test
		testUpdateStoreGenericError(newStore, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false, true);
	}
	
	private void testUpdateStoreHibernateException(Exception exception, String message)  {
		Store newStore = new Store();	
		
		//Mocks
		try {
			doThrow(exception).when(storeBoMock).update(store);
			when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		testUpdateStoreGenericError(newStore, 400, ErrorType.BAD_REQUEST, message, true, true);
	}
	
	@Test
	public void testUpdateStoreAlreadyExists() {
		testUpdateStoreHibernateException(VIOLATION_EXCEPTION,
				"There is already a Store with that name/URL registered in the system");
	}
	
	@Test
	public void testUpdateStoreOtherDataException() {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testUpdateStoreHibernateException(exception, exception.getCause().getMessage());
	}
	
	@Test
	public void testUpdateStoreNotKnownException() throws StoreNotFoundException {
		Store newStore = new Store();
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).update(store);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		testUpdateStoreGenericError(newStore, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteStoreNotAllowed() throws StoreNotFoundException {
		// Mocks
		when(storeAuthMock.canDelete(store)).thenReturn(false);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.deleteStore(DISPLAY_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to delete store " + DISPLAY_NAME);

		// Verify mocks
		verify(storeBoMock, never()).delete(store);	
	}
	
	@Test
	public void testDeleteStoreNoErrors() throws StoreNotFoundException {
		// Mocks
		when(storeAuthMock.canDelete(store)).thenReturn(true);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.deleteStore(DISPLAY_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);

		// Verify mocks
		verify(storeBoMock).delete(store);	
	}
	
	private void testDeleteStoreGenericError(int status, ErrorType errorType, 
			String message, boolean deleteInvoked) {
		int deleteTimes = deleteInvoked ? 1 : 0;

		try {
			// Mocks
			when(storeAuthMock.canDelete(store)).thenReturn(true);

			// Call the method
			Response res = storeRegistrationService.deleteStore(DISPLAY_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(storeBoMock, times(deleteTimes)).delete(store);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test 
	public void testDeleteStoreStoreNotFound() throws StoreNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(DISPLAY_NAME);
		
		testDeleteStoreGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test
	public void testDeleteStoreNotKnownException() throws StoreNotFoundException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).delete(store);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		testDeleteStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// GET ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetStoreNotAllowed() throws StoreNotFoundException {
		// Mocks
		when(storeAuthMock.canGet(store)).thenReturn(false);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.getStore(DISPLAY_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to get store " + DISPLAY_NAME);
	}
	
	@Test
	public void testGetStoreNoErrors() throws StoreNotFoundException {
		// Mocks
		when(storeAuthMock.canGet(store)).thenReturn(true);
		when(storeBoMock.findByName(DISPLAY_NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.getStore(DISPLAY_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Store) res.getEntity()).isEqualTo(store);
	}
	
	private void testGetStoreGenericError(int status, ErrorType errorType, String message) {

		try {
			// Mocks
			when(storeAuthMock.canGet(store)).thenReturn(true);

			// Call the method
			Response res = storeRegistrationService.getStore(DISPLAY_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testGetStoreStoreNotFound() throws StoreNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(DISPLAY_NAME);
		
		testGetStoreGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testGetStoreNotKnownException() throws StoreNotFoundException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).findByName(DISPLAY_NAME);

		testGetStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListStoresNotAllowed() {
		// Mocks
		when(storeAuthMock.canList()).thenReturn(false);

		// Call the method
		Response res = storeRegistrationService.listStores(0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to list stores");
	}
	
	
	private void testListStoresInvalidParams(int offset, int max) {
		// Mocks
		when(storeAuthMock.canList()).thenReturn(true);

		// Call the method
		Response res = storeRegistrationService.listStores(offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListStoresInvalidOffset() {
		testListStoresInvalidParams(-1, 100);
	}
	
	@Test
	public void testListStoresInvalidMax() {
		testListStoresInvalidParams(0, -1);
	}
	
	@Test
	public void testListStoresInvalidOffsetMax() {
		testListStoresInvalidParams(-1, -1);
	}
	
	@Test
	public void testListStoresGetNoErrors() {
		List<Store> stores = new ArrayList<Store>();
		for (int i = 0; i < 3; i++) {
			Store store = new Store();
			store.setId(i);
			stores.add(store);
		}
		
		// Mocks
		when(storeAuthMock.canList()).thenReturn(true);
		when(storeBoMock.getStoresPage(anyInt(), anyInt())).thenReturn(stores);
		
		// Call the method
		int offset = 0;
		int max = 100;
		Response res = storeRegistrationService.listStores(offset, max);
		
		// Verify
		verify(storeBoMock).getStoresPage(offset, max);
		
		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Stores) res.getEntity()).getStores()).isEqualTo(stores);
	}
	
	@Test
	public void testListStoresException() {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock)
				.getStoresPage(anyInt(), anyInt());
		when(storeAuthMock.canList()).thenReturn(true);

		// Call the method
		int offset = 0;
		int max = 100;
		Response res = storeRegistrationService.listStores(offset, max);
		
		// Verify
		verify(storeBoMock).getStoresPage(offset, max);
		
		// Check exception
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}


}
