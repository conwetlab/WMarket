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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;	

import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.ErrorType;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class DescriptionServiceIT extends AbstractIT {
	
	private final static String USER_NAME = "marketplace";
	private final static String PASSWORD = "password1!a";
	private final static String EMAIL = "example@example.com";
	private final static String STORE_NAME = "wstore";
	private final static String STORE_URL = "http://store.lab.fiware.org";
	
	private final static String MESSAGE_NAME_IN_USE = "This name is already in use in this Store.";
	private final static String MESSAGE_URL_IN_USE = "This URL is already in use in this Store.";
	
	private String serverUrl;
	
	@Rule
	public WireMockRule wireMock = new WireMockRule(0);

	@Override
	public void specificSetUp() {
		createUser(USER_NAME, EMAIL, PASSWORD);
		createStore(USER_NAME, PASSWORD, STORE_NAME, STORE_URL);
		
		// Configure server
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBodyFile("final_usdl.rdf")));
		
		// Start up server
		wireMock.start();
		
		// Set server URL
		serverUrl = "http://127.0.0.1:" + wireMock.port();
	}
	
	@After
	public void stopMockServer() {
		wireMock.stop();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// AUXILIAR //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private Response getDescription(String userName, String password, String storeName, String descriptionName) {
		Client client = ClientBuilder.newClient();
		return client.target(endPoint + "/api/v2/store/" + storeName + "/description/" + descriptionName)
				.request(MediaType.APPLICATION_XML)
				.header("Authorization", getAuthorization(userName, password)).get();
	}
	
	private void checkDescription(String userName, String password, String storeName, 
			String descriptionName, String displayName, String url, String comment) {
		
		Description desciption = getDescription(userName, password, storeName, descriptionName)
				.readEntity(Description.class);
		
		assertThat(desciption.getName()).isEqualTo(descriptionName);
		assertThat(desciption.getDisplayName()).isEqualTo(displayName);
		assertThat(desciption.getUrl()).isEqualTo(url);
		assertThat(desciption.getComment()).isEqualTo(comment);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private Response createDescription(String userName, String password, String storeName, String displayName,
			String url, String comment) {
		
		Description description = new Description();
		description.setDisplayName(displayName);
		description.setUrl(url);
		description.setComment(comment);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + storeName + "/description")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getAuthorization(userName, password))
				.post(Entity.entity(description, MediaType.APPLICATION_JSON));
		
		return response;
	}
	
	@Test
	public void testCreation() {
		
		String displayName = "Offering 1";
		String descriptionName = "offering-1";
		String descriptionComment = "Example Comment";
		
		Response response = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, 
				serverUrl, descriptionComment);	
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeaderString("Location")).isEqualTo(endPoint + "/api/v2/store/" + STORE_NAME +
				"/description/" + descriptionName);
		
		// Check that the description actually exists
		checkDescription(USER_NAME, PASSWORD, STORE_NAME, descriptionName, displayName, 
				serverUrl, descriptionComment);
	}
	
	private void testCreationInvalidField(String displayName, String url, String comment, String invalidField,
			String message) {

		Response response = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, url, comment);
		checkAPIError(response, 400, invalidField, message, ErrorType.VALIDATION_ERROR);
	}
	
	@Test
	public void testCreationDisplayNameInvalid() {
		testCreationInvalidField("OFFERING!", serverUrl, "", "displayName", 
				MESSAGE_INVALID_DISPLAY_NAME);
	}
	
	@Test
	public void testCreationDisplayNameTooShort() {
		testCreationInvalidField("a", serverUrl, "", "displayName", 
				String.format(MESSAGE_TOO_SHORT, 3));
	}
	
	@Test
	public void testCreationDisplayNameTooLong() {
		testCreationInvalidField("abcdefghijklmnopqrstuvwxyz", serverUrl, "", "displayName", 
				String.format(MESSAGE_TOO_LONG, 20));
	}
	
	@Test
	public void testCreationURLInvalid() {
		testCreationInvalidField("Offering", "https:/127.0.0.1:" + wireMock.port(), "", "url", 
				MESSAGE_INVALID_URL);
	}
	
	@Test
	public void testCreationCommentTooLong() {
		testCreationInvalidField("Offering", serverUrl, "12345678901234567890123456789012345678901234567890"
				+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456"
				+ "7890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", 
				"comment", String.format(MESSAGE_TOO_LONG, 200));
	}
	
	private void testCreationFieldAlreayExists(String displayName1, String displayName2, String url1, String url2,
			String field, String expectedMessage) {

		createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName1, url1, "");
		Response response = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName2, url2, "");
		
		checkAPIError(response, 400, field, expectedMessage, ErrorType.VALIDATION_ERROR);

	}
	
	@Test
	public void testCreationDisplayNameAlreadyExists() {		
		String displayName = "Offering 1";
		
		// name is based on display name and name is checked before display name...
		testCreationFieldAlreayExists(displayName, displayName, serverUrl, serverUrl + "a", "name", 
				MESSAGE_NAME_IN_USE);
	}
	
	@Test
	public void testCreationURLAlreadyExists() {
		testCreationFieldAlreayExists("offering-1", "offering-2", serverUrl, serverUrl, "url", MESSAGE_URL_IN_USE);
	}
	
	private void testCreationFieldAlreayExistsInAnotherStore(String displayName1, String displayName2, 
			String url1, String url2) {

		// Create another Store
		String newStoreName = STORE_NAME + "a";
		createStore(USER_NAME, PASSWORD, newStoreName, STORE_URL + ":8000");
		
		Response createResponse1 = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName1, url1, "");
		Response createResponse2 = createDescription(USER_NAME, PASSWORD, newStoreName, displayName2, url2, "");
		
		// Both offerings can be created
		assertThat(createResponse1.getStatus()).isEqualTo(201);
		assertThat(createResponse2.getStatus()).isEqualTo(201);
		
	}
	
	@Test
	public void testCreationDisplayNameAlreadyExistsInAnotherStore() {		
		String displayName = "Offering 1";
		
		// name is based on display name and name is checked before display name...
		testCreationFieldAlreayExistsInAnotherStore(displayName, displayName, serverUrl, serverUrl + "/a");
	}
	
	@Test
	public void testCreationURLAlreadyExistsInAnotherStore() {
		testCreationFieldAlreayExistsInAnotherStore("offering-1", "offering-2", serverUrl, serverUrl);
	}
	
	@Test
	public void testDeleteUserWithDescription() {
		String name = "offering-1";
		
		Response createStoreResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, name, serverUrl, "");
		assertThat(createStoreResponse.getStatus()).isEqualTo(201);
		checkDescription(USER_NAME, PASSWORD, STORE_NAME, name, name, serverUrl, "");
		
		// Delete user
		Response deleteUserResponse = deleteUser(USER_NAME, PASSWORD, USER_NAME);
		assertThat(deleteUserResponse.getStatus()).isEqualTo(204);
		
		// Create another user to be able to check the store
		String newUserName = USER_NAME + "a";
		String email = "new_email__@example.com";
		Response createUserResponse = createUser(newUserName, email, PASSWORD);
		assertThat(createUserResponse.getStatus()).isEqualTo(201);
		
		// Check that the Store does not exist anymore
		Response getStoreResponse = getDescription(newUserName, PASSWORD, STORE_NAME, name);
		checkAPIError(getStoreResponse, 404, null, String.format(MESSAGE_STORE_NOT_FOUND, STORE_NAME), 
				ErrorType.NOT_FOUND);
		
	}
	
}
