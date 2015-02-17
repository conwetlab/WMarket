package org.fiware.apps.marketplace.model.validators;

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

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.junit.Test;

public class UserValidatorTest {
	
	private UserValidator userValidator = new UserValidator();
	
	private static final String MISSING_FILEDS_MSG = "name, email and/or password cannot be null";
	private static final String INVALID_LENGTH_PATTERN = "%s is not valid. (min length: %d, max length: %d)";
	private static final String INVALID_EMAIL = "email is not valid";
	
	private static User generateValidUser() {
		User user = new User();
		user.setDisplayName("userName");
		user.setPassword("12345678");
		user.setEmail("example@example.com");
		user.setCompany("EXAMPLE");
		user.setDisplayName("Example Name");
		
		return user;
	}
	
	private void assertInvalidUser(User user, String expectedMsg, boolean creating) {
		try {
			userValidator.validateUser(user, creating);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
		}
	}
	
	@Test
	public void testValidBasicUser() throws ValidationException {
		User user = new User();
		user.setDisplayName("userName");
		user.setPassword("12345678");
		user.setEmail("example@example.com");
		
		assertThat(userValidator.validateUser(user, true)).isTrue();
	}
	
	@Test
	public void testValidComplexUser() throws ValidationException {
		User user = generateValidUser();
		assertThat(userValidator.validateUser(user, true)).isTrue();
	}
	
	@Test
	public void testMissingUserNameOnCreation() {
		// Generate a user without username
		User user = generateValidUser();
		user.setDisplayName(null);
		
		assertInvalidUser(user, MISSING_FILEDS_MSG, true);
	}
	
	@Test
	public void testMissingUserNameOnUpdate() throws ValidationException {
		// Generate a user without username
		User user = generateValidUser();
		user.setDisplayName(null);
		
		assertThat(userValidator.validateUser(user, false)).isTrue();
	}
	
	@Test
	public void testMissingPasswordOnCreation() {
		// Generate a user without password
		User user = generateValidUser();
		user.setPassword(null);
		
		assertInvalidUser(user, MISSING_FILEDS_MSG, true);
	}
	
	@Test
	public void testMissingPasswordOnUpdate() throws ValidationException {
		// Generate a user without password
		User user = generateValidUser();
		user.setPassword(null);
		
		assertThat(userValidator.validateUser(user, false)).isTrue();
	}
	
	@Test
	public void testMissingMailOnCreation() {
		// Generate a user without mail
		User user = generateValidUser();
		user.setEmail(null);
		
		assertInvalidUser(user, MISSING_FILEDS_MSG, true);
	}
	
	@Test
	public void testMissingMailOnUpdate() throws ValidationException {
		// Generate a user without mail
		User user = generateValidUser();
		user.setEmail(null);
		
		assertThat(userValidator.validateUser(user, false)).isTrue();
	}
	
	@Test
	public void testMissingDisplayName() throws ValidationException {
		User user = generateValidUser();
		user.setDisplayName(null);
		
		// Display name can be set to null
		assertThat(userValidator.validateUser(user, false)).isTrue();
	}
	
	@Test
	public void testMissingCompany() throws ValidationException {
		User user = generateValidUser();
		user.setCompany(null);
		
		// Company name can be set to null
		assertThat(userValidator.validateUser(user, false)).isTrue();
	}
	
	@Test
	public void testPasswordTooShort() {
		User user = generateValidUser();
		user.setPassword("a");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "password", 8, 30), false);
	}
	
	@Test
	public void testPasswordTooLong() {
		User user = generateValidUser();
		user.setPassword("1234567890123456789012345678901");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "password", 8, 30), false);
	}
	
	@Test
	public void testDisplayNameTooShort() {
		User user = generateValidUser();
		user.setDisplayName("a");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "displayName", 5, 30), false);
	}
	
	@Test
	public void testDisplayNameTooLong() {
		User user = generateValidUser();
		user.setDisplayName("1234567890123456789012345678901");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "displayName", 5, 30), false);
	}
	
	@Test
	public void testInvalidMail1() {
		User user = generateValidUser();
		user.setEmail("test");
		
		assertInvalidUser(user, INVALID_EMAIL, false);
	}
	
	@Test
	public void testInvalidMail2() {
		User user = generateValidUser();
		user.setEmail("test@test");
		
		assertInvalidUser(user, INVALID_EMAIL, false);
	}
	
	@Test
	public void testInvalidMail3() {
		User user = generateValidUser();
		user.setEmail("@test.com");
		
		assertInvalidUser(user, INVALID_EMAIL, false);
	}
	
	@Test
	public void testCompanyTooShort() {
		User user = generateValidUser();
		user.setCompany("a");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "company", 3, 30), false);
	}
	
	@Test
	public void testCompanyTooLong() {
		User user = generateValidUser();
		user.setCompany("1234567890123456789012345678901");
		
		assertInvalidUser(user, String.format(INVALID_LENGTH_PATTERN, "company", 3, 30), false);
	}
}
