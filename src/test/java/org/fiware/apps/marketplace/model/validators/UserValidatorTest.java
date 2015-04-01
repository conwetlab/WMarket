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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserValidatorTest {
	
	@Mock private UserDao userDaoMock;
	@InjectMocks private UserValidator userValidator = new UserValidator();
	
	private static final String MISSING_FIELDS_MSG = "This field is required.";
	private static final String TOO_SHORT_PATTERN = "This field must be at least %d chars.";
	private static final String TOO_LONG_PATTERN = "This field must not exceed %d chars.";
	private static final String INVALID_EMAIL = "This field must be a valid email.";
	
	private static User generateValidUser() {
		User user = new User();
		user.setDisplayName("userName");
		user.setPassword("12345678a!");
		user.setEmail("example@example.com");
		user.setCompany("EXAMPLE");
		user.setDisplayName("Example Name");
		
		return user;
	}
	
	private void assertInvalidNewUser(User user, String field, String expectedMsg) {
		try {
			userValidator.validateNewUser(user);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
			assertThat(ex.getFieldName()).isEqualTo(field);
		}
	}
	
	private void assertInvalidUpdatedUser(User oldUser, User updatedUser, String field, String expectedMsg) {
		try {
			userValidator.validateUpdatedUser(oldUser, updatedUser);
			failBecauseExceptionWasNotThrown(ValidationException.class);
		} catch (ValidationException ex) {
			assertThat(ex).hasMessage(expectedMsg);
			assertThat(ex.getFieldName()).isEqualTo(field);
		}
	}
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(userDaoMock.isEmailAvailable(anyString())).thenReturn(true);
		
	}
	
	@Test
	public void testValidBasicUser() throws ValidationException {
		User user = new User();
		user.setDisplayName("userName");
		user.setPassword("12345678!a");
		user.setEmail("example@example.com");
		
		userValidator.validateNewUser(user);
	}
	
	@Test
	public void testValidComplexUser() throws ValidationException {
		User user = generateValidUser();
		userValidator.validateNewUser(user);
	}
	
	@Test
	public void testMissingDisplayNameOnCreation() {
		// Generate a user without username
		User user = generateValidUser();
		user.setDisplayName(null);
		
		assertInvalidNewUser(user, "displayName", MISSING_FIELDS_MSG);
	}
	
	@Test
	public void testMissingDisplayNameOnUpdate() throws ValidationException {
		// Generate a user without user name
		User oldUser = generateValidUser();
		User updatedUser = generateValidUser();
		updatedUser.setDisplayName(null);
		
		userValidator.validateUpdatedUser(oldUser, updatedUser);
	}
	
	@Test
	public void testMissingPasswordOnCreation() {
		// Generate a user without password
		User user = generateValidUser();
		user.setPassword(null);
		
		assertInvalidNewUser(user, "password", MISSING_FIELDS_MSG);
	}
	
	@Test
	public void testMissingPasswordOnUpdate() throws ValidationException {
		// Generate a user without password
		User oldUser = generateValidUser();
		User updatedUser = generateValidUser();
		updatedUser.setPassword(null);
		
		userValidator.validateUpdatedUser(oldUser, updatedUser);
	}
	
	@Test
	public void testMissingEMailOnCreation() {
		// Generate a user without mail
		User user = generateValidUser();
		user.setEmail(null);
		
		assertInvalidNewUser(user, "email", MISSING_FIELDS_MSG);
	}
	
	@Test
	public void testMissingMailOnUpdate() throws ValidationException {
		// Generate a user without mail
		User oldUser = generateValidUser();
		User updatedUser = generateValidUser();
		updatedUser.setEmail(null);
		
		userValidator.validateUpdatedUser(oldUser, updatedUser);
	}
	
	
	@Test
	public void testMissingCompany() throws ValidationException {
		User user = generateValidUser();
		user.setCompany(null);
		
		// Company name can be set to null
		userValidator.validateNewUser(user);
	}
	
	@Test
	public void testPasswordTooShort() {
		User user = generateValidUser();
		user.setPassword("a");
		
		// Passwords are check in the same way for new or updated users
		assertInvalidNewUser(user, "password", String.format(TOO_SHORT_PATTERN, 8));
	}
	
	@Test
	public void testPasswordTooLong() {
		User user = generateValidUser();
		user.setPassword("1234567890123456789012345678901");
		
		// Passwords are check in the same way for new or updated users
		assertInvalidNewUser(user, "password", String.format(TOO_LONG_PATTERN, 30));
	}
	
	@Test
	public void testDisplayNameTooShort() {
		User user = generateValidUser();
		user.setDisplayName("a");
		
		// Display names are check in the same way for new or updated users
		assertInvalidNewUser(user, "displayName", String.format(TOO_SHORT_PATTERN, 3));
	}
	
	@Test
	public void testDisplayNameTooLong() {
		User user = generateValidUser();
		user.setDisplayName("abcdefghijklmnopqrstuvwxyzabcdefghij");
		
		// Display names are check in the same way for new or updated users
		assertInvalidNewUser(user, "displayName", String.format(TOO_LONG_PATTERN, 30));
	}
	
	@Test
	public void testInvalidMail1() {
		User user = generateValidUser();
		user.setEmail("test");
		
		assertInvalidNewUser(user, "email", INVALID_EMAIL);
	}
	
	@Test
	public void testInvalidMail2() {
		User user = generateValidUser();
		user.setEmail("test@test");
		
		assertInvalidNewUser(user, "email", INVALID_EMAIL);
	}
	
	@Test
	public void testInvalidMail3() {
		User user = generateValidUser();
		user.setEmail("@test.com");
		
		assertInvalidNewUser(user, "email", INVALID_EMAIL);
	}

	@Test
	public void testEmailInUseOnCreate() {
		String email = "email@example.com";
		User user = generateValidUser();
		user.setEmail(email);
		when(userDaoMock.isEmailAvailable(email)).thenReturn(false);
		
		assertInvalidNewUser(user, "email", "This email is already registered.");
		
	}
	
	@Test
	public void testUpdateMailToOneUsedByOtherUser() {
		String oldMail = "oldMail@example.com";
		String newEmail = "email@example.com";
		
		User oldUser = generateValidUser();
		User updatedUser = generateValidUser();
	
		oldUser.setEmail(oldMail);
		updatedUser.setEmail(newEmail);
		
		when(userDaoMock.isEmailAvailable(newEmail)).thenReturn(false);
		
		assertInvalidUpdatedUser(oldUser, updatedUser, "email", "This email is already registered.");
		
	}
	
	@Test
	public void testUpdateMailToOneUsedByTheSameUser() throws ValidationException {
		String mail = "email@example.com";
		
		User oldUser = generateValidUser();
		User updatedUser = generateValidUser();
	
		oldUser.setEmail(mail);
		updatedUser.setEmail(mail);
		
		when(userDaoMock.isEmailAvailable(mail)).thenReturn(false);
		
		userValidator.validateUpdatedUser(oldUser, updatedUser);
	}
	
	@Test
	public void testCompanyTooShort() {
		User user = generateValidUser();
		user.setCompany("a");
		
		// Organizations are checked in the same way for new and updated users
		assertInvalidNewUser(user, "company", String.format(TOO_SHORT_PATTERN, 3));
	}
	
	@Test
	public void testCompanyTooLong() {
		User user = generateValidUser();
		user.setCompany("1234567890123456789012345678901");
		
		// Organizations are checked in the same way for new and updated users
		assertInvalidNewUser(user, "company", String.format(TOO_LONG_PATTERN, 30));
	}
}
