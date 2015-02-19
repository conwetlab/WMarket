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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Descriptions;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.DescriptionValidator;
import org.fiware.apps.marketplace.rest.v2.DescriptionService;
import org.fiware.apps.marketplace.security.auth.DescriptionAuth;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.hp.hpl.jena.shared.JenaException;

public class DescriptionRegistrationServiceTest {

	@Mock private UserBo userBoMock;
	@Mock private StoreBo storeBoMock;
	@Mock private DescriptionBo descriptionBoMock;
	@Mock private DescriptionAuth descriptionAuthMock;
	@Mock private DescriptionValidator descriptionValidatorMock;

	@InjectMocks private DescriptionService descriptionRegistrationService;

	// Default values
	private Store store;
	private Description description;
	private User user;
	private UriInfo uri;

	// Other useful constants
	private static final String STORE_NAME = "wstore";
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String DESCRIPTION_ALREADY_EXISTS = 
			"There is already a Description in this Store with that name";
	private static final String VALIDATION_ERROR = "Validation Exception";
	private static final String DESCRIPTION = "This is a basic description";
	private static final String DESCRIPTION_DISPLAY_NAME = "Offerings Description";
	private static final String DESCRIPTION_NAME = "offerings-description";
	private static final String INVALID_RDF = "Your RDF could not be parsed";
	private static final String URL = "https://repo.lab.fi-ware.org/description.rdf";
	private static final String PATH = "/api/store/" + STORE_NAME + "/offerings_description";
	
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
		description = new Description();
		description.setDescription(DESCRIPTION);
		description.setDisplayName(DESCRIPTION_DISPLAY_NAME);
		description.setUrl(URL);
	}
	
	@Before
	public void setUpUri() {
		uri = mock(UriInfo.class);
		when(uri.getPath()).thenReturn(PATH);
	}
	
	@Before
	public void initAuthUtils() throws UserNotFoundException {
		user = new User();
		when(userBoMock.getCurrentUser()).thenReturn(user);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreateDescriptionNotAllowed() {
		// Mocks
		when(descriptionAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = descriptionRegistrationService.createDescription(uri, STORE_NAME, description);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to create description");

		// Verify mocks
		verify(storeBoMock, never()).save(store);
	}

	@Test
	public void testCreateDescriptionNoErrors() throws Exception {
		// Mocks
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, Description.class).setName(DESCRIPTION_NAME);
				return null;
			}
		}).when(descriptionBoMock).save(description);

		//Call the method
		Response res = descriptionRegistrationService.createDescription(uri, STORE_NAME, description);

		// Verify mocks
		verify(descriptionValidatorMock).validateDescription(description, true);
		verify(descriptionBoMock).save(description);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(res.getHeaders().get("Location").get(0).toString()).isEqualTo(PATH + "/" + DESCRIPTION_NAME);

		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		assertThat(description.getRegistrationDate()).isNotNull();
		assertThat(description.getName()).isEqualTo(DESCRIPTION_NAME);
		assertThat(description.getDisplayName()).isEqualTo(DESCRIPTION_DISPLAY_NAME);
		assertThat(description.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(description.getUrl()).isEqualTo(URL);
		assertThat(description.getCreator()).isEqualTo(user);
		assertThat(description.getLasteditor()).isEqualTo(user);
	}

	private void testCreateDescriptionGenericError(int statusCode, ErrorType errorType, 
			String errorMsg, boolean saveInvoked) {
		
		int saveTimes = saveInvoked ? 1 : 0;

		// Call the method
		Response res = descriptionRegistrationService.createDescription(uri, STORE_NAME, description);

		// Verify mocks
		try {
			verify(descriptionValidatorMock).validateDescription(description, true);
			verify(descriptionBoMock, times(saveTimes)).save(description);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, errorMsg);
		} catch (Exception e) {
			// Impossible...
			fail("exception " + e + " not expected");
		}
	}

	@Test
	public void testCreateDescriptionValidationException() throws ValidationException {
		// Mocks
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).
				when(descriptionValidatorMock).validateDescription(description, true);

		testCreateDescriptionGenericError(400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false);
	}
	
	@Test
	public void testCreateDescriptionInvalidRDF() throws Exception {
		//Mocks
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(new JenaException("Some message")).
				when(descriptionBoMock).save(description);
		
		testCreateDescriptionGenericError(400, ErrorType.BAD_REQUEST, INVALID_RDF, true);
	}

	@Test
	public void testCreateDescriptionUserNotFoundException() 
			throws ValidationException, UserNotFoundException {
		// Mocks
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(new UserNotFoundException("User Not Found exception")).when(userBoMock).getCurrentUser();

		testCreateDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false);

	}

	@Test
	public void testCreateDescriptionStoreNotFoundException() 
			throws ValidationException, StoreNotFoundException {
		// Mocks
		String exceptionMsg = "Store Not Found!";
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(STORE_NAME);

		testCreateDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}

	private void testCreateDescriptionHibernateException(Exception exception, String message) 
			throws Exception {
		// Mock
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(exception).when(descriptionBoMock).save(description);

		testCreateDescriptionGenericError(400, ErrorType.BAD_REQUEST, message, true);
	}

	@Test
	public void testCreateDescriptionAlreadyExists() throws Exception {
		testCreateDescriptionHibernateException(VIOLATION_EXCEPTION, 
				DESCRIPTION_ALREADY_EXISTS);
	}

	@Test
	public void testCreateDescriptionOtherDataException() throws Exception {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testCreateDescriptionHibernateException(exception, exception.getCause().getMessage());
	}

	@Test
	public void testCreteDescriptionNotKnowException() throws Exception {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(descriptionAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(descriptionBoMock).save(description);

		testCreateDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateDescriptionNotAllowed() 
			throws Exception {
		
		Description newDescription = new Description();

		// Mocks
		when(descriptionAuthMock.canUpdate(description)).thenReturn(false);
		when(descriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(description);

		// Call the method
		Response res = descriptionRegistrationService.
				updateDescription(STORE_NAME, DESCRIPTION_NAME, newDescription);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to update description " + DESCRIPTION_NAME);

		// Verify mocks
		verify(descriptionBoMock, never()).update(description);	
	}

	private void testUpdateDescriptionField(Description newDescription) {
		try {
			// Mock
			when(descriptionAuthMock.canUpdate(description)).thenReturn(true);
			when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
					thenReturn(description);

			// Get the 
			String previousName = description.getName();
			
			// Call the method
			Response res = descriptionRegistrationService.
					updateDescription(STORE_NAME, DESCRIPTION_NAME, newDescription);

			// Verify mocks
			verify(descriptionValidatorMock).validateDescription(newDescription, false);
			verify(descriptionBoMock).update(description);

			// Assertions
			assertThat(res.getStatus()).isEqualTo(200);
			
			// Assert that description name has not changed
			assertThat(description.getName()).isEqualTo(previousName);

			// New values
			String newStoreName = newDescription.getName() != null ? 
					newDescription.getName() : description.getName();
			assertThat(description.getName()).isEqualTo(newStoreName);

			String newStoreUrl = newDescription.getUrl() != null ? 
					newDescription.getUrl() : description.getUrl();
			assertThat(description.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = newDescription.getDescription() != null ? 
					newDescription.getDescription() : description.getDescription();
			assertThat(description.getDescription()).isEqualTo(newStoreDescription);
		} catch (Exception ex) {
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateDescriptionName() {
		Description newDescription = new Description();
		newDescription.setDisplayName("new_name");
		testUpdateDescriptionField(newDescription);
	}

	@Test
	public void testUpdateDescriptionUrl() {
		Description newDescription = new Description();
		newDescription.setUrl("https://repo.lab.fi-ware.org/new_description.rdf");
		testUpdateDescriptionField(newDescription);
	}

	@Test
	public void testUpdateDescriptionDescription() {
		Description newDescription = new Description();
		newDescription.setDescription("New Description");
		testUpdateDescriptionField(newDescription);
	}

	private void testUpdateDescriptionGenericError(Description newDescription, 
			int status, ErrorType errorType, String message, boolean updateInvoked, boolean verifyInvoked) {
		int updateTimes = updateInvoked ? 1 : 0;
		int verifyTimes = verifyInvoked ? 1 : 0;

		try {
			// Mocks
			when(descriptionAuthMock.canUpdate(description)).thenReturn(true);

			// Call the method
			Response res = descriptionRegistrationService.
					updateDescription(STORE_NAME, DESCRIPTION_NAME, newDescription);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(descriptionValidatorMock, times(verifyTimes)).
					validateDescription(newDescription, false);
			verify(descriptionBoMock, times(updateTimes)).update(description);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}


	@Test
	public void testUpdateDescriptionValidationException() throws ValidationException, 
			StoreNotFoundException, DescriptionNotFoundException {
		Description newDescription = new Description();

		// Mocks
		doThrow(new ValidationException(VALIDATION_ERROR)).
				when(descriptionValidatorMock).validateDescription(newDescription, false);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		// Test
		testUpdateDescriptionGenericError(newDescription, 400, ErrorType.BAD_REQUEST, 
				VALIDATION_ERROR, false, true);
	}
	
	@Test
	public void testUpdateDescriptionInvalidRDF() throws Exception {
		Description newDescription = new Description();

		// Mocks
		doThrow(new JenaException("A message")).
				when(descriptionBoMock).update(description);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		// Test
		testUpdateDescriptionGenericError(newDescription, 400, ErrorType.BAD_REQUEST, 
				INVALID_RDF, true, true);
	}
	
	
	@Test
	public void testUpdateDescriptionStoreNotFound() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		Description newDescription = new Description();

		//Mocks
		String exceptionMsg = "Store not Found!";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(descriptionBoMock).
				findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);

		testUpdateDescriptionGenericError(newDescription, 404, ErrorType.NOT_FOUND, 
				exceptionMsg, false, false);
	}

	@Test
	public void testUpdateDescriptionNotFound() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		Description newDescription = new Description();

		//Mocks
		String exceptionMsg = "Description not Found!";
		doThrow(new DescriptionNotFoundException(exceptionMsg)).
				when(descriptionBoMock).findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);

		testUpdateDescriptionGenericError(newDescription, 404, ErrorType.NOT_FOUND, 
				exceptionMsg, false, false);
	}

	@Test
	public void testUpdateDescriptionNotFoundException() 
			throws UserNotFoundException, StoreNotFoundException, DescriptionNotFoundException {
		Description newDescription = new Description();

		// Mocks
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		// Test
		testUpdateDescriptionGenericError(newDescription, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false, true);
	}
	
	private void testUpdateDescriptionHibernateException(Exception exception, String message)  {
		Description newDescription = new Description();
		
		//Mocks
		try {
			doThrow(exception).
					when(descriptionBoMock).update(description);
			when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
					thenReturn(description);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		testUpdateDescriptionGenericError(newDescription, 400, ErrorType.BAD_REQUEST, 
				message, true, true);
	}
	
	@Test
	public void testUpdateDescriptionAlreadyExists() {
		testUpdateDescriptionHibernateException(VIOLATION_EXCEPTION,
				DESCRIPTION_ALREADY_EXISTS);
	}
	
	@Test
	public void testUpdateDescriptionOtherDataException() {
		HibernateException exception = new HibernateException(new Exception("Too much content"));
		testUpdateDescriptionHibernateException(exception, exception.getCause().getMessage());
	}

	@Test
	public void testUpdateDescriptionNotKnownException() 
			throws Exception {
		Description newDescription = new Description();
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(descriptionBoMock).update(description);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		testUpdateDescriptionGenericError(newDescription, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				exceptionMsg, true, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteDescriptionNotAllowed() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		// Mocks
		when(descriptionAuthMock.canDelete(description)).thenReturn(false);
		when(descriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(description);

		// Call the method
		Response res = descriptionRegistrationService.deleteDescription(
				STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to delete description " + DESCRIPTION_NAME);

		// Verify mocks
		verify(storeBoMock, never()).delete(store);	
	}
	
	@Test
	public void testDeleteDescriptionNoErrors() 
			throws Exception {
		
		// Mocks
		when(descriptionAuthMock.canDelete(description)).thenReturn(true);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		// Call the method
		Response res = descriptionRegistrationService.deleteDescription(
				STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);

		// Verify mocks
		verify(descriptionBoMock).delete(description);	
	}
	
	private void testDeleteDescriptionGenericError(int status, ErrorType errorType, 
			String message, boolean deleteInvoked) {
		int deleteTimes = deleteInvoked ? 1 : 0;

		try {
			// Mocks
			when(descriptionAuthMock.canDelete(description)).thenReturn(true);

			// Call the method
			Response res = descriptionRegistrationService.deleteDescription(
					STORE_NAME, DESCRIPTION_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(descriptionBoMock, times(deleteTimes)).delete(description);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test 
	public void testDeleteDescriptionStoreNotFound() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(descriptionBoMock).
				findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
		
		testDeleteDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test 
	public void testDeleteDescriptionNotFound() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		// Mocks
		String exceptionMsg = "Description not found";
		doThrow(new DescriptionNotFoundException(exceptionMsg)).when(descriptionBoMock).
				findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
		
		testDeleteDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test
	public void testDeleteDescriptionNotKnownException() 
			throws Exception {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).
				when(descriptionBoMock).delete(description);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		testDeleteDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// GET ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetDescriptionNotAllowed() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		// Mocks
		when(descriptionAuthMock.canGet(description)).thenReturn(false);
		when(descriptionBoMock.findByNameAndStore(DESCRIPTION_NAME, STORE_NAME)).
				thenReturn(description);

		// Call the method
		Response res = descriptionRegistrationService.getDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to get description " + DESCRIPTION_NAME);
	}
	
	@Test
	public void testGetDescriptionNoErrors() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		// Mocks
		when(descriptionAuthMock.canGet(description)).thenReturn(true);
		when(descriptionBoMock.findByNameAndStore(STORE_NAME, DESCRIPTION_NAME)).
				thenReturn(description);

		// Call the method
		Response res = descriptionRegistrationService.getDescription(STORE_NAME, DESCRIPTION_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Description) res.getEntity()).isEqualTo(description);
	}
	
	private void testGetDescriptionGenericError(int status, ErrorType errorType, String message) {

		try {
			// Mocks
			when(descriptionAuthMock.canGet(description)).thenReturn(true);

			// Call the method
			Response res = descriptionRegistrationService.getDescription(
					STORE_NAME, DESCRIPTION_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testGetDescriptionStoreNotFound() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(
				exceptionMsg)).when(descriptionBoMock).findByNameAndStore(
						STORE_NAME, DESCRIPTION_NAME);
		
		testGetDescriptionGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testGetDescriptionNotKnownException() 
			throws StoreNotFoundException, DescriptionNotFoundException {
		
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", 
				new Exception(exceptionMsg))).when(descriptionBoMock).
						findByNameAndStore(STORE_NAME, DESCRIPTION_NAME);
		
		testGetDescriptionGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListDescriptionsNotAllowed() {
		// Mocks
		when(descriptionAuthMock.canList(store)).thenReturn(false);

		// Call the method
		Response res = descriptionRegistrationService.listDescriptionsInStore(STORE_NAME, 0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to list descriptions");
	}
	
	private void testListDescriptionsInvalidParams(int offset, int max) {
		// Mocks
		when(descriptionAuthMock.canList(store)).thenReturn(true);

		// Call the method
		Response res = descriptionRegistrationService.listDescriptionsInStore(STORE_NAME, offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListDescriptionsInvalidOffset() {
		testListDescriptionsInvalidParams(-1, 100);
	}
	
	@Test
	public void testListDescriptionsInvalidMax() {
		testListDescriptionsInvalidParams(0, 0);
	}
	
	@Test
	public void testListDescriptionsInvalidOffsetMax() {
		testListDescriptionsInvalidParams(-1, -1);
	}
	
	@Test
	public void testListDescriptionsNoErrors() throws StoreNotFoundException {
		List<Description> descriptions = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Description description = new Description();
			description.setId(i);
			descriptions.add(description);
		}
		
		// Mocks
		when(descriptionAuthMock.canList(store)).thenReturn(true);
		when(descriptionBoMock.getStoreDescriptionsPage(eq(STORE_NAME), anyInt(), anyInt())).thenReturn(descriptions);
		
		// Call the method
		int offset = 5;
		int max = 8;
		Response res = descriptionRegistrationService.listDescriptionsInStore(STORE_NAME, offset, max);
		
		// Chceks
		verify(descriptionBoMock).getStoreDescriptionsPage(STORE_NAME, offset, max);
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Descriptions) res.getEntity()).
				getDescriptions()).isEqualTo(descriptions);

	}
	
	@Test
	public void testListDescriptionsException() throws StoreNotFoundException {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(descriptionBoMock)
				.getStoreDescriptionsPage(eq(STORE_NAME), anyInt(), anyInt());
		when(descriptionAuthMock.canList(store)).thenReturn(true);

		// Call the method
		int offset = 0;
		int max = 100;
		Response res = descriptionRegistrationService.listDescriptionsInStore(STORE_NAME, offset, max);
		
		// Verify
		verify(descriptionBoMock).getStoreDescriptionsPage(STORE_NAME, offset, max);
		
		// Check exception
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
}
