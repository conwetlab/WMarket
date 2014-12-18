package org.fiware.apps.marketplace.model.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.util.Date;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Test;

public class StoreValidatorTest {

	private StoreValidator storeValidator = new StoreValidator();

	private static final String MISSING_FIELDS = "name and/or url cannot be null";
	private static final String INVALID_LENGTH_PATTERN = "%s is not valid. (min length: %d, max length: %d)";
	private static final String INVALID_URL = "url is not valid";

	private static Store generateValidStore() {
		Store store = new Store();
		store.setCreator(new User());
		store.setDescription("This is a basic description");
		store.setLasteditor(store.getCreator());
		store.setName("store");
		store.setUrl("https://store.lab.fi-ware.org");
		store.setRegistrationDate(new Date());

		return store;
	}

	private void assertInvalidStore(Store store, String expectedMsg, boolean creating) {
		try {
			storeValidator.validateStore(store, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
		}
	}

	@Test
	public void testValidBasicStore() throws ValidationException {
		Store store = new Store();
		store.setName("store");
		store.setUrl("https://store.lab.fi-ware.org");

		assertThat(storeValidator.validateStore(store, true)).isTrue();
	}

	@Test
	public void testValidComplexStore() throws ValidationException {
		Store store = generateValidStore();
		assertThat(storeValidator.validateStore(store, true)).isTrue();
	}

	@Test
	public void testMissingNameOnCreation() {
		Store store = generateValidStore();
		store.setName(null);

		assertInvalidStore(store, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingNameOnUpdate() throws ValidationException {
		Store store = generateValidStore();
		store.setName(null);

		assertThat(storeValidator.validateStore(store, false)).isTrue();
	}

	@Test
	public void testMissingUrlOnCreation() {
		Store store = generateValidStore();
		store.setUrl(null);

		assertInvalidStore(store, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingUrlOnUpdate() throws ValidationException {
		Store store = generateValidStore();
		store.setUrl(null);

		assertThat(storeValidator.validateStore(store, false)).isTrue();
	}

	@Test
	public void testMissingDescriptionOnCreation() throws ValidationException {
		Store store = generateValidStore();
		store.setDescription(null);

		assertThat(storeValidator.validateStore(store, true)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnUpdate() throws ValidationException {
		Store store = generateValidStore();
		store.setDescription(null);

		assertThat(storeValidator.validateStore(store, false)).isTrue();
	}

	@Test
	public void testNameTooShort() {
		Store store = generateValidStore();
		store.setName("a");

		assertInvalidStore(store, String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}

	@Test
	public void testUserNameTooLong() {
		Store store = generateValidStore();
		store.setName("1234567890123456");

		assertInvalidStore(store, String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}

	@Test
	public void testInvalidURL1() {
		Store store = generateValidStore();
		store.setUrl("http://");

		assertInvalidStore(store, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL2() {
		Store store = generateValidStore();
		store.setUrl("store.lab.fi-ware.org");

		assertInvalidStore(store, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL3() {
		Store store = generateValidStore();
		store.setUrl("https://store.lab.fi-ware.org:222222");

		assertInvalidStore(store, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL4() {
		Store store = generateValidStore();
		store.setUrl("store");

		assertInvalidStore(store, INVALID_URL, false);
	}

	@Test
	public void testEmptyDescription() throws ValidationException {
		Store store = generateValidStore();
		store.setDescription("");

		// Empty descriptions are allowed
		assertThat(storeValidator.validateStore(store, false)).isTrue();
	}

	@Test
	public void testDescriptionTooLong() throws ValidationException {
		Store store = generateValidStore();

		//240 characters (80 * 3)
		store.setDescription("12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890");	

		// Empty descriptions are allowed
		assertInvalidStore(store, String.format(INVALID_LENGTH_PATTERN, "description", 0, 200),false);	
	}

}
