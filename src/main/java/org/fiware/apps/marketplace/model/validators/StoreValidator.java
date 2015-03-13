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
import org.fiware.apps.marketplace.model.Store;
import org.springframework.stereotype.Service;

@Service("storeValidator")
public class StoreValidator {
	
	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();
	
	/**
	 * @param store Store to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, url) are included.
	 * @return True if the store is valid. Otherwise <code>ValidationException</code> will be thrown
	 * @throws ValidationException If store is not valid
	 */
	public boolean validateStore(Store store, boolean isBeingCreated) throws ValidationException {

		if (isBeingCreated) {
			GENERIC_VALIDATOR.validatorRequired("displayName", store.getDisplayName());
		}

		GENERIC_VALIDATOR.validatorPattern("displayName", store.getDisplayName(), "^[\\w -]+$", "This field only accepts letters, digits and (-/_).");
		GENERIC_VALIDATOR.validatorMinLength("displayName", store.getDisplayName(), GenericValidator.getDisplayNameMinLength());
		GENERIC_VALIDATOR.validatorMaxLength("displayName", store.getDisplayName(), GenericValidator.getDisplayNameMaxLength());

		if (isBeingCreated) {
			GENERIC_VALIDATOR.validatorRequired("url", store.getUrl());
		}

		GENERIC_VALIDATOR.validatorURLPattern("url", store.getUrl());

		if (store.getDescription() != null && !GENERIC_VALIDATOR.validateDescription(store.getDescription())) {
			int minDescriptionLength = GenericValidator.getDescriptionMinLength();
			int maxDescriptionLength = GenericValidator.getDescriptionMaxLength();
			throw new ValidationException(GENERIC_VALIDATOR.getLengthErrorMessage("description", 
					minDescriptionLength, maxDescriptionLength));
		}
		
		return true;
	}

}
