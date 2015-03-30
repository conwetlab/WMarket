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
import org.springframework.stereotype.Service;

@Service("userValidator")
public class UserValidator {

	@Autowired private UserDao userDao;

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
					"^[a-zA-Z ]+$", "This field only accepts letters and white spaces.");
			BASIC_VALIDATOR.validateMinLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MAX_LENGTH);
		}
		
		// Email cannot be repeated. DAO is asked
		if (user.getEmail() != null) {
			BASIC_VALIDATOR.validateEMail("email", user.getEmail());

			if(!userDao.isEmailAvailable(user.getEmail())) {
				throw new ValidationException("email", "This email is already registered.");
			}
		}

		// Check password. Spaces are not allowed
		if (user.getPassword() != null) {
			BASIC_VALIDATOR.validateMinLength("password", user.getPassword(), 
					PASSWORD_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("password", user.getPassword(), 
					PASSWORD_MAX_LENGTH);
			// Check that password contains one letter, one number and one special character
			BASIC_VALIDATOR.validatePattern("password", user.getPassword(), "^.*(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!#$%&?]).*$", 
					"Password must contain one number, one letter and one unique character such as !#$%&?");
		}

		if (user.getCompany() != null) {
			BASIC_VALIDATOR.validatePattern("company", user.getCompany(), "^[a-zA-Z0-9 -]+$", 
					"This field only accepts letters, numbers, white spaces and hyphens.");
			BASIC_VALIDATOR.validateMinLength("company", user.getCompany(), 
					COMPANY_MIN_LENGTH);
			BASIC_VALIDATOR.validateMaxLength("company", user.getCompany(), 
					COMPANY_MAX_LENGTH);
		}
	}
}