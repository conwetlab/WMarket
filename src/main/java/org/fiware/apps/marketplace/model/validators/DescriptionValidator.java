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
import org.fiware.apps.marketplace.model.Description;
import org.springframework.stereotype.Service;

@Service("offeringsDescriptionValidator")
public class DescriptionValidator {

	private static final GenericValidator GENERIC_VALIDATOR = GenericValidator.getInstance();

	/**
	 * @param description Offerings Description to be checked
	 * @param isBeingCreated true if the user is being created. In this case, the system will check if
	 * the basic fields (name, url) are included.
	 * @throws ValidationException If description is not valid
	 */
	public void validateDescription(Description description, boolean isBeingCreated) throws ValidationException {

		// Check basic fields when a description is created
		if (isBeingCreated) {
			GENERIC_VALIDATOR.validateRequired("displayName", description.getDisplayName());
			GENERIC_VALIDATOR.validateRequired("url", description.getUrl());
		}

		if (description.getDisplayName() != null) {
			GENERIC_VALIDATOR.validatePattern("displayName", description.getDisplayName(), 
					"^[\\w -]+$", "This field only accepts letters, digits and (-/_).");
			GENERIC_VALIDATOR.validateMinLength("displayName", description.getDisplayName(), 
					GenericValidator.getDisplayNameMinLength());
			GENERIC_VALIDATOR.validateMaxLength("displayName", description.getDisplayName(), 
					GenericValidator.getDisplayNameMaxLength());
		}

		if (description.getUrl() != null) {
			GENERIC_VALIDATOR.validateURL("url", description.getUrl());
		}

		if (description.getDescription() != null) {
			GENERIC_VALIDATOR.validateMinLength("description", description.getDescription(), 
					GenericValidator.getDescriptionMinLength());
			GENERIC_VALIDATOR.validateMaxLength("description", description.getDescription(), 
					GenericValidator.getDescriptionMaxLength());

		}	
	}

}
