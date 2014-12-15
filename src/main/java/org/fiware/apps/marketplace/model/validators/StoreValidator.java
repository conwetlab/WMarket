package org.fiware.apps.marketplace.model.validators;

import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Store;
import org.springframework.stereotype.Service;

@Service("storeValidator")
public class StoreValidator {
	
	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();
	
	/**
	 * @param store Store to be checked
	 * @return True if the store is valid. Otherwise <code>ValidationException</code> will be thrown
	 * @throws ValidationException If store is not valid
	 */
	public boolean validateStore(Store store) throws ValidationException {
		
		if (store.getName() == null || store.getUrl() == null) {
			throw new ValidationException("name and/or url cannot be null");
		}
		
		if (store.getName() != null && !GENERIC_VALIDATOR.validateName(store.getName())) {
			int minNameLength = GenericValidator.getNameMinLength();
			int maxNameLength = GenericValidator.getNameMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("name", 
					minNameLength, maxNameLength));
		}
		
		if (store.getUrl() != null && !GENERIC_VALIDATOR.validateURL(store.getUrl())) {
			throw new ValidationException("url is not valid");
		}
		
		if (store.getDescription() != null && !GENERIC_VALIDATOR.validateDescription(store.getDescription())) {
			int minDescriptionLength = GenericValidator.getDescriptionMinLength();
			int maxDescriptionLength = GenericValidator.getDescriptionMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("description", 
					minDescriptionLength, maxDescriptionLength));
		}
		
		return true;
	}

}
