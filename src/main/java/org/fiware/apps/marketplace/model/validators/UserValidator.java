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

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.stereotype.Service;

@Service("userValidator")
public class UserValidator {

	private static final int DISPLAY_NAME_MIN_LENGTH = 3;
	private static final int DISPLAY_NAME_MAX_LENGTH = 30;
	private static final int COMPANY_MIN_LENGTH = 3;	//For example: UPM, TID, SAP, ENG,...
	private static final int COMPANY_MAX_LENGTH = 30;
	private static final int PASSWORD_MIN_LENGTH = 8;
	private static final int PASSWORD_MAX_LENGTH = 30;

	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();

	/**
	 * @param user User to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, mail, pass) are included.
	 * @throws ValidationException If user is not valid
	 */
	public void validateUser(User user, boolean isBeingCreated) throws ValidationException {
		
		// Check basic fields when a user is created
		if (isBeingCreated) {
			GENERIC_VALIDATOR.validateRequired("displayName", user.getDisplayName());
			GENERIC_VALIDATOR.validateRequired("email", user.getEmail());
			GENERIC_VALIDATOR.validateRequired("password", user.getPassword());
		}
		
		if (user.getDisplayName() != null) {
			GENERIC_VALIDATOR.validatePattern("displayName", user.getDisplayName(), 
					"^[\\w ]+$", "This field only accepts letters and digits.");
			GENERIC_VALIDATOR.validateMinLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MIN_LENGTH);
			GENERIC_VALIDATOR.validateMaxLength("displayName", user.getDisplayName(), 
					DISPLAY_NAME_MAX_LENGTH);
		}
		
		if (user.getPassword() != null) {
			GENERIC_VALIDATOR.validateMinLength("password", user.getPassword(), 
					PASSWORD_MIN_LENGTH);
			GENERIC_VALIDATOR.validateMaxLength("password", user.getPassword(), 
					PASSWORD_MAX_LENGTH);
		}
		
		if (user.getEmail() != null) {
			GENERIC_VALIDATOR.validateEMail("email", user.getEmail());
		}

		if (user.getCompany() != null) {
			GENERIC_VALIDATOR.validateMinLength("company", user.getCompany(), 
					COMPANY_MIN_LENGTH);
			GENERIC_VALIDATOR.validateMaxLength("company", user.getCompany(), 
					COMPANY_MAX_LENGTH);
		}
	}
}
