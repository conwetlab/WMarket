package org.fiware.apps.marketplace.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ServiceError {
	
	private ErrorType errorType;
	private String errorMessage;
	
	public ServiceError() {
		
	}
	
	public ServiceError(ErrorType errorType, String errorMessage) {
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}

	@XmlElement(name = "type")
	public ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	@XmlElement(name = "message")
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
	

}
