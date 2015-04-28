package org.fiware.apps.marketplace.it;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.fiware.apps.marketplace.model.APIError;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractIT {
	
	private static Environment environment = Environment.getInstance();
	protected static String endPoint;
	
	protected final static String MESSAGE_INVALID_DISPLAY_NAME = 
			"This field only accepts letters, numbers, white spaces and hyphens.";
	protected final static String MESSAGE_TOO_LONG = "This field must not exceed %d chars."; 
	protected final static String MESSAGE_TOO_SHORT = "This field must be at least %d chars.";
	protected final static String MESSAGE_FIELD_REQUIRED = "This field is required.";
	protected final static String MESSAGE_NOT_AUTHORIZED = "You are not authorized to %s";
	protected final static String MESSAGE_INVALID_URL = "This field must be a valid URL.";
	protected final static String MESSAGE_INVALID_OFFSET_MAX = "offset and/or max are not valid";
	protected final static String MESSAGE_STORE_NOT_FOUND = "Store %s not found";

	@BeforeClass
	public static void startUp() throws Exception {
		
		int port = environment.start();
		
		// End Point depends on the Tomcat port
		endPoint = String.format("http://localhost:%d/FiwareMarketplace", port);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		environment.stop();
	}
	
	@Before
	public void setUp() throws Exception {
		environment.cleanDB();
		specificSetUp();
	}

	public abstract void specificSetUp();
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// AUXILIAR //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	protected Response getUser(String userName) {
		Client client = ClientBuilder.newClient();
		return client.target(endPoint + "/api/v2/user/" + userName).request(MediaType.APPLICATION_JSON)
				.get();

	}
	
	protected void checkUser(String userName, String displayName, String company) {
		User user = getUser(userName).readEntity(User.class);
		
		assertThat(user.getUserName()).isEqualTo(userName);
		assertThat(user.getDisplayName()).isEqualTo(displayName);
		assertThat(user.getCompany()).isEqualTo(company);

	}
	
	protected void checkAPIError(Response response, int status, String field, String message, ErrorType errorType) {
		
		assertThat(response.getStatus()).isEqualTo(status);
				
		APIError error = response.readEntity(APIError.class);
		assertThat(error.getField()).isEqualTo(field);
		assertThat(error.getErrorMessage()).isEqualTo(message);
		assertThat(error.getErrorType()).isEqualTo(errorType);

	}
	
	protected String getAuthorization(String userName, String password) {
		String authorization = userName + ":" + password;
		String encodedAuthorization = "Basic " + new String(Base64.encodeBase64(authorization.getBytes()));

		return encodedAuthorization;
	}
	
	protected Response createUser(String displayName, String email, String password, String company) {
		
		SerializableUser user = new SerializableUser();
		user.setDisplayName(displayName);
		user.setEmail(email);
		user.setPassword(password);
		user.setCompany(company);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/user").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));
		
		return response;

	}
	
	protected Response createUser(String displayName, String email, String password) {
		return createUser(displayName, email, password, null);
	}
	
	protected Response deleteUser(String authUserName, String authPassword, String userName) {
		
		String encodedAuthorization = getAuthorization(authUserName, authPassword);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/user/" + userName)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", encodedAuthorization)
				.delete();
		
		return response;

	}
	
	private Response createOrUpdateStore(String userName, String password, String name, String displayName, String url, 
			String comment, String imageBase64) {
		
		Store store = new Store();
		store.setDisplayName(displayName);
		store.setUrl(url);
		store.setComment(comment);
		store.setImageBase64(imageBase64);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + name).request(MediaType.APPLICATION_JSON)
				.header("Authorization", getAuthorization(userName, password))
				.post(Entity.entity(store, MediaType.APPLICATION_JSON));
		
		return response;

	}
	
	protected Response createStore(String userName, String password, String displayName, String url, String comment, 
			String imageBase64) {
		
		return createOrUpdateStore(userName, password, "", displayName, url, comment, imageBase64);
	}
	
	protected Response createStore(String userName, String password, String displayName, String url) {
		return createStore(userName, password, displayName, url, null, null);
	}
	
	protected Response updateStore(String userName, String password, String name, String displayName, String url, 
			String comment, String imageBase64) {
		
		return createOrUpdateStore(userName, password, name, displayName, url, comment, imageBase64);
	}

}
