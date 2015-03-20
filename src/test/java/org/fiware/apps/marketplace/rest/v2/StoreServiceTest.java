package org.fiware.apps.marketplace.rest.v2;

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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.Stores;
import org.fiware.apps.marketplace.rest.v2.StoreService;
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

	@InjectMocks private StoreService storeRegistrationService;

	// Default URI
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
		store.setName(NAME);
		store.setUrl(URL);
	}
	
	@Before
	public void setUpUri() {
		uri = mock(UriInfo.class);
		when(uri.getPath()).thenReturn(PATH);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreateStoreNotAllowed() throws Exception {
		// Mocks
		Exception e = new NotAuthorizedException("create store");
		doThrow(e).when(storeBoMock).save(store);

		// Call the method
		Response res = storeRegistrationService.createStore(uri, store);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 403, ErrorType.FORBIDDEN, e.getMessage());

		// Verify mocks
		verify(storeBoMock).save(store);
	}

	@Test
	public void testCreateStoreNoErrors() throws Exception {
		// Mocks
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
		verify(storeBoMock).save(store);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(res.getHeaders().get("Location").get(0).toString()).isEqualTo(PATH + "/" + NAME);

		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		// assertThat(store.getRegistrationDate()).isNotNull();
		assertThat(store.getDisplayName()).isEqualTo(DISPLAY_NAME);
		assertThat(store.getName()).isEqualTo(NAME);
		assertThat(store.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(store.getUrl()).isEqualTo(URL);
		// assertThat(store.getCreator()).isEqualTo(user);
		// assertThat(store.getLasteditor()).isEqualTo(user);
	}
	
	private void testCreateStoreGenericError(int statusCode, ErrorType errorType, 
			String fieldName, String errorMsg) {
		
		// Call the method
		Response res = storeRegistrationService.createStore(uri, store);

		// Verify mocks
		try {
			verify(storeBoMock).save(store);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, errorMsg, fieldName);
		} catch (Exception e) {
			e.printStackTrace();
			// Impossible...
			fail("exception " + e + " not expected");
		}
	}
	
	@Test
	public void testCreateStoreValidationException() throws Exception {
		// Mocks
		String field = "aField";
		doThrow(new ValidationException(field, VALIDATION_ERROR)).when(storeBoMock).save(store);

		testCreateStoreGenericError(400, ErrorType.VALIDATION_ERROR, field, VALIDATION_ERROR);
	}
	
	@Test
	public void testCreateStoreUserNotFoundException() throws Exception {
		// Mocks
		String exceptionMsg = "User not found";
		doThrow(new RuntimeException(new UserNotFoundException(exceptionMsg)))
				.when(storeBoMock).save(store);

		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, null, exceptionMsg);

	}
	
	private void testCreateStoreHibernateException(Exception exception, String message)  throws Exception {
		// Mock
		doThrow(exception).when(storeBoMock).save(store);

		testCreateStoreGenericError(400, ErrorType.BAD_REQUEST, null, message);
	}
	
	@Test
	public void testCreateStoreAlreadyExists() throws Exception {
		testCreateStoreHibernateException(VIOLATION_EXCEPTION,
				"There is already a Store with that name/URL registered in the system");
	}

	@Test
	public void testCreateStoreOtherDataException() throws Exception {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testCreateStoreHibernateException(exception, exception.getCause().getMessage());
	}
	
	@Test
	public void testCreteStoreNotKnowException() throws Exception {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).save(store);

		testCreateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, null, exceptionMsg);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateStoreNotAllowed() throws Exception {
		// Mocks
		Exception e = new NotAuthorizedException("update store");
		doThrow(e).when(storeBoMock).update(NAME, store);

		// Call the method
		Response res = storeRegistrationService.updateStore(NAME, store);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 403, ErrorType.FORBIDDEN, e.getMessage());

		// Verify mocks
		verify(storeBoMock).update(NAME, store);	
	}
	
	private void testUpdateStoreGenericError(int status, ErrorType errorType, 
			String field, String message) {
		
		try {
			// Call the method
			Response res = storeRegistrationService.updateStore(NAME, store);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message, field);

			// Verify mocks
			verify(storeBoMock).update(NAME, store);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testUpdateStoreValidationException() throws Exception {		
		// Mocks
		String field = "field";
		doThrow(new ValidationException("field", VALIDATION_ERROR)).when(storeBoMock).update(NAME, store);
		when(storeBoMock.findByName(NAME)).thenReturn(store);
		
		// Test
		testUpdateStoreGenericError(400, ErrorType.VALIDATION_ERROR, field, VALIDATION_ERROR);
	}
	
	@Test
	public void testUpdateStoreStoreNotFound() throws Exception {		
		//Mocks
		String exceptionMsg = "Store not Found!";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).update(NAME, store);
		
		testUpdateStoreGenericError(404, ErrorType.NOT_FOUND, null, exceptionMsg);
	}
	
	@Test
	public void testUpdateStoreNotFoundException() throws Exception {		
		// Mocks
		String exceptionMsg = "user not found";
		doThrow(new RuntimeException(new UserNotFoundException(exceptionMsg))).when(storeBoMock).update(NAME, store);
		when(storeBoMock.findByName(NAME)).thenReturn(store);
		
		// Test
		testUpdateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, null, exceptionMsg);
	}
	
	private void testUpdateStoreHibernateException(Exception exception, String message) {
		//Mocks
		try {
			doThrow(exception).when(storeBoMock).update(NAME, store);
			when(storeBoMock.findByName(NAME)).thenReturn(store);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		testUpdateStoreGenericError(400, ErrorType.BAD_REQUEST, null, message);
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
	public void testUpdateStoreNotKnownException() throws Exception {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).update(NAME, store);

		testUpdateStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, null, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteStoreGenericError(int status, ErrorType errorType, 
			String message) {
		
		try {
			// Call the method
			Response res = storeRegistrationService.deleteStore(NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(storeBoMock).delete(NAME);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testDeleteStoreNotAllowed() throws Exception {
		// Mocks
		Exception e = new NotAuthorizedException("delete store");
		doThrow(e).when(storeBoMock).delete(NAME);
		
		testDeleteStoreGenericError(403, ErrorType.FORBIDDEN, e.getMessage());
	}
	
	@Test 
	public void testDeleteStoreStoreNotFound() throws Exception {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).delete(NAME);
		
		testDeleteStoreGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testDeleteStoreNotKnownException() throws StoreNotFoundException, NotAuthorizedException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).delete(NAME);

		testDeleteStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	@Test
	public void testDeleteStoreNoErrors() throws Exception {
		// Call the method
		Response res = storeRegistrationService.deleteStore(NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);

		// Verify mocks
		verify(storeBoMock).delete(NAME);	
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// GET ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetStoreNotAllowed() throws Exception {
		// Mocks
		Exception e = new NotAuthorizedException("get store");
		doThrow(e).when(storeBoMock).findByName(NAME);

		// Call the method
		Response res = storeRegistrationService.getStore(NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 403, ErrorType.FORBIDDEN, e.getMessage());
		
		// Verify mocks
		verify(storeBoMock).findByName(NAME);	
	}
	
	@Test
	public void testGetStoreNoErrors() throws Exception {
		// Mocks
		when(storeBoMock.findByName(NAME)).thenReturn(store);

		// Call the method
		Response res = storeRegistrationService.getStore(NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Store) res.getEntity()).isEqualTo(store);
	}
	
	private void testGetStoreGenericError(int status, ErrorType errorType, String message) {
		try {
			// Call the method
			Response res = storeRegistrationService.getStore(NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testGetStoreStoreNotFound() throws Exception {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(NAME);
		
		testGetStoreGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testGetStoreNotKnownException() throws Exception {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock).findByName(NAME);

		testGetStoreGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testListStoresNotAllowed() throws NotAuthorizedException {
		// Mocks
		Exception e = new NotAuthorizedException("list stores");
		doThrow(e).when(storeBoMock).getStoresPage(anyInt(), anyInt());

		// Call the method
		Response res = storeRegistrationService.listStores(0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 403, ErrorType.FORBIDDEN, e.getMessage());
		
		// Verify
		verify(storeBoMock).getStoresPage(0, 100);
	}
	
	
	private void testListStoresInvalidParams(int offset, int max) {
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
	public void testListStoresGetNoErrors() throws NotAuthorizedException {
		List<Store> stores = new ArrayList<Store>();
		for (int i = 0; i < 3; i++) {
			Store store = new Store();
			store.setId(i);
			stores.add(store);
		}
		
		// Mocks
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
	public void testListStoresException() throws NotAuthorizedException {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(storeBoMock)
				.getStoresPage(anyInt(), anyInt());

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
