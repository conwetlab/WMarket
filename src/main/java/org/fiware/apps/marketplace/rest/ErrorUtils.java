package org.fiware.apps.marketplace.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.APIError;
import org.springframework.dao.DataAccessException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class ErrorUtils {
	
	private String contraintViolationMessage;
	
	public ErrorUtils(String contraintViolationMessage) {
		this.contraintViolationMessage = contraintViolationMessage;
	}
	
	public Response badRequestResponse(DataAccessException ex) {
		String message;
		if (ex.getRootCause() instanceof MySQLIntegrityConstraintViolationException)  {
			message = this.contraintViolationMessage;
		} else {
			message = ex.getRootCause().getMessage();
		}
		
		return Response.status(Status.BAD_REQUEST).entity(
				new APIError(ErrorType.BAD_REQUEST, message)).build();
	}
	
	public Response badRequestResponse(String message) {
		return Response.status(Status.BAD_REQUEST).entity(
				new APIError(ErrorType.BAD_REQUEST, message)).build();
	}
	
	public Response entityNotFoundResponse(Exception ex) {
		return Response.status(Status.NOT_FOUND).entity(
				new APIError(ErrorType.NOT_FOUND, ex.getMessage())).build();
	}
	
	public Response unauthorizedResponse(String action) {
		return Response.status(Status.UNAUTHORIZED).entity(
				new APIError(ErrorType.UNAUTHORIZED, "You are not authorized to " + action)).build();
	}
	
	public Response internalServerError(String cause) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				new APIError(ErrorType.INTERNAL_SERVER_ERROR, cause)).build();
	}
	
	public Response serviceUnavailableResponse(String cause) {
		return Response.status(Status.SERVICE_UNAVAILABLE).entity(
				new APIError(ErrorType.SERVICE_UNAVAILABLE, cause)).build();
	}

}
