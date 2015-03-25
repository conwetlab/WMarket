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

import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("storeValidator")
public class StoreValidator {

    @Autowired private StoreDao storeDao;

    private static final int DISPLAY_NAME_MIN_LENGTH = 5;
    private static final int DISPLAY_NAME_MAX_LENGTH = 30;
    private static final int DESCRIPTION_MAX_LENGTH = 200;

    private static final BasicValidator BASIC_VALIDATOR = BasicValidator.getInstance();
	
	/**
	 * @param store Store to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, url) are included.
	 * @throws ValidationException If store is not valid
	 */
	public void validateStore(Store store, boolean isBeingCreated) throws ValidationException {

		// Check basic fields when a store is created
		if (isBeingCreated) {
			BASIC_VALIDATOR.validateRequired("displayName", store.getDisplayName());
			BASIC_VALIDATOR.validateRequired("url", store.getUrl());
		}

		// If the store is being created, this value cannot be null since we have
		// checked it before
		if (store.getDisplayName() != null) {
			BASIC_VALIDATOR.validatePattern("displayName", store.getDisplayName(), 
					"^[\\w -]+$", "This field only accepts letters, digits and (-/_).");
			BASIC_VALIDATOR.validateMinLength("displayName", store.getDisplayName(), 
					BasicValidator.getDisplayNameMinLength());
			BASIC_VALIDATOR.validateMaxLength("displayName", store.getDisplayName(), 
					BasicValidator.getDisplayNameMaxLength());
		}

		// If the store is being created, this value cannot be null since we have
		// checked it before
		if (store.getUrl() != null) {
			BASIC_VALIDATOR.validateURL("url", store.getUrl());
		}

		if (store.getDescription() != null) {
			BASIC_VALIDATOR.validateMinLength("description", store.getDescription(), 
					BasicValidator.getDescriptionMinLength());
			BASIC_VALIDATOR.validateMaxLength("description", store.getDescription(), 
					BasicValidator.getDescriptionMaxLength());
		}
	}

    public void validateRegistrationForm(Store store) throws ValidationException {
        validateDisplayName(store.getDisplayName());

        if (storeDao.containsWithName(NameGenerator.getURLName(store.getDisplayName()))) {
            throw new ValidationException("displayName", "The name is already taken.");
        }

        validateURL(store.getUrl());
        validateDescription(store.getDescription());
    }

    private void validateDisplayName(String displayName) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("displayName", displayName);
        BASIC_VALIDATOR.validatePattern("displayName", displayName, "^[a-zA-Z -]+$", "This field only accepts letters, white spaces and hyphens.");
        BASIC_VALIDATOR.validateMinLength("displayName", displayName, DISPLAY_NAME_MIN_LENGTH);
        BASIC_VALIDATOR.validateMaxLength("displayName", displayName, DISPLAY_NAME_MAX_LENGTH);
    }

    private void validateURL(String url) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("url", url);
        BASIC_VALIDATOR.validateURL("url", url);
    }

    private void validateDescription(String description) throws ValidationException {
        if (description != null) {
            BASIC_VALIDATOR.validateMaxLength("description", description, DESCRIPTION_MAX_LENGTH);
        }
    }

}
