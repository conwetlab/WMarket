package org.fiware.apps.marketplace.rest;

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

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Services;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.ServiceValidator;
import org.fiware.apps.marketplace.security.auth.AuthUtils;
import org.fiware.apps.marketplace.security.auth.OfferingRegistrationAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class OfferingRegistrationServiceTest {

	@Mock private StoreBo storeBoMock;
	@Mock private ServiceBo serviceBoMock;
	@Mock private OfferingRegistrationAuth offeringRegistrationAuthMock;
	@Mock private ServiceValidator serviceValidatorMock;
	@Mock private AuthUtils authUtilsMock;

	@InjectMocks private OfferingRegistrationService offeringRegistrationService;

	// Default values
	private Store store;
	private Service service;
	private User user;

	// Other useful constants
	private static final String STORE_NAME = "WStore";
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String SERVICE_ALREADY_EXISTS = "There is already an Offering in this Store with that name/URL";
	private static final String VALIDATION_ERROR = "Validation Exception";
	private static final String DESCRIPTION = "This is a basic description";
	private static final String SERVICE_NAME = "service";
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
		service = new Service();
		service.setDescription(DESCRIPTION);
		service.setName(SERVICE_NAME);
		service.setUrl(URL);
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
	public void testCreateServiceNotAllowed() {
		// Mocks
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(false);

		// Call the method
		Response res = offeringRegistrationService.createService(STORE_NAME, service);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to create offering");

		// Verify mocks
		verify(storeBoMock, never()).save(store);
	}

	@Test
	public void testCreateServiceNoErrors() throws ValidationException {
		// Mocks
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);

		//Call the method
		Response res = offeringRegistrationService.createService(STORE_NAME, service);

		// Verify mocks
		verify(serviceValidatorMock).validateService(service, true);
		verify(serviceBoMock).save(service);

		// Check the response
		assertThat(res.getStatus()).isEqualTo(201);

		// Check that all the parameters of the Store are correct
		// (some of them must have been changed by the method)
		assertThat(service.getRegistrationDate()).isNotNull();
		assertThat(service.getName()).isEqualTo(SERVICE_NAME);
		assertThat(service.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(service.getUrl()).isEqualTo(URL);
		assertThat(service.getCreator()).isEqualTo(user);
		assertThat(service.getLasteditor()).isEqualTo(user);
	}

	private void testCreateServiceGenericError(int statusCode, ErrorType errorType, String errorMsg, boolean saveInvoked) {
		int saveTimes = saveInvoked ? 1 : 0;

		// Call the method
		Response res = offeringRegistrationService.createService(STORE_NAME, service);

		// Verify mocks
		try {
			verify(serviceValidatorMock).validateService(service, true);
			verify(serviceBoMock, times(saveTimes)).save(service);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, errorMsg);
		} catch (ValidationException e) {
			// Impossible...
			fail("exception " + e + " not expected");
		}
	}

	@Test
	public void testCreateServiceValidationException() throws ValidationException {
		// Mocks
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new ValidationException(VALIDATION_ERROR)).when(serviceValidatorMock).validateService(service, true);

		testCreateServiceGenericError(400, ErrorType.BAD_REQUEST, VALIDATION_ERROR, false);
	}

	@Test
	public void testCreateServiceUserNotFoundException() throws ValidationException, UserNotFoundException {
		// Mocks
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new UserNotFoundException("User Not Found exception")).when(authUtilsMock).getLoggedUser();

		testCreateServiceGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false);

	}

	@Test
	public void testCreateServiceStoreNotFoundException() throws ValidationException, StoreNotFoundException {
		// Mocks
		String exceptionMsg = "Store Not Found!";
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new StoreNotFoundException(exceptionMsg)).when(storeBoMock).findByName(STORE_NAME);

		testCreateServiceGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}

	private void testCreateServiceDataAccessException(Exception exception, String message) throws ValidationException {
		// Mock
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new DataIntegrityViolationException("", exception)).when(serviceBoMock).save(service);

		testCreateServiceGenericError(400, ErrorType.BAD_REQUEST, message, true);
	}

	@Test
	public void testCreateServiceAlreadyExists() throws ValidationException {
		testCreateServiceDataAccessException(new MySQLIntegrityConstraintViolationException(), 
				SERVICE_ALREADY_EXISTS);
	}

	@Test
	public void testCreateServiceOtherDataException() throws ValidationException {
		Exception exception = new Exception("Too much content");
		testCreateServiceDataAccessException(exception, exception.getMessage());
	}

	@Test
	public void testCreteServiceNotKnowException() throws ValidationException {
		// Mock
		String exceptionMsg = "SERVER ERROR";
		when(offeringRegistrationAuthMock.canCreate()).thenReturn(true);
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(serviceBoMock).save(service);

		testCreateServiceGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateServiceNotAllowed() throws ServiceNotFoundException, StoreNotFoundException {
		Service newService = new Service();

		// Mocks
		when(offeringRegistrationAuthMock.canUpdate(service)).thenReturn(false);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Call the method
		Response res = offeringRegistrationService.updateService(STORE_NAME, SERVICE_NAME, newService);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to update offering " + SERVICE_NAME);

		// Verify mocks
		verify(serviceBoMock, never()).update(service);	
	}

	private void testUpdateServiceField(Service newService) {
		try {
			// Mock
			when(offeringRegistrationAuthMock.canUpdate(service)).thenReturn(true);
			when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

			// Call the method
			Response res = offeringRegistrationService.updateService(STORE_NAME, SERVICE_NAME, newService);

			// Verify mocks
			verify(serviceValidatorMock).validateService(newService, false);
			verify(serviceBoMock).update(service);

			// Assertions
			assertThat(res.getStatus()).isEqualTo(200);

			// New values
			String newStoreName = newService.getName() != null ? newService.getName() : service.getName();
			assertThat(service.getName()).isEqualTo(newStoreName);

			String newStoreUrl = newService.getUrl() != null ? newService.getUrl() : service.getUrl();
			assertThat(service.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = newService.getDescription() != null ? newService.getDescription() : service.getDescription();
			assertThat(service.getDescription()).isEqualTo(newStoreDescription);
		} catch (Exception ex) {
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateServiceName() {
		Service newService = new Service();
		newService.setName("new_name");
		testUpdateServiceField(newService);
	}

	@Test
	public void testUpdateServiceUrl() {
		Service newService = new Service();
		newService.setUrl("https://repo.lab.fi-ware.org/new_description.rdf");
		testUpdateServiceField(newService);
	}

	@Test
	public void testUpdateServiceDescription() {
		Service newService = new Service();
		newService.setDescription("New Description");
		testUpdateServiceField(newService);
	}

	private void testUpdateServiceGenericError(Service newService, int status, ErrorType errorType, 
			String message, boolean updateInvoked, boolean verifyInvoked) {
		int updateTimes = updateInvoked ? 1 : 0;
		int verifyTimes = verifyInvoked ? 1 : 0;

		try {
			// Mocks
			when(offeringRegistrationAuthMock.canUpdate(service)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.updateService(STORE_NAME, SERVICE_NAME, newService);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(serviceValidatorMock, times(verifyTimes)).validateService(newService, false);
			verify(serviceBoMock, times(updateTimes)).update(service);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}


	@Test
	public void testUpdateServiceValidationException() throws ValidationException, 
			StoreNotFoundException, ServiceNotFoundException {
		Service newService = new Service();

		// Mocks
		doThrow(new ValidationException(VALIDATION_ERROR)).when(serviceValidatorMock).validateService(
				newService, false);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Test
		testUpdateServiceGenericError(newService, 400, ErrorType.BAD_REQUEST, 
				VALIDATION_ERROR, false, true);
	}

	@Test
	public void testUpdateServiceStoreNotFound() throws StoreNotFoundException, ServiceNotFoundException {
		Service newService = new Service();

		//Mocks
		String exceptionMsg = "Store not Found!";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);

		testUpdateServiceGenericError(newService, 404, ErrorType.NOT_FOUND, exceptionMsg, false, false);
	}

	@Test
	public void testUpdateServiceServiceNotFound() throws StoreNotFoundException, ServiceNotFoundException {
		Service newService = new Service();

		//Mocks
		String exceptionMsg = "Service not Found!";
		doThrow(new ServiceNotFoundException(exceptionMsg)).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);

		testUpdateServiceGenericError(newService, 404, ErrorType.NOT_FOUND, exceptionMsg, false, false);
	}

	@Test
	public void testUpdateServiceNotFoundException() throws UserNotFoundException, StoreNotFoundException, 
			ServiceNotFoundException {
		Service newService = new Service();

		// Mocks
		doThrow(new UserNotFoundException("")).when(authUtilsMock).getLoggedUser();
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Test
		testUpdateServiceGenericError(newService, 500, ErrorType.INTERNAL_SERVER_ERROR, 
				"There was an error retrieving the user from the database", false, true);
	}
	
	private void testUpdateServiceDataAccessException(Exception exception, String message)  {
		Service newService = new Service();
		
		//Mocks
		try {
			doThrow(new DataIntegrityViolationException("", exception)).when(serviceBoMock).update(service);
			when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		testUpdateServiceGenericError(newService, 400, ErrorType.BAD_REQUEST, message, true, true);
	}
	
	@Test
	public void testUpdateServiceAlreadyExists() {
		testUpdateServiceDataAccessException(new MySQLIntegrityConstraintViolationException(),
				SERVICE_ALREADY_EXISTS);
	}
	
	@Test
	public void testUpdateServiceOtherDataException() {
		Exception exception = new Exception("Too much content");
		testUpdateServiceDataAccessException(exception, exception.getMessage());
	}

	@Test
	public void testUpdateServiceNotKnownException() throws StoreNotFoundException, ServiceNotFoundException {
		Service newService = new Service();
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(serviceBoMock).update(service);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		testUpdateServiceGenericError(newService, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteServiceNotAllowed() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		when(offeringRegistrationAuthMock.canDelete(service)).thenReturn(false);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Call the method
		Response res = offeringRegistrationService.deleteService(STORE_NAME, SERVICE_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to delete offering " + SERVICE_NAME);

		// Verify mocks
		verify(storeBoMock, never()).delete(store);	
	}
	
	@Test
	public void testDeleteServiceNoErrors() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		when(offeringRegistrationAuthMock.canDelete(service)).thenReturn(true);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Call the method
		Response res = offeringRegistrationService.deleteService(STORE_NAME, SERVICE_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(204);

		// Verify mocks
		verify(serviceBoMock).delete(service);	
	}
	
	private void testDeleteServiceGenericError(int status, ErrorType errorType, 
			String message, boolean deleteInvoked) {
		int deleteTimes = deleteInvoked ? 1 : 0;

		try {
			// Mocks
			when(offeringRegistrationAuthMock.canDelete(service)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.deleteService(STORE_NAME, SERVICE_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);

			// Verify mocks
			verify(serviceBoMock, times(deleteTimes)).delete(service);	
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test 
	public void testDeleteServiceStoreNotFound() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(exceptionMsg)).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);
		
		testDeleteServiceGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test 
	public void testDeleteServiceServiceNotFound() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		String exceptionMsg = "Service not found";
		doThrow(new ServiceNotFoundException(exceptionMsg)).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);
		
		testDeleteServiceGenericError(404, ErrorType.NOT_FOUND, exceptionMsg, false);
	}
	
	@Test
	public void testDeleteServiceNotKnownException() throws StoreNotFoundException, ServiceNotFoundException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(serviceBoMock).delete(service);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		testDeleteServiceGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg, true);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// GET ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetServiceNotAllowed() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		when(offeringRegistrationAuthMock.canGet(service)).thenReturn(false);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Call the method
		Response res = offeringRegistrationService.getService(STORE_NAME, SERVICE_NAME);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to get offering " + SERVICE_NAME);
	}
	
	@Test
	public void testGetServiceNoErrors() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		when(offeringRegistrationAuthMock.canGet(service)).thenReturn(true);
		when(serviceBoMock.findByNameAndStore(SERVICE_NAME, STORE_NAME)).thenReturn(service);

		// Call the method
		Response res = offeringRegistrationService.getService(STORE_NAME, SERVICE_NAME);

		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Service) res.getEntity()).isEqualTo(service);
	}
	
	private void testGetServiceGenericError(int status, ErrorType errorType, String message) {

		try {
			// Mocks
			when(offeringRegistrationAuthMock.canGet(service)).thenReturn(true);

			// Call the method
			Response res = offeringRegistrationService.getService(STORE_NAME, SERVICE_NAME);

			// Assertions
			GenericRestTestUtils.checkAPIError(res, status, errorType, message);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected.");
		}
	}
	
	@Test
	public void testGetServiceStoreNotFound() throws StoreNotFoundException, ServiceNotFoundException {
		// Mocks
		String exceptionMsg = "Store not found";
		doThrow(new StoreNotFoundException(
				exceptionMsg)).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);
		
		testGetServiceGenericError(404, ErrorType.NOT_FOUND, exceptionMsg);
	}
	
	@Test
	public void testGetServiceNotKnownException() throws StoreNotFoundException, ServiceNotFoundException {
		String exceptionMsg = "SERVER ERROR";
		doThrow(new RuntimeException("", 
				new Exception(exceptionMsg))).when(serviceBoMock).findByNameAndStore(SERVICE_NAME, STORE_NAME);
		
		testGetServiceGenericError(500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
	

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// LIST ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListServicesNotAllowed() {
		// Mocks
		when(offeringRegistrationAuthMock.canList(store)).thenReturn(false);

		// Call the method
		Response res = offeringRegistrationService.listServicesInStore(STORE_NAME, 0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, "You are not authorized to list offerings");
	}
	
	private void testListServicesInvalidParams(int offset, int max) {
		// Mocks
		when(offeringRegistrationAuthMock.canList(store)).thenReturn(true);

		// Call the method
		Response res = offeringRegistrationService.listServicesInStore(STORE_NAME, offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, OFFSET_MAX_INVALID);
	}
	
	@Test
	public void testListServicesInvalidOffset() {
		testListServicesInvalidParams(-1, 100);
	}
	
	@Test
	public void testListServicesInvalidMax() {
		testListServicesInvalidParams(0, 0);
	}
	
	@Test
	public void testListServicesInvalidOffsetMax() {
		testListServicesInvalidParams(-1, -1);
	}
	
	private void testListServicesNoError(List<Service> storeServices, List<Service> returnedServices, 
			int offset, int max) {
		store = new Store();
		
		// Mocks
		try {
			when(storeBoMock.findByName(STORE_NAME)).thenReturn(store);
			when(offeringRegistrationAuthMock.canList(store)).thenReturn(true);
			store.setServices(storeServices);
		} catch(Exception ex) {
			fail ("Exception " + ex + " not expected");
		}
		
		// Call the method
		Response res = offeringRegistrationService.listServicesInStore(STORE_NAME, offset, max);
		
		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Services) res.getEntity()).getServices()).isEqualTo(returnedServices);
	}
	
	private List<Service> generateServicesList() {
		List<Service> services = new ArrayList<Service>();
		for (int i = 0; i < 10; i++) {
			Service service = new Service();
			service.setId(i);
			services.add(service);
		}
		
		return services;
	}
	
	private void testListServicesOnlyOneElement(int elementIndex) {
		List<Service> storeServices = generateServicesList();
		
		List<Service> returnedServices = new ArrayList<Service>();
		returnedServices.add(storeServices.get(elementIndex));
		
		testListServicesNoError(storeServices, returnedServices, elementIndex, 1);
	}
	
	@Test
	public void testListServicesOnlyFirst() {
		testListServicesOnlyOneElement(0);		
	}
	
	@Test
	public void testListServicesOnlyFifth() {
		testListServicesOnlyOneElement(5);		
	}
	
	private void testListServicesInRange(int initial, int end) {
		List<Service> storeServices = generateServicesList();

		List<Service> returnedServices = new ArrayList<Service>();
		for (int i = initial; i < end; i++) {
			returnedServices.add(storeServices.get(i));
		}
		
		testListServicesNoError(storeServices, returnedServices, initial, end - initial);
	}
	
	@Test
	public void testListServicesFirstTwo() {
		testListServicesInRange(0, 2);
	}
	
	@Test
	public void testListServicesFirstFive() {
		testListServicesInRange(0, 5);
	}
	
	@Test
	public void testListServicesTwoThirdAndFoth() {
		testListServicesInRange(2, 4);
	}
	
	@Test
	public void testListServicesBigMax() {
		List<Service> storeServices = generateServicesList();		
		testListServicesNoError(storeServices, storeServices, 0, 15);
	}
	
	@Test
	public void testListServicesBigMaxAndOffsetIsNotZero() {
		List<Service> storeServices = generateServicesList();
		List<Service> returnedServices = new ArrayList<Service>();
		
		for (int i = 1; i < storeServices.size(); i++) {
			returnedServices.add(storeServices.get(i));
		}
		
		testListServicesNoError(storeServices, returnedServices, 1, 15);
	}

}
