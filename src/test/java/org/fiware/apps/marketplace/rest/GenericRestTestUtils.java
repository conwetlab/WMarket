package org.fiware.apps.marketplace.rest;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.model.APIError;
import org.fiware.apps.marketplace.model.ErrorType;

public class GenericRestTestUtils {
	
	public static void checkAPIError(Response res, int status, ErrorType type, String message) {
		assertThat(res.getStatus()).isEqualTo(status);
		APIError error = (APIError) res.getEntity();
		assertThat(error.getErrorType()).isEqualTo(type);
		assertThat(error.getErrorMessage()).isEqualTo(message);
	}


}
