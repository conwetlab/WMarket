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

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userValidator")
public class UserValidator {

    @Autowired private UserDao userDao;
    @Autowired private PasswordEncoder encoder;

	private static final int DISPLAY_NAME_MIN_LENGTH = 3;
	private static final int DISPLAY_NAME_MAX_LENGTH = 30;
	private static final int COMPANY_MIN_LENGTH = 3;	//For example: UPM, TID, SAP, ENG,...
	private static final int COMPANY_MAX_LENGTH = 30;
	private static final int PASSWORD_MIN_LENGTH = 8;
	private static final int PASSWORD_MAX_LENGTH = 30;

	private static final BasicValidator BASIC_VALIDATOR = BasicValidator.getInstance();

	/**
	 * @param user User to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, mail, pass) are included.
	 * @throws ValidationException If user is not valid
	 */
	public void validateUser(User user, boolean isBeingCreated) throws ValidationException {
		
		// Check basic fields when a user is created
		if (isBeingCreated) {
			BASIC_VALIDATOR.validateRequired("displayName", user.getDisplayName());
			BASIC_VALIDATOR.validateRequired("email", user.getEmail());
			BASIC_VALIDATOR.validateRequired("password", user.getPassword());
		}
		
		if (user.getDisplayName() != null) {
			BASIC_VALIDATOR.validatePattern("displayName", user.getDisplayName(), 
					"^[\\w ]+$", "This field only accepts letters and digits.");
			BASIC_VALIDATOR.validateMinLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MAX_LENGTH);
		}
		
		if (user.getPassword() != null) {
			BASIC_VALIDATOR.validateMinLength("password", user.getPassword(), 
					PASSWORD_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("password", user.getPassword(), 
					PASSWORD_MAX_LENGTH);
		}
		
		if (user.getEmail() != null) {
			BASIC_VALIDATOR.validateEMail("email", user.getEmail());
		}

		if (user.getCompany() != null) {
			BASIC_VALIDATOR.validateMinLength("company", user.getCompany(), 
					COMPANY_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("company", user.getCompany(), 
					COMPANY_MAX_LENGTH);
		}
	}

    public void validateRegistrationForm(User user, String passwordConfirm) throws ValidationException {
        validateDisplayName(user.getDisplayName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword(), passwordConfirm);
    }

    public void validateAccountForm(User user, User currentUser) throws ValidationException {
        validateDisplayName(user.getDisplayName());
        validateEmail(user.getEmail(), currentUser.getEmail());
        validateCompany(user.getCompany());
    }

    public void validateChangePasswordForm(User currentUser, String oldPassword, String password, String passwordConfirm) throws ValidationException {
        try {
            validatePassword(oldPassword);
        } catch (ValidationException e) {
            throw new ValidationException("oldPassword", e.getFieldError());
        }

        if (!encoder.matches(oldPassword, currentUser.getPassword())) {
            throw new ValidationException("oldPassword", "The password given is not valid.");
        }

        validatePassword(password, passwordConfirm);
    }

    private void validateDisplayName(String displayName) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("displayName", displayName);
        BASIC_VALIDATOR.validatePattern("displayName", displayName, "^[a-zA-Z ]+$", "This field only accepts letters and white spaces.");
        BASIC_VALIDATOR.validateMinLength("displayName", displayName, DISPLAY_NAME_MIN_LENGTH);
        BASIC_VALIDATOR.validateMaxLength("displayName", displayName, DISPLAY_NAME_MAX_LENGTH);
    }

    private void validateEmail(String email) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("email", email);
        BASIC_VALIDATOR.validateEMail("email", email);

        if (userDao.containsWithEmail(email)) {
            throw new ValidationException("email", "The email is already registered.");
        }
    }

    private void validateEmail(String email, String exceptEmail) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("email", email);
        BASIC_VALIDATOR.validateEMail("email", email);

        if (userDao.containsWithEmail(email) && !exceptEmail.equals(email)) {
            throw new ValidationException("email", "The email is already registered.");
        }
    }

    private void validatePassword(String password) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("password", password);
        BASIC_VALIDATOR.validateMinLength("password", password, PASSWORD_MIN_LENGTH);
        BASIC_VALIDATOR.validateMaxLength("password", password, PASSWORD_MAX_LENGTH);
    }

    private void validatePassword(String password, String passwordConfirm) throws ValidationException {
        validatePassword(password);

        BASIC_VALIDATOR.validateRequired("passwordConfirm", passwordConfirm);

        if (!password.equals(passwordConfirm)) {
            throw new ValidationException("passwordConfirm", "Passwords do not match.");
        }
    }

    private void validateCompany(String company) throws ValidationException {
        if (company != null) {
            BASIC_VALIDATOR.validatePattern("company", company, "^[a-zA-Z -]+$", "This field only accepts letters, white spaces and hyphens.");
            BASIC_VALIDATOR.validateMinLength("company", company, COMPANY_MIN_LENGTH);
            BASIC_VALIDATOR.validateMaxLength("company", company, COMPANY_MAX_LENGTH);
        }
    }

}
