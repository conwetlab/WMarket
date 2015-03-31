package org.fiware.apps.marketplace.model.validators;

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
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.fiware.apps.marketplace.dao.DescriptionDao;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DescriptionValidatorTest {
	
	@Mock private DescriptionDao descriptionDaoMock;
	@InjectMocks private DescriptionValidator descriptionValidator = new DescriptionValidator();
	
	private static final String MISSING_FIELDS_MSG = "This field is required.";
	private static final String TOO_SHORT_PATTERN = "This field must be at least %d chars.";
	private static final String TOO_LONG_PATTERN = "This field must not exceed %d chars.";
	private static final String INVALID_URL = "This field must be a valid URL.";
	
	private static Description generateValiddescription() {
		// Additional classes
		User creator = new User();
		creator.setId(1);
		
		Description description = new Description();
		description.setDisplayName("description1");
		description.setUrl("https://repo.lab.fi-ware.org/offerings/offering1.rdf");
		description.setDescription("This is an example description");
		description.setRegistrationDate(new Date());
		description.setStore(new Store());
		description.setCreator(creator);
		description.setLasteditor(creator);
		
		return description;
	}
	
	
	private void assertInvalidDescription(Description description, String field,
			String expectedMsg, boolean creating) {
		try {
			descriptionValidator.validateDescription(description, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
			assertThat(ex.getFieldName()).isEqualTo(field);
		}
	}
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(descriptionDaoMock.isNameAvailableInStore(anyString(), anyString())).thenReturn(true);
		when(descriptionDaoMock.isURLAvailableInStore(anyString(), anyString())).thenReturn(true);
		
	}
	
	@Test
	public void testValidBasicDescription() throws ValidationException {
		Store store = mock(Store.class);
		when(store.getName()).thenReturn("store display name");
		Description description = new Description();
		description.setDisplayName("description1");
		description.setStore(store);
		description.setUrl("https://repo.lab.fi-ware.org/offerings/offering1.rdf");
		
		descriptionValidator.validateDescription(description, true);
	}
	
	@Test
	public void testValidComplexDescription() throws ValidationException {
		Description description = generateValiddescription();
		descriptionValidator.validateDescription(description, true);
	}
	
	@Test
	public void testMissingDisplayNameOnCreation() {
		Description description = generateValiddescription();
		description.setDisplayName(null);
		assertInvalidDescription(description, "displayName", MISSING_FIELDS_MSG, true);
	}
	
	@Test
	public void testMissingDisplayNameOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setName(null);
		descriptionValidator.validateDescription(description, false);
	}
	
	@Test
	public void testMissingUrlOnCreation() {
		Description description = generateValiddescription();
		description.setUrl(null);
		assertInvalidDescription(description, "url", MISSING_FIELDS_MSG, true);
	}
	
	@Test
	public void testMissingUrlOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setUrl(null);
		descriptionValidator.validateDescription(description, false);
	}
	
	@Test
	public void testMissingDescriptionOnCreation() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription(null);

		descriptionValidator.validateDescription(description, true);
	}
	
	@Test
	public void testMissingDescriptionOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription(null);

		descriptionValidator.validateDescription(description, false);
	}
	
	@Test
	public void testDisplayNameTooShort() {
		Description description = generateValiddescription();
		description.setDisplayName("a");
		assertInvalidDescription(description, "displayName", String.format(TOO_SHORT_PATTERN, 3), false);
	}
	
	@Test
	public void testDispalyNameTooLong() {
		Description description = generateValiddescription();
		description.setDisplayName("123456789012345678901");
		assertInvalidDescription(description, "displayName", String.format(TOO_LONG_PATTERN, 20), false);
	}
	
	@Test
	public void testNameInUse() {
		String name = "Description-Name";
		Description description = generateValiddescription();
		description.setName(name);
		
		when(descriptionDaoMock.isNameAvailableInStore(anyString(), eq(name))).thenReturn(false);

		assertInvalidDescription(description, "displayName", "This name is already in use in this Store.", true);
	}
	
	@Test
	public void testInvalidURL1() {
		Description description = generateValiddescription();
		description.setUrl("http://");

		assertInvalidDescription(description, "url", INVALID_URL, false);
	}

	@Test
	public void testInvalidURL2() {
		Description description = generateValiddescription();
		description.setUrl("repo.lab.fi-ware.org/offerings/offering1.rdf");

		assertInvalidDescription(description, "url", INVALID_URL, false);
	}

	@Test
	public void testInvalidURL3() {
		Description description = generateValiddescription();
		description.setUrl("https://repo.lab.fi-ware.org:222222/offerings/offering1.rdf");

		assertInvalidDescription(description, "url", INVALID_URL, false);
	}

	@Test
	public void testInvalidURL4() {
		Description description = generateValiddescription();
		description.setUrl("offering");

		assertInvalidDescription(description, "url", INVALID_URL, false);
	}
	
	@Test
	public void testURLInUse() {
		String url = "http://fiware.org/valid_usdl.rdf";
		Description description = generateValiddescription();
		description.setUrl(url);
		
		when(descriptionDaoMock.isURLAvailableInStore(anyString(), eq(url))).thenReturn(false);

		assertInvalidDescription(description, "url", "This URL is already in use in this Store.", true);
	}
	
	@Test
	public void testEmptyDescription() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription("");

		// Empty descriptions are allowed
		descriptionValidator.validateDescription(description, false);
	}

	@Test
	public void testDescriptionTooLong() throws ValidationException {
		Description description = generateValiddescription();

		//240 characters (80 * 3)
		description.setDescription(
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890");	

		// Too longs descriptions are not allowed
		assertInvalidDescription(description, "description", String.format(TOO_LONG_PATTERN, 200), false);
	}
}
