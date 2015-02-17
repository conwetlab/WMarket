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

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.stereotype.Service;

@Service("userValidator")
public class UserValidator {

	private static final int COMPANY_MIN_LENGTH = 3;	//For example: UPM, TID, SAP, ENG,...
	private static final int COMPANY_MAX_LENGTH = 30;
	//Just for the future...
	private static final int PASSWORD_MIN_LENGTH = 8;
	private static final int PASSWORD_MAX_LENGTH = 30;
	private static final int DISPLAY_NAME_MIN_LENGTH = 5;
	private static final int DISPLAY_NAME_MAX_LENGTH = 30;

	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();

	/**
	 * @param user User to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, mail, pass) are included.
	 * @return True if the user is valid. Otherwise <code>ValidationException</code> will be thrown
	 * @throws ValidationException If user is not valid
	 */
	public boolean validateUser(User user, boolean isBeingCreated) throws ValidationException {
		
		if (isBeingCreated) {
			if (user.getDisplayName() == null || user.getEmail() == null || user.getPassword() == null) {
				throw new ValidationException("name, email and/or password cannot be null");
			}
		}

		if (user.getDisplayName() != null && !GENERIC_VALIDATOR.validateLength(user.getDisplayName(), 
				DISPLAY_NAME_MIN_LENGTH, DISPLAY_NAME_MAX_LENGTH)) {
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("displayName", 
					DISPLAY_NAME_MIN_LENGTH, DISPLAY_NAME_MAX_LENGTH));
		}
		
		if (user.getPassword() != null && !GENERIC_VALIDATOR.validateLength(user.getPassword(), 
				PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH)) {
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("password", 
					PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH));
		}
		
		if (user.getEmail() != null && !GENERIC_VALIDATOR.validateEMail(user.getEmail())) {
			throw new ValidationException("email is not valid");
		}

		if (user.getCompany() != null && !GENERIC_VALIDATOR.validateLength(user.getCompany(), 
				COMPANY_MIN_LENGTH, COMPANY_MAX_LENGTH)) {
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("company", 
					COMPANY_MIN_LENGTH, COMPANY_MAX_LENGTH));
		}

		return true;
	}

}
