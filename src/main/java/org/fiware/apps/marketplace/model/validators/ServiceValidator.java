package org.fiware.apps.marketplace.model.validators;

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.springframework.stereotype.Service;

@Service("serviceValidator")
public class ServiceValidator {

	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();

	/**
	 * @param service Service to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, url) are included.
	 * @return True if the service is valid. Otherwise <code>ValidationException</code> will be thrown
	 * @throws ValidationException If service is not valid
	 */
	public boolean validateService(org.fiware.apps.marketplace.model.Service service, 
			boolean isBeingCreated) throws ValidationException {

		if (isBeingCreated) {
			if (service.getName() == null || service.getUrl() == null) {
				throw new ValidationException("name and/or url cannot be null");
			}
		}

		if (service.getName() != null && !GENERIC_VALIDATOR.validateName(service.getName())) {
			int minNameLength = GenericValidator.getNameMinLength();
			int maxNameLength = GenericValidator.getNameMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("name", 
					minNameLength, maxNameLength));
		}

		if (service.getUrl() != null && !GENERIC_VALIDATOR.validateURL(service.getUrl())) {
			throw new ValidationException("url is not valid");
		}

		if (service.getDescription() != null && !GENERIC_VALIDATOR.validateDescription(service.getDescription())) {
			int minDescriptionLength = GenericValidator.getDescriptionMinLength();
			int maxDescriptionLength = GenericValidator.getDescriptionMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("description", 
					minDescriptionLength, maxDescriptionLength));
		}

		return true;
	}

}
