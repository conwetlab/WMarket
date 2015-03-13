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

import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.fiware.apps.marketplace.exceptions.ValidationException;

public class GenericValidator {
	
	private Pattern emailPattern;
	private UrlValidator urlValidator;
	
	private static final int NAME_MIN_LENGTH = 5;
	private static final int NAME_MAX_LENGTH = 15;
	private static final int DESCRIPTION_MIN_LENGTH = 0;
	private static final int DESCRIPTION_MAX_LENGTH = 200;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static GenericValidator INSTANCE = new GenericValidator();
	
	private GenericValidator() {
		this.emailPattern = Pattern.compile(EMAIL_PATTERN);
		this.urlValidator = new UrlValidator();
	}
	
	public static GenericValidator getInstance() {
		return INSTANCE;
	}
	
	public static int getDisplayNameMaxLength() {
		return NAME_MAX_LENGTH;
	}

	public static int getDescriptionMaxLength() {
		return DESCRIPTION_MAX_LENGTH;
	}
	
	public static int getDisplayNameMinLength() {
		return NAME_MIN_LENGTH;
	}

	public static int getDescriptionMinLength() {
		return DESCRIPTION_MIN_LENGTH;
	}

	public void validatorRequired(String fieldName, String fieldValue) throws ValidationException {
		if (fieldValue == null || fieldValue.length() == 0) {
			throw new ValidationException(fieldName, "This field is required.");
		}
	}

	public void validatorPattern(String fieldName, String fieldValue, String regex, String errorMessage) throws ValidationException {
		if (fieldValue == null || !fieldValue.matches(regex)) {
			throw new ValidationException(fieldName, errorMessage);
		}
	}

	public void validatorURLPattern(String fieldName, String fieldValue) throws ValidationException {
		if (fieldValue == null || !urlValidator.isValid(fieldValue)) {
			throw new ValidationException(fieldName, "This field must be an URL valid.");
		}
	}

	public void validatorMinLength(String fieldName, String fieldValue, int minLength) throws ValidationException {
		if (fieldValue == null || fieldValue.length() < minLength) {
			throw new ValidationException(fieldName, String.format("This field must be at least %d chars.", minLength));
		}
	}

	public void validatorMaxLength(String fieldName, String fieldValue, int maxLength) throws ValidationException {
		if (fieldValue == null || fieldValue.length() >= maxLength) {
			throw new ValidationException(fieldName, String.format("This field must not exceed the %d chars.", maxLength));
		}
	}

	public String getLengthErrorMessage(String field, int minLength, int maxLength) {
		return field + " is not valid. (min length: " +
				minLength + ", max length: " + maxLength + ")";
	}

	public boolean validateLength(String value, int minLength, int maxLength) {		
		boolean minAccomplished = minLength <= 0 ? true : value.length() >= minLength;
		boolean maxAccomplished = value.length() <= maxLength;
		
		return minAccomplished && maxAccomplished;
	}

	public boolean validateDisplayName(String name) {
		return validateLength(name, NAME_MIN_LENGTH, NAME_MAX_LENGTH);
	}
	
	public boolean validateDescription(String description) {
		return validateLength(description, DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH);
	}
	
	public boolean validateEMail(String email) {
		return emailPattern.matcher(email).matches();
	}
	
	public boolean validateURL(String url) {
		return urlValidator.isValid(url);
	}
	
}
