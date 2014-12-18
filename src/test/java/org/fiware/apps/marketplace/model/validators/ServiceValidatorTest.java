package org.fiware.apps.marketplace.model.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.util.Date;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Test;

public class ServiceValidatorTest {
	
	private ServiceValidator serviceValidator = new ServiceValidator();
	
	private static final String MISSING_FIELDS = "name and/or url cannot be null";
	private static final String INVALID_LENGTH_PATTERN = "%s is not valid. (min length: %d, max length: %d)";
	private static final String INVALID_URL = "url is not valid";
	
	private static Service generateValidService() {
		// Additional classes
		User creator = new User();
		creator.setId(1);
		
		Service service = new Service();
		service.setName("service1");
		service.setUrl("https://repo.lab.fi-ware.org/services/service1.rdf");
		service.setDescription("This is an example description");
		service.setRegistrationDate(new Date());
		service.setStore(new Store());
		service.setCreator(creator);
		service.setLasteditor(creator);
		
		return service;
	}
	
	
	private void assertInvalidService(Service service, String expectedMsg, boolean creating) {
		try {
			serviceValidator.validateService(service, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
		}
	}
	
	@Test
	public void testValidBasicService() throws ValidationException {
		Service service = new Service();
		service.setName("service1");
		service.setUrl("https://repo.lab.fi-ware.org/services/service1.rdf");
		
		assertThat(serviceValidator.validateService(service, true)).isTrue();
	}
	
	@Test
	public void testValidComplexService() throws ValidationException {
		Service service = generateValidService();
		assertThat(serviceValidator.validateService(service, true)).isTrue();
	}
	
	@Test
	public void testMissingNameOnCreation() {
		Service service = generateValidService();
		service.setName(null);
		assertInvalidService(service, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingNameOnUpdate() throws ValidationException {
		Service service = generateValidService();
		service.setName(null);
		assertThat(serviceValidator.validateService(service, false)).isTrue();
	}
	
	@Test
	public void testMissingUrlOnCreation() {
		Service service = generateValidService();
		service.setUrl(null);
		assertInvalidService(service, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingUrlOnUpdate() throws ValidationException {
		Service service = generateValidService();
		service.setUrl(null);
		assertThat(serviceValidator.validateService(service, false)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnCreation() throws ValidationException {
		Service service = generateValidService();
		service.setDescription(null);

		assertThat(serviceValidator.validateService(service, true)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnUpdate() throws ValidationException {
		Service service = generateValidService();
		service.setDescription(null);

		assertThat(serviceValidator.validateService(service, false)).isTrue();
	}
	
	@Test
	public void testNameTooShort() {
		Service service = generateValidService();
		service.setName("a");
		assertInvalidService(service, String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testNameTooLong() {
		Service service = generateValidService();
		service.setName("1234567890123456");
		assertInvalidService(service, String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testInvalidURL1() {
		Service service = generateValidService();
		service.setUrl("http://");

		assertInvalidService(service, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL2() {
		Service service = generateValidService();
		service.setUrl("repo.lab.fi-ware.org/services/service1.rdf");

		assertInvalidService(service, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL3() {
		Service service = generateValidService();
		service.setUrl("https://repo.lab.fi-ware.org:222222/services/service1.rdf");

		assertInvalidService(service, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL4() {
		Service service = generateValidService();
		service.setUrl("service");

		assertInvalidService(service, INVALID_URL, false);
	}
	
	@Test
	public void testEmptyDescription() throws ValidationException {
		Service service = generateValidService();
		service.setDescription("");

		// Empty descriptions are allowed
		assertThat(serviceValidator.validateService(service, false)).isTrue();
	}

	@Test
	public void testDescriptionTooLong() throws ValidationException {
		Service service = generateValidService();

		//240 characters (80 * 3)
		service.setDescription("12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890");	

		// Empty descriptions are allowed
		assertInvalidService(service, String.format(INVALID_LENGTH_PATTERN, "description", 0, 200), false);	
	}
}
