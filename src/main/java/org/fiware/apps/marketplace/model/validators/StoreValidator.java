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
import org.springframework.stereotype.Service;

@Service("storeValidator")
public class StoreValidator {

    private static final int DISPLAY_NAME_MIN_LENGTH = 5;
    private static final int DISPLAY_NAME_MAX_LENGTH = 30;
    private static final int DESCRIPTION_MAX_LENGTH = 200;

    private static final BasicValidator BASIC_VALIDATOR = BasicValidator.getInstance();

    public void validateDisplayName(String displayName) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("displayName", displayName);
        BASIC_VALIDATOR.validatePattern("displayName", displayName, "^[a-zA-Z -]+$", "This field only accepts letters, white spaces and hyphens.");
        BASIC_VALIDATOR.validateMinLength("displayName", displayName, DISPLAY_NAME_MIN_LENGTH);
        BASIC_VALIDATOR.validateMaxLength("displayName", displayName, DISPLAY_NAME_MAX_LENGTH);
    }

    public void validateURL(String url) throws ValidationException {
        BASIC_VALIDATOR.validateRequired("url", url);
        BASIC_VALIDATOR.validateURL("url", url);
    }

    public void validateDescription(String description) throws ValidationException {
        if (description != null) {
            BASIC_VALIDATOR.validateMaxLength("description", description, DESCRIPTION_MAX_LENGTH);
        }
    }

}
