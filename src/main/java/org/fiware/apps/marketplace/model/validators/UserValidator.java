package org.fiware.apps.marketplace.model.validators;

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
			if (user.getUserName() == null || user.getEmail() == null || user.getPassword() == null) {
				throw new ValidationException("name, email and/or password cannot be null");
			}
		}
		
		if (user.getUserName() != null && !GENERIC_VALIDATOR.validateName(user.getUserName())) {
			int minUserNameLength = GenericValidator.getNameMinLength();
			int maxUserNameLength = GenericValidator.getNameMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("userName", 
					minUserNameLength, maxUserNameLength));
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
