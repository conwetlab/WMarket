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
