package org.fiware.apps.marketplace.rest;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.OfferingsDescriptionBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.OfferingDescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.OfferingsDescription;
import org.fiware.apps.marketplace.model.OfferingsDescriptions;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.OfferingsDescriptionValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.OfferingsDescriptionRegistrationAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.hp.hpl.jena.shared.JenaException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class OfferingsDescriptionRegistrationServiceTest {

	@Mock private StoreBo storeBoMock;
	@Mock private OfferingsDescriptionBo offeringsDescriptionBoMock;
	@Mock private OfferingsDescriptionRegistrationAuth offeringsDescriptionRegistrationAuthMock;
	@Mock private OfferingsDescriptionValidator offeringsDescriptionValidatorMock;
	@Mock private AuthUtils authUtilsMock;

	@InjectMocks private OfferingsDescriptionRegistrationService offeringRegistrationService;

	// Default values
	private Store store;
	private OfferingsDescription offeringsDescription;
	private User user;

	// Other useful constants
	private static final String STORE_NAME = "WStore";
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String DESCRIPTION_ALREADY_EXISTS = 
			"There is already an Offering in this Store with that name/URL";
	private static final String VALIDATION_ERROR = "Validation Exception";
	private static final String DESCRIPTION = "This is a basic description";
	private static final String DESCRIPTION_NAME = "offerings description";
	private static final String INVALID_RDF = "Your RDF could not be parsed";
	private static final String URL = "https://repo.lab.fi-ware.org/description.rdf";

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BASIC METHODS ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void generateValidStore() {
		offeringsDescription = new OfferingsDescription();
		offeringsDescription.setDescription(DESCRIPTION);
		offeringsDescription.setName(DESCRIPTION_NAME);
		offeringsDescription.setUrl(URL);
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
	public void testCreateOfferingsDescriptionNotAllowed() {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = offeringRegistrationService.createOfferingsDescription(STORE_NAME, offeringsDescription);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to create offering");

		// Verify mocks
		verify(storeBoMock, never()).save(store);
	}

	@Test
	public void testCreateOfferingsDescriptionNoErrors() throws ValidationException {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);

		//Call the method
		Response res = offeringRegistrationService.createOfferingsDescription(STORE_NAME, offeringsDescription);

		// Verify mocks
		verify(offeringsDescriptionValidatorMock).validateOfferingsDescription(offeringsDescription, true);
		verify(offeringsDescriptionBoMock).save(offeringsDescription);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);

		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		assertThat(offeringsDescription.getRegistrationDate()).isNotNull();
		assertThat(offeringsDescription.getName()).isEqualTo(DESCRIPTION_NAME);
		assertThat(offeringsDescription.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(offeringsDescription.getUrl()).isEqualTo(URL);
		assertThat(offeringsDescription.getCreator()).isEqualTo(user);
		assertThat(offeringsDescription.getLasteditor()).isEqualTo(user);
	}

	private void testCreateOfferingsDescriptionGenericError(int statusCode, ErrorType errorType, 
			String errorMsg, boolean saveInvoked) {
		
		int saveTimes = saveInvoked ? 1 : 0;

		// Call the method
		Response res = offeringRegistrationService.createOfferingsDescription(STORE_NAME, offeringsDescription);

		// Verify mocks
		try {
			verify(offeringsDescriptionValidatorMock).validateOfferingsDescription(offeringsDescription, true);
			verify(offeringsDescriptionBoMock, times(saveTimes)).save(offeringsDescription);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, errorMsg);
		} catch (ValidationException e) {
			// Impossible...
			fail("exception " + e + " not expected");
		}
	}

	@Test
	public void testCreateOfferingsDescriptionValidationException() throws ValidationException {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).
				when(offeringsDescriptionValidatorMock).validateOfferingsDescription(offeringsDescription, true);

		testCreateOfferingsDescriptionGenericError(400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false);
	}
	
	@Test
	public void testCreateOfferingsDescriptionInvalidRDF() throws ValidationException {
		//Mocks
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new JenaException("Some message")).
				when(offeringsDescriptionBoMock).save(offeringsDescription);
		
		testCreateOfferingsDescriptionGenericError(400, ErrorType.BAD_REQUEST, INVALID_RDF, true);
	}

	@Test
	public void testCreateOfferingsDescriptionUserNotFoundException() 
			throws ValidationException, UserNotFoundException {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new UserNotFoundException("User Not Found exception")).when(authUtilsMock).getLoggedUser();

		testCreateOfferingsDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false);

	}

	@Test
	public void testCreateOfferingsDescriptionStoreNotFoundException() 
			throws ValidationException, StoreNotFoundException {
		// Mocks
		String exceptionMsg = "Store Not Found!";
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(STORE_NAME);

		testCreateOfferingsDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}

	private void testCreateOfferingsDescriptionDataAccessException(Exception exception, String message) 
			throws ValidationException {
		// Mock
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new DataIntegrityViolationException("", exception)).
				when(offeringsDescriptionBoMock).save(offeringsDescription);

		testCreateOfferingsDescriptionGenericError(400, ErrorType.BAD_REQUEST, message, true);
	}

	@Test
	public void testCreateOfferingsDescriptionAlreadyExists() throws ValidationException {
		testCreateOfferingsDescriptionDataAccessException(new MySQLIntegrityConstraintViolationException(), 
				DESCRIPTION_ALREADY_EXISTS);
	}

	@Test
	public void testCreateOfferingsDescriptionOtherDataException() throws ValidationException {
		Exception exception = new Exception("Too much content");
		testCreateOfferingsDescriptionDataAccessException(exception, exception.getMessage());
	}

	@Test
	public void testCreteOfferingsDescriptionNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(offeringsDescriptionRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(offeringsDescriptionBoMock).save(offeringsDescription);

		testCreateOfferingsDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateOfferingsDescriptionNotAllowed() 
			throws OfferingDescriptionNotFoundException, StoreNotFoundException {
		
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canUpdate(offeringsDescription)).thenReturn(false);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Call the method
		Response res = offeringRegistrationService.
				updateOfferingsDescription(STORE_NAME, DESCRIPTION_NAME, newOfferingsDescription);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to update offering " + DESCRIPTION_NAME);

		// Verify mocks
		verify(offeringsDescriptionBoMock, never()).update(offeringsDescription);	
	}

	private void testUpdateOfferingsDescriptionField(OfferingsDescription newOfferingsDescription) {
		try {
			// Mock
			when(offeringsDescriptionRegistrationAuthMock.canUpdate(offeringsDescription)).thenReturn(true);
			when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
					thenReturn(offeringsDescription);

			// Call the method
			Response res = offeringRegistrationService.
					updateOfferingsDescription(STORE_NAME, DESCRIPTION_NAME, newOfferingsDescription);

			// Verify mocks
			verify(offeringsDescriptionValidatorMock).validateOfferingsDescription(newOfferingsDescription, false);
			verify(offeringsDescriptionBoMock).update(offeringsDescription);

			// Assertions
			assertThat(res.getStatus()).isEqualTo(200);

			// New values
			String newStoreName = newOfferingsDescription.getName() != null ? 
					newOfferingsDescription.getName() : offeringsDescription.getName();
			assertThat(offeringsDescription.getName()).isEqualTo(newStoreName);

			String newStoreUrl = newOfferingsDescription.getUrl() != null ? 
					newOfferingsDescription.getUrl() : offeringsDescription.getUrl();
			assertThat(offeringsDescription.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = newOfferingsDescription.getDescription() != null ? 
					newOfferingsDescription.getDescription() : offeringsDescription.getDescription();
			assertThat(offeringsDescription.getDescription()).isEqualTo(newStoreDescription);
		} catch (Exception ex) {
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateOfferingsDescriptionName() {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();
		newOfferingsDescription.setName("new_name");
		testUpdateOfferingsDescriptionField(newOfferingsDescription);
	}

	@Test
	public void testUpdateOfferingsDescriptionUrl() {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();
		newOfferingsDescription.setUrl("https://repo.lab.fi-ware.org/new_description.rdf");
		testUpdateOfferingsDescriptionField(newOfferingsDescription);
	}

	@Test
	public void testUpdateOfferingsDescriptionDescription() {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();
		newOfferingsDescription.setDescription("New Description");
		testUpdateOfferingsDescriptionField(newOfferingsDescription);
	}

	private void testUpdateOfferingsDescriptionGenericError(OfferingsDescription newOfferingsDescription, 
			int status, ErrorType errorType, String message, boolean updateInvoked, boolean verifyInvoked) {
		int updateTimes = updateInvoked ? 1 : 0;
		int verifyTimes = verifyInvoked ? 1 : 0;

		try {
			// Mocks
			when(offeringsDescriptionRegistrationAuthMock.canUpdate(offeringsDescription)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.
					updateOfferingsDescription(STORE_NAME, DESCRIPTION_NAME, newOfferingsDescription);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(offeringsDescriptionValidatorMock, times(verifyTimes)).
					validateOfferingsDescription(newOfferingsDescription, false);
			verify(offeringsDescriptionBoMock, times(updateTimes)).update(offeringsDescription);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}


	@Test
	public void testUpdateOfferingsDescriptionValidationException() throws ValidationException, 
			StoreNotFoundException, OfferingDescriptionNotFoundException {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		// Mocks
		doThrow(new ValidationException(VALIDATION_ERROR)).
				when(offeringsDescriptionValidatorMock).validateOfferingsDescription(newOfferingsDescription, false);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Test
		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 400, ErrorType.BAD_REQUEST, 
				VALIDATION_ERROR, false, true);
	}
	
	@Test
	public void testUpdateOfferingsDescriptionInvalidRDF() throws ValidationException, 
			StoreNotFoundException, OfferingDescriptionNotFoundException {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		// Mocks
		doThrow(new JenaException("A message")).
				when(offeringsDescriptionBoMock).update(offeringsDescription);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Test
		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 400, ErrorType.BAD_REQUEST, 
				INVALID_RDF, true, true);
	}
	
	

	@Test
	public void testUpdateOfferingsDescriptionStoreNotFound() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		//Mocks
		String exceptionMsg = "Store not Found!";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(offeringsDescriptionBoMock).
				findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);

		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 404, ErrorType.NOT_FOUND, 
				exceptionMsg, false, false);
	}

	@Test
	public void testUpdateOfferingsDescriptionNotFound() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		//Mocks
		String exceptionMsg = "Description not Found!";
		doThrow(new OfferingDescriptionNotFoundException(exceptionMsg)).
				when(offeringsDescriptionBoMock).findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);

		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 404, ErrorType.NOT_FOUND, 
				exceptionMsg, false, false);
	}

	@Test
	public void testUpdateOfferingsDescriptionNotFoundException() 
			throws UserNotFoundException, StoreNotFoundException, OfferingDescriptionNotFoundException {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();

		// Mocks
		doThrow(new UserNotFoundException("")).when(authUtilsMock).getLoggedUser();
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Test
		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false, true);
	}
	
	private void testUpdateOfferingsDescriptionDataAccessException(Exception exception, String message)  {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();
		
		//Mocks
		try {
			doThrow(new DataIntegrityViolationException("", exception)).
					when(offeringsDescriptionBoMock).update(offeringsDescription);
			when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
					thenReturn(offeringsDescription);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 400, ErrorType.BAD_REQUEST, 
				message, true, true);
	}
	
	@Test
	public void testUpdateOfferingsDescriptionAlreadyExists() {
		testUpdateOfferingsDescriptionDataAccessException(new MySQLIntegrityConstraintViolationException(),
				DESCRIPTION_ALREADY_EXISTS);
	}
	
	@Test
	public void testUpdateOfferingsDescriptionOtherDataException() {
		Exception exception = new Exception("Too much content");
		testUpdateOfferingsDescriptionDataAccessException(exception, exception.getMessage());
	}

	@Test
	public void testUpdateOfferingsDescriptionNotKnownException() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		OfferingsDescription newOfferingsDescription = new OfferingsDescription();
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(offeringsDescriptionBoMock).update(offeringsDescription);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		testUpdateOfferingsDescriptionGenericError(newOfferingsDescription, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				exceptionMsg, true, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteOfferingsDescriptionNotAllowed() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canDelete(offeringsDescription)).thenReturn(false);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Call the method
		Response res = offeringRegistrationService.deleteOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to delete offering " + DESCRIPTION_NAME);

		// Verify mocks
		verify(storeBoMock, never()).delete(store);	
	}
	
	@Test
	public void testDeleteOfferingsDescriptionNoErrors() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canDelete(offeringsDescription)).thenReturn(true);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Call the method
		Response res = offeringRegistrationService.deleteOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);

		// Verify mocks
		verify(offeringsDescriptionBoMock).delete(offeringsDescription);	
	}
	
	private void testDeleteOfferingsDescriptionGenericError(int status, ErrorType errorType, 
			String message, boolean deleteInvoked) {
		int deleteTimes = deleteInvoked ? 1 : 0;

		try {
			// Mocks
			when(offeringsDescriptionRegistrationAuthMock.canDelete(offeringsDescription)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.deleteOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(offeringsDescriptionBoMock, times(deleteTimes)).delete(offeringsDescription);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test 
	public void testDeleteOfferingsDescriptionStoreNotFound() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(offeringsDescriptionBoMock).
				findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);
		
		testDeleteOfferingsDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test 
	public void testDeleteOfferingsDescriptionNotFound() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		// Mocks
		String exceptionMsg = "Description not found";
		doThrow(new OfferingDescriptionNotFoundException(exceptionMsg)).when(offeringsDescriptionBoMock).
				findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);
		
		testDeleteOfferingsDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test
	public void testDeleteOfferingsDescriptionNotKnownException() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(offeringsDescriptionBoMock).delete(offeringsDescription);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		testDeleteOfferingsDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// GET ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetOfferingsDescriptionNotAllowed() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canGet(offeringsDescription)).thenReturn(false);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Call the method
		Response res = offeringRegistrationService.getOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to get offering " + DESCRIPTION_NAME);
	}
	
	@Test
	public void testGetOfferingsDescriptionNoErrors() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canGet(offeringsDescription)).thenReturn(true);
		when(offeringsDescriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(offeringsDescription);

		// Call the method
		Response res = offeringRegistrationService.getOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((OfferingsDescription) res.getEntity()).isEqualTo(offeringsDescription);
	}
	
	private void testGetOfferingsDescriptionGenericError(int status, ErrorType errorType, String message) {

		try {
			// Mocks
			when(offeringsDescriptionRegistrationAuthMock.canGet(offeringsDescription)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.getOfferingsDescription(STORE_NAME, DESCRIPTION_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testGetOfferingsDescriptionStoreNotFound() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(
				exceptionMsg)).when(offeringsDescriptionBoMock).findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);
		
		testGetOfferingsDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testGetOfferingsDescriptionNotKnownException() 
			throws StoreNotFoundException, OfferingDescriptionNotFoundException {
		
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", 
				new Exception(exceptionMsg))).when(offeringsDescriptionBoMock).
						findByNameAndStore(DESCRIPTION_NAME, STORE_NAME);
		
		testGetOfferingsDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListOfferingsDescriptionsNotAllowed() {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canList(store)).thenReturn(false);

		// Call the method
		Response res = offeringRegistrationService.listOfferingsDescriptionsInStore(STORE_NAME, 0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to list offerings");
	}
	
	private void testListOfferingsDescriptionsInvalidParams(int offset, int max) {
		// Mocks
		when(offeringsDescriptionRegistrationAuthMock.canList(store)).thenReturn(true);

		// Call the method
		Response res = offeringRegistrationService.listOfferingsDescriptionsInStore(STORE_NAME, offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListOfferingsDescriptionsInvalidOffset() {
		testListOfferingsDescriptionsInvalidParams(-1, 100);
	}
	
	@Test
	public void testListOfferingsDescriptionsInvalidMax() {
		testListOfferingsDescriptionsInvalidParams(0, 0);
	}
	
	@Test
	public void testListOfferingsDescriptionsInvalidOffsetMax() {
		testListOfferingsDescriptionsInvalidParams(-1, -1);
	}
	
	private void testListOfferingsDescriptionsNoError(List<OfferingsDescription> storeOfferingsDescriptions, 
			List<OfferingsDescription> returnedOfferingsDescriptions, int offset, int max) {
		store = new Store();
		
		// Mocks
		try {
			when(storeBoMock.findByName(STORE_NAME)).thenReturn(store);
			when(offeringsDescriptionRegistrationAuthMock.canList(store)).thenReturn(true);
			store.setOfferingsDescriptions(storeOfferingsDescriptions);
		} catch(Exception ex) {
			fail ("Exception " + ex + " not expected");
		}
		
		// Call the method
		Response res = offeringRegistrationService.listOfferingsDescriptionsInStore(STORE_NAME, offset, max);
		
		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((OfferingsDescriptions) res.getEntity()).
				getOfferingsDescriptions()).isEqualTo(returnedOfferingsDescriptions);
	}
	
	private List<OfferingsDescription> generateOfferingsDescriptionsList() {
		List<OfferingsDescription> offeringsDescriptions = new ArrayList<OfferingsDescription>();
		for (int i = 0; i < 10; i++) {
			OfferingsDescription offeringsDescription = new OfferingsDescription();
			offeringsDescription.setId(i);
			offeringsDescriptions.add(offeringsDescription);
		}
		
		return offeringsDescriptions;
	}
	
	private void testListOfferingsDescriptionsOnlyOneElement(int elementIndex) {
		List<OfferingsDescription> storeOfferingsDescriptions = generateOfferingsDescriptionsList();
		
		List<OfferingsDescription> returnedOfferingsDescriptions = new ArrayList<OfferingsDescription>();
		returnedOfferingsDescriptions.add(storeOfferingsDescriptions.get(elementIndex));
		
		testListOfferingsDescriptionsNoError(storeOfferingsDescriptions,
				returnedOfferingsDescriptions, elementIndex, 1);
	}
	
	@Test
	public void testListOfferingsDescriptionsOnlyFirst() {
		testListOfferingsDescriptionsOnlyOneElement(0);		
	}
	
	@Test
	public void testListOfferingsDescriptionsOnlyFifth() {
		testListOfferingsDescriptionsOnlyOneElement(5);		
	}
	
	private void testListOfferingsDescriptionsInRange(int initial, int end) {
		List<OfferingsDescription> storeOfferingsDescriptions = generateOfferingsDescriptionsList();

		List<OfferingsDescription> returnedOfferingsDescriptions = new ArrayList<OfferingsDescription>();
		for (int i = initial; i < end; i++) {
			returnedOfferingsDescriptions.add(storeOfferingsDescriptions.get(i));
		}
		
		testListOfferingsDescriptionsNoError(storeOfferingsDescriptions, returnedOfferingsDescriptions, 
				initial, end - initial);
	}
	
	@Test
	public void testListOfferingsDescriptionsFirstTwo() {
		testListOfferingsDescriptionsInRange(0, 2);
	}
	
	@Test
	public void testListOfferingsDescriptionsFirstFive() {
		testListOfferingsDescriptionsInRange(0, 5);
	}
	
	@Test
	public void testListOfferingsDescriptionsTwoThirdAndFoth() {
		testListOfferingsDescriptionsInRange(2, 4);
	}
	
	@Test
	public void testListOfferingsDescriptionsBigMax() {
		List<OfferingsDescription> storeOfferingsDescriptions = generateOfferingsDescriptionsList();		
		testListOfferingsDescriptionsNoError(storeOfferingsDescriptions, storeOfferingsDescriptions, 0, 15);
	}
	
	@Test
	public void testListOfferingsDescriptionsBigMaxAndOffsetIsNotZero() {
		List<OfferingsDescription> storeOfferingsDescriptions = generateOfferingsDescriptionsList();
		List<OfferingsDescription> returnedOfferingsDescriptions = new ArrayList<OfferingsDescription>();
		
		for (int i = 1; i < storeOfferingsDescriptions.size(); i++) {
			returnedOfferingsDescriptions.add(storeOfferingsDescriptions.get(i));
		}
		
		testListOfferingsDescriptionsNoError(storeOfferingsDescriptions, returnedOfferingsDescriptions, 1, 15);
	}

}
