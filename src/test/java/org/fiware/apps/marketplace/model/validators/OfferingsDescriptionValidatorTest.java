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
import org.fiware.apps.marketplace.model.OfferingsDescription;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.Test;

public class OfferingsDescriptionValidatorTest {
	
	private OfferingsDescriptionValidator offeringsDescriptionValidator = new OfferingsDescriptionValidator();
	
	private static final String MISSING_FIELDS = "name and/or url cannot be null";
	private static final String INVALID_LENGTH_PATTERN = "%s is not valid. (min length: %d, max length: %d)";
	private static final String INVALID_URL = "url is not valid";
	
	private static OfferingsDescription generateValidOfferingsDescription() {
		// Additional classes
		User creator = new User();
		creator.setId(1);
		
		OfferingsDescription offeringsDescription = new OfferingsDescription();
		offeringsDescription.setDisplayName("description1");
		offeringsDescription.setUrl("https://repo.lab.fi-ware.org/offerings/offering1.rdf");
		offeringsDescription.setDescription("This is an example description");
		offeringsDescription.setRegistrationDate(new Date());
		offeringsDescription.setStore(new Store());
		offeringsDescription.setCreator(creator);
		offeringsDescription.setLasteditor(creator);
		
		return offeringsDescription;
	}
	
	
	private void assertInvalidOfferingsDescription(OfferingsDescription offeringsDescription, 
			String expectedMsg, boolean creating) {
		try {
			offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
		}
	}
	
	@Test
	public void testValidBasicOfferingsDescription() throws ValidationException {
		OfferingsDescription offeringsDescription = new OfferingsDescription();
		offeringsDescription.setDisplayName("description1");
		offeringsDescription.setUrl("https://repo.lab.fi-ware.org/offerings/offering1.rdf");
		
		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, true)).isTrue();
	}
	
	@Test
	public void testValidComplexOfferingsDescription() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, true)).isTrue();
	}
	
	@Test
	public void testMissingNameOnCreation() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDisplayName(null);
		assertInvalidOfferingsDescription(offeringsDescription, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingNameOnUpdate() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setName(null);
		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, false)).isTrue();
	}
	
	@Test
	public void testMissingUrlOnCreation() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl(null);
		assertInvalidOfferingsDescription(offeringsDescription, MISSING_FIELDS, true);
	}
	
	@Test
	public void testMissingUrlOnUpdate() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl(null);
		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, false)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnCreation() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDescription(null);

		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, true)).isTrue();
	}
	
	@Test
	public void testMissingDescriptionOnUpdate() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDescription(null);

		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, false)).isTrue();
	}
	
	@Test
	public void testNameTooShort() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDisplayName("a");
		assertInvalidOfferingsDescription(offeringsDescription, 
				String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testNameTooLong() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDisplayName("1234567890123456");
		assertInvalidOfferingsDescription(offeringsDescription, 
				String.format(INVALID_LENGTH_PATTERN, "name", 5, 15), false);
	}
	
	@Test
	public void testInvalidURL1() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl("http://");

		assertInvalidOfferingsDescription(offeringsDescription, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL2() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl("repo.lab.fi-ware.org/offerings/offering1.rdf");

		assertInvalidOfferingsDescription(offeringsDescription, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL3() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl("https://repo.lab.fi-ware.org:222222/offerings/offering1.rdf");

		assertInvalidOfferingsDescription(offeringsDescription, INVALID_URL, false);
	}

	@Test
	public void testInvalidURL4() {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setUrl("offering");

		assertInvalidOfferingsDescription(offeringsDescription, INVALID_URL, false);
	}
	
	@Test
	public void testEmptyDescription() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();
		offeringsDescription.setDescription("");

		// Empty descriptions are allowed
		assertThat(offeringsDescriptionValidator.validateOfferingsDescription(offeringsDescription, false)).isTrue();
	}

	@Test
	public void testDescriptionTooLong() throws ValidationException {
		OfferingsDescription offeringsDescription = generateValidOfferingsDescription();

		//240 characters (80 * 3)
		offeringsDescription.setDescription(
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890" + 
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890");	

		// Empty descriptions are allowed
		assertInvalidOfferingsDescription(offeringsDescription, String.format(INVALID_LENGTH_PATTERN, 
				"description", 0, 200), false);	
	}
}
