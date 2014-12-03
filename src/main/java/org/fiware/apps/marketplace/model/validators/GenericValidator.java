package org.fiware.apps.marketplace.model.validators;

import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;

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
	
	public static int getNameMaxLength() {
		return NAME_MAX_LENGTH;
	}

	public static int getDescriptionMaxLength() {
		return DESCRIPTION_MAX_LENGTH;
	}
	
	public static int getNameMinLength() {
		return NAME_MIN_LENGTH;
	}

	public static int getDescriptionMinLength() {
		return DESCRIPTION_MIN_LENGTH;
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

	public boolean validateName(String name) {
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
