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

import java.util.Date;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Test;

public class DescriptionValidatorTest {
	
	private DescriptionValidator descriptionValidator = new DescriptionValidator();
	
	private static final String MISSING_FIELDS = "name and/or url cannot be null";
	private static final String INVALID_LENGTH_PATTERN = "%s is not valid. (min length: %d, max length: %d)";
	private static final String INVALID_URL = "url is not valid";
	
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
	
	
	private void assertInvalidDescription(Description description, 
			String expectedMsg, boolean creating) {
		try {
			descriptionValidator.validateDescription(description, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
		}
	}
	
	@Test
	public void testValidBasicDescription() throws ValidationException {
		Description description = new Description();
		description.setDisplayName("description1");
		description.setUrl("https://repo.lab.fi-ware.org/offerings/offering1.rdf");
		
		assertThat(descriptionValidator.validateDescription(description, true)).isTrue();
	}
	
	@Test
	public void testValidComplexDescription() throws ValidationException {
		Description description = generateValiddescription();
		assertThat(descriptionValidator.validateDescription(description, true)).isTrue();
	}
	
	@Test
	public void testMissingNameOnCreation() {
		Description description = generateValiddescription();
		description.setDisplayName(null);
		assertInvalidDescription(description, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingNameOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setName(null);
		assertThat(descriptionValidator.validateDescription(description, false)).isTrue();
	}
	
	@Test
	public void testMissingUrlOnCreation() {
		Description description = generateValiddescription();
		description.setUrl(null);
		assertInvalidDescription(description, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingUrlOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setUrl(null);
		assertThat(descriptionValidator.validateDescription(description, false)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnCreation() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription(null);

		assertThat(descriptionValidator.validateDescription(description, true)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnUpdate() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription(null);

		assertThat(descriptionValidator.validateDescription(description, false)).isTrue();
	}
	
	@Test
	public void testNameTooShort() {
		Description description = generateValiddescription();
		description.setDisplayName("a");
		assertInvalidDescription(description, 
				String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testNameTooLong() {
		Description description = generateValiddescription();
		description.setDisplayName("1234567890123456");
		assertInvalidDescription(description, 
				String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testInvalidURL1() {
		Description description = generateValiddescription();
		description.setUrl("http://");

		assertInvalidDescription(description, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL2() {
		Description description = generateValiddescription();
		description.setUrl("repo.lab.fi-ware.org/offerings/offering1.rdf");

		assertInvalidDescription(description, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL3() {
		Description description = generateValiddescription();
		description.setUrl("https://repo.lab.fi-ware.org:222222/offerings/offering1.rdf");

		assertInvalidDescription(description, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL4() {
		Description description = generateValiddescription();
		description.setUrl("offering");

		assertInvalidDescription(description, INVALID_URL, false);
	}
	
	@Test
	public void testEmptyDescription() throws ValidationException {
		Description description = generateValiddescription();
		description.setDescription("");

		// Empty descriptions are allowed
		assertThat(descriptionValidator.validateDescription(description, false)).isTrue();
	}

	@Test
	public void testDescriptionTooLong() throws ValidationException {
		Description description = generateValiddescription();

		//240 characters (80 * 3)
		description.setDescription(
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890");	

		// Empty descriptions are allowed
		assertInvalidDescription(description, String.format(INVALID_LENGTH_PATTERN, 
				"description", 0, 200), false);	
	}
}
