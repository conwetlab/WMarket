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

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;	

import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Descriptions;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Offering;
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
	private final static String MESSAGE_INVALID_RDF = "Your RDF could not be parsed.";
	protected final static String MESSAGE_DESCRIPTION_NOT_FOUND = "Description %s not found";
	
	private final static Offering FIRST_OFFERING = new Offering();
	private final static Offering SECOND_OFFERING = new Offering();
	
	static {
		// WARN: This properties depends on the RDF files stored in "src/test/resources/__files" so if these files
		// changes, this properties must be changed. Otherwise, tests will fail.
		FIRST_OFFERING.setDisplayName("OrionStarterKit");
		FIRST_OFFERING.setImageUrl(
				"https://store.lab.fi-ware.org/media/CoNWeT__OrionStarterKit__1.2/catalogue.png");
		FIRST_OFFERING.setDescription("Offering composed of three mashable application components: "
				+ "ngsi-source, ngsientity2poi and ngsi-updater. Those components are provided as the base "
				+ "tools/examples for making application mashups using WireCloud and the Orion Context Broker. "
				+ "Those resources can be used for example for showing entities coming from an Orion server inside "
				+ "the Map Viewer widget or browsing and updating the attributes of those entities.");
		
		SECOND_OFFERING.setDisplayName("CKAN starter Kit");
		SECOND_OFFERING.setImageUrl(
				"https://store.lab.fiware.org/media/CoNWeT__CKANStarterKit__1.2/logo-ckan_170x80.png");
		SECOND_OFFERING.setDescription("Offering composed of several mashable application components that compose "
				+ "the base tools/examples for making application mashups using WireCloud and CKAN. Those resources "
				+ "can be used for example for showing data coming from CKAN's dataset inside the Map Viewer widget "
				+ "or inside a graph widget or for browsing data inside a table widget.");

		
	}
	
	private String defaultUSDLPath;
	private String serverUrl;
	private String secondaryUSDLPath;
	
	@Rule
	public WireMockRule wireMock = new WireMockRule(0);

	@Override
	public void specificSetUp() {
		createUser(USER_NAME, EMAIL, PASSWORD);
		createStore(USER_NAME, PASSWORD, STORE_NAME, STORE_URL);
		
		// Configure server
		stubFor(get(urlMatching("/default[0-9]*.rdf"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBodyFile("default.rdf")));
		
		stubFor(get(urlMatching("/secondary.rdf"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBodyFile("secondary.rdf")));
		
		// Start up server
		wireMock.start();
		
		// Set server URL
		serverUrl = "http://127.0.0.1:" + wireMock.port();
		defaultUSDLPath = serverUrl + "/default.rdf";
		secondaryUSDLPath = serverUrl + "/secondary.rdf";
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
		
		Description description = getDescription(userName, password, storeName, descriptionName)
				.readEntity(Description.class);
		
		assertThat(description.getName()).isEqualTo(descriptionName);
		assertThat(description.getDisplayName()).isEqualTo(displayName);
		assertThat(description.getUrl()).isEqualTo(url);
		assertThat(description.getComment()).isEqualTo(comment);
		
		// Check default offering
		Offering[] expectedOfferings;
		if (url == secondaryUSDLPath) {
			expectedOfferings = new Offering[] {FIRST_OFFERING, SECOND_OFFERING};
		} else {	// defaultUSDLPath
			expectedOfferings = new Offering[] {FIRST_OFFERING};
		}
		
		List<Offering> descriptionOfferings = description.getOfferings();
		assertThat(descriptionOfferings.size()).isEqualTo(expectedOfferings.length);
		
		for (int i = 0; i < expectedOfferings.length; i++) {
			
			// Look for the offering in the description
			boolean found = false;
			int j = 0;

			for (j = 0; !found && j < descriptionOfferings.size(); j++) {
				found = expectedOfferings[i].getDisplayName().equals(descriptionOfferings.get(j).getDisplayName());
			}
			
			// Check that the offering has been found
			assertThat(found).isTrue();
			
			// Check that all the properties are as expected			
			assertThat(descriptionOfferings.get(j-1).getDisplayName()).isEqualTo(expectedOfferings[i].getDisplayName());
			assertThat(descriptionOfferings.get(j-1).getDescription()).isEqualTo(expectedOfferings[i].getDescription());
			assertThat(descriptionOfferings.get(j-1).getImageUrl()).isEqualTo(expectedOfferings[i].getImageUrl());

		}
	}
	
	private Response createOrUpdateDescription(String userName, String password, String storeName, 
			String descriptionName, String displayName, String url, String comment) {
		
		Description description = new Description();
		description.setDisplayName(displayName);
		description.setUrl(url);
		description.setComment(comment);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + storeName + "/description/" + descriptionName)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getAuthorization(userName, password))
				.post(Entity.entity(description, MediaType.APPLICATION_JSON));
		
		return response;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private Response createDescription(String userName, String password, String storeName, String displayName,
			String url, String comment) {
		return createOrUpdateDescription(userName, password, storeName, "", displayName, url, comment);
	}
	
	public void testCreation(String url) {
		String displayName = "Description 1";
		String descriptionName = "description-1";
		String descriptionComment = "Example Comment";
		
		Response response = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, url, 
				descriptionComment);	
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeaderString("Location")).isEqualTo(endPoint + "/api/v2/store/" + STORE_NAME +
				"/description/" + descriptionName);
		
		// Check that the description actually exists
		checkDescription(USER_NAME, PASSWORD, STORE_NAME, descriptionName, displayName, url, descriptionComment);

	}
	
	@Test
	public void testCreationDefaultUSDL() {
		testCreation(defaultUSDLPath);
	}
	
	@Test
	public void testCreationSecondaryUSDL() {
		testCreation(secondaryUSDLPath);
	}
	
	private void testCreationInvalidField(String displayName, String url, String comment, String invalidField,
			String message) {

		Response response = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, url, comment);
		checkAPIError(response, 400, invalidField, message, ErrorType.VALIDATION_ERROR);
	}
	
	@Test
	public void testCreationDisplayNameInvalid() {
		testCreationInvalidField("Description!", defaultUSDLPath, "", "displayName", 
				MESSAGE_INVALID_DISPLAY_NAME);
	}
	
	@Test
	public void testCreationDisplayNameTooShort() {
		testCreationInvalidField("a", defaultUSDLPath, "", "displayName", 
				String.format(MESSAGE_TOO_SHORT, 3));
	}
	
	@Test
	public void testCreationDisplayNameTooLong() {
		testCreationInvalidField("abcdefghijklmnopqrstuvwxyz", defaultUSDLPath, "", "displayName", 
				String.format(MESSAGE_TOO_LONG, 20));
	}
	
	@Test
	public void testCreationURLInvalid() {
		testCreationInvalidField("Description", "https:/127.0.0.1:" + wireMock.port(), "", "url", 
				MESSAGE_INVALID_URL);
	}
	
	@Test
	public void testCreationRDFInvalid() {
		testCreationInvalidField("Description", serverUrl, "", "url", MESSAGE_INVALID_RDF);

	}
	
	@Test
	public void testCreationCommentTooLong() {
		testCreationInvalidField("Description", defaultUSDLPath, "12345678901234567890123456789012345678901234567890"
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
		String displayName = "Description 1";
		
		// name is based on display name and name is checked before display name...
		testCreationFieldAlreayExists(displayName, displayName, defaultUSDLPath, defaultUSDLPath + "a", "name", 
				MESSAGE_NAME_IN_USE);
	}
	
	@Test
	public void testCreationURLAlreadyExists() {
		testCreationFieldAlreayExists("offering-1", "offering-2", defaultUSDLPath, defaultUSDLPath, "url", 
				MESSAGE_URL_IN_USE);
	}
	
	@Test
	public void testCreationNameAndUrlAlreayExistsInAnotherStore() {

		// Create another Store
		String newStoreName = STORE_NAME + "a";
		String descriptionName = "description-1"; 
		
		createStore(USER_NAME, PASSWORD, newStoreName, STORE_URL + ":8000");
		
		Response createResponse1 = createDescription(USER_NAME, PASSWORD, STORE_NAME, 
				descriptionName, defaultUSDLPath, "");
		Response createResponse2 = createDescription(USER_NAME, PASSWORD, newStoreName, 
				descriptionName, defaultUSDLPath, "");
		
		// Both offerings can be created
		assertThat(createResponse1.getStatus()).isEqualTo(201);
		assertThat(createResponse2.getStatus()).isEqualTo(201);
		
	}
	
	@Test
	public void testDeleteUserWithDescription() {
		String name = "description-1";
		
		Response createStoreResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, name, 
				defaultUSDLPath, "");
		assertThat(createStoreResponse.getStatus()).isEqualTo(201);
		checkDescription(USER_NAME, PASSWORD, STORE_NAME, name, name, defaultUSDLPath, "");
		
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
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private Response updateDescription(String userName, String password, String storeName, 
			String descriptionName, String displayName, String url, String comment) {
		return createOrUpdateDescription(userName, password, storeName, descriptionName, displayName, url, comment);
	}
	
	public void testUpdate(String newDisplayName, String newUrl, String newComment) {
		// Create Description
		String name = "description-1";
		String displayName = "Description-1";
		String comment = "commnet1";
		
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, 
				defaultUSDLPath, comment);
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);
		
		// Update the description		
		Response updateDescriptionResponse = updateDescription(USER_NAME, PASSWORD, STORE_NAME, name, 
				newDisplayName, newUrl, newComment);
		assertThat(updateDescriptionResponse.getStatus()).isEqualTo(200);
		
		// Check that the description has been updated
		String expectedDisplayName = newDisplayName == null ? displayName : newDisplayName;
		String expectedUrl = newUrl == null ? defaultUSDLPath : newUrl;
		String expectedComment = newComment == null ? comment : newComment;
		
		checkDescription(USER_NAME, PASSWORD, STORE_NAME, name, expectedDisplayName, expectedUrl, expectedComment);

	}
	
	@Test
	public void testUpdateNameAndDescription() {
		testUpdate("Description 2", null, "comment-2");
	}
	
	@Test
	public void tesUpdateUrlSameUrl() {
		testUpdate(null, defaultUSDLPath, null);
	}
	
	@Test
	public void testUpdateUrlDifferentUrl() {
		testUpdate(null, secondaryUSDLPath, null);
	}
	
	private void testUpdateInvalidField(String newDisplayName, String newUrl, String newComment, 
			String invalidField, String message) {
		
		String name = "offering";
		String displayName = "Offering";
		String comment = "";
		
		Response createResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, 
				defaultUSDLPath, comment);
		assertThat(createResponse.getStatus()).isEqualTo(201);

		Response updateResponse = updateDescription(USER_NAME, PASSWORD, STORE_NAME, name, newDisplayName, 
				newUrl, newComment);
		checkAPIError(updateResponse, 400, invalidField, message, ErrorType.VALIDATION_ERROR);
	}
	
	@Test
	public void testUpdateDisplayNameInvalid() {
		testUpdateInvalidField("Description!", defaultUSDLPath, "", "displayName", 
				MESSAGE_INVALID_DISPLAY_NAME);
	}
	
	@Test
	public void testUpdateDisplayNameTooShort() {
		testUpdateInvalidField("a", defaultUSDLPath, "", "displayName", 
				String.format(MESSAGE_TOO_SHORT, 3));
	}
	
	@Test
	public void testUpdateDisplayNameTooLong() {
		testUpdateInvalidField("abcdefghijklmnopqrstuvwxyz", defaultUSDLPath, "", "displayName", 
				String.format(MESSAGE_TOO_LONG, 20));
	}
	
	@Test
	public void testUpdateURLInvalid() {
		testUpdateInvalidField("Description", "https:/store.lab.fiware.org/offering1.rdf", "", "url", 
				MESSAGE_INVALID_URL);
	}
	
	@Test
	public void testUpdateRDFInvalid() {
		testUpdateInvalidField("Description", serverUrl, "", "url", MESSAGE_INVALID_RDF);
	}
	
	@Test
	public void testUpdateCommentTooLong() {
		testUpdateInvalidField("Offering", serverUrl, "12345678901234567890123456789012345678901234567890"
				+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456"
				+ "7890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", 
				"comment", String.format(MESSAGE_TOO_LONG, 200));
	}
	
	/**
	 * This methods creates two descriptions, based on the different parameters and tries to updates the second
	 * description based on updatedDisplayName and updatedURL. However, it's expected that one of these parameters
	 * has been used to create the first description, so an error should arise. Error details should be contained in
	 * field an expectedMessage.
	 * @param nameDescription1 The name of the first description
	 * @param urlDescription1 The URL of the first description
	 * @param nameDescription2 The name of the second description (the one to be updated). This name is used to modify 
	 * the description so this is not a displayName but the name (the one without spaces, ...) 
	 * @param urlDescription2 The URL of the second description (the one to be updated)
	 * @param updatedDisplayName The new display name to be set in the second description
	 * @param updatedURL The new URL to be set in the second description
	 * @param field The field that is repeated
	 * @param expectedMessage Expected error message
	 */
	private void testUpdateFieldAlreayExists(
			String nameDescription1,String urlDescription1, 
			String nameDescription2, String urlDescription2,
			String updatedDisplayName, String updatedURL,
			String field, String expectedMessage) {
		
		Response createStore1Response = createDescription(USER_NAME, PASSWORD, STORE_NAME, nameDescription1, 
				urlDescription1, "");
		Response createStore2Response = createDescription(USER_NAME, PASSWORD, STORE_NAME, nameDescription2, 
				urlDescription2, "");
		assertThat(createStore1Response.getStatus()).isEqualTo(201);
		assertThat(createStore2Response.getStatus()).isEqualTo(201);
		
		Response updateResponse = updateDescription(USER_NAME, PASSWORD, STORE_NAME, nameDescription2, 
				updatedDisplayName, updatedURL, null);
		checkAPIError(updateResponse, 400, field, expectedMessage, ErrorType.VALIDATION_ERROR);

	}
	
	@Test
	public void testUpdateDisplayNameAlreadyExists() {		
		String displayName = "description";
		
		testUpdateFieldAlreayExists(
				displayName, defaultUSDLPath, 
				"descritpion-2", secondaryUSDLPath, 
				displayName, null, 
				"displayName", MESSAGE_NAME_IN_USE);	
	}
	
	@Test
	public void testUpdateURLAlreadyExists() {
		testUpdateFieldAlreayExists(
				"description", defaultUSDLPath, 
				"description-2", secondaryUSDLPath, 
				"description-2", defaultUSDLPath, 
				"url", MESSAGE_URL_IN_USE);	
	}
	
	@Test
	public void testUpdateNonExisting() {
		
		String displayName = "offering-1";
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, 
				defaultUSDLPath, "");
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);
		
		// Update non-existing description
		String descriptionToBeUpdated = displayName + "a";  	//This ID is supposed not to exist
		Response updateDescriptionResponse = updateDescription(USER_NAME, PASSWORD, STORE_NAME, 
				descriptionToBeUpdated, "new display", null, null);
		checkAPIError(updateDescriptionResponse, 404, null, 
				String.format(MESSAGE_DESCRIPTION_NOT_FOUND, descriptionToBeUpdated), ErrorType.NOT_FOUND);	
	}
	
	@Test
	public void testUpdateWithAnotherUser() {
		
		String displayName = "offering-1";
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, displayName, 
				defaultUSDLPath, "");
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);

		// Create another user
		String newUserName = USER_NAME + "a";
		String email = "new_email__@example.com";
		createUser(newUserName, email, PASSWORD);
		
		// Update description with the new user
		Response updateDescriptionResponse = updateDescription(newUserName, PASSWORD, STORE_NAME, displayName, 
				"new display name", null, null);
		checkAPIError(updateDescriptionResponse, 403, null, 
				String.format(MESSAGE_NOT_AUTHORIZED, "update description"), ErrorType.FORBIDDEN);	

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private Response deleteDescription(String authUserName, String authPassword, String storeName, 
			String descriptionName) {
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + storeName + "/description/" + descriptionName)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getAuthorization(authUserName, authPassword))
				.delete();
		
		return response;

	}
	
	@Test
	public void testDelete() {
		
		String name = "description-1";
		
		// Create the description
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, name, 
				defaultUSDLPath, null);
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);
		
		// Delete the description
		Response deleteDescriptionResponse = deleteDescription(USER_NAME, PASSWORD, STORE_NAME, name);
		assertThat(deleteDescriptionResponse.getStatus()).isEqualTo(204);
	}
	
	@Test
	public void testDeleteNonExisting() {
		
		String name = "description-1";
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, name, 
				defaultUSDLPath, null);
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);
		
		// Delete non-existing description
		String descriptionToBeDeleted = name + "a";  	//This ID is supposed not to exist
		Response deleteStoreResponse = deleteDescription(USER_NAME, PASSWORD, STORE_NAME, descriptionToBeDeleted);
		checkAPIError(deleteStoreResponse, 404, null, 
				String.format(MESSAGE_DESCRIPTION_NOT_FOUND, descriptionToBeDeleted), ErrorType.NOT_FOUND);	
	}
	
	@Test
	public void testDeleteWithAnotherUser() {
		
		String name = "description-1";
		Response createDescriptionResponse = createDescription(USER_NAME, PASSWORD, STORE_NAME, name, 
				defaultUSDLPath, null);
		assertThat(createDescriptionResponse.getStatus()).isEqualTo(201);

		// Create another user
		String newUserName = USER_NAME + "a";
		String email = "new_email__@example.com";
		createUser(newUserName, email, PASSWORD);
		
		//Delete user
		Response deleteStoreResponse = deleteDescription(newUserName, PASSWORD, STORE_NAME, name);
		checkAPIError(deleteStoreResponse, 403, null, String.format(MESSAGE_NOT_AUTHORIZED, "delete description"), 
				ErrorType.FORBIDDEN);	

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// LIST STORES /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testListAllDescriptions() {
		
		// Create some descriptions
		int DESCRIPTIONS_CREATED = 6;
		String displayNamePattern = "Store %d";
		String urlPattern = serverUrl + "/default%d.rdf";
		
		for (int i = 0; i < DESCRIPTIONS_CREATED; i++) {
			createDescription(USER_NAME, PASSWORD, STORE_NAME, String.format(displayNamePattern, i), 
					String.format(urlPattern, i), "");
		}
		
		// Get all descriptions
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + STORE_NAME + "/description")
				.request(MediaType.APPLICATION_XML).header("Authorization", getAuthorization(USER_NAME, PASSWORD))
				.get();
		
		// Check the response
		assertThat(response.getStatus()).isEqualTo(200);
		Descriptions descriptions = response.readEntity(Descriptions.class);
		assertThat(descriptions.getDescriptions().size()).isEqualTo(DESCRIPTIONS_CREATED);
		
		// Users are supposed to be returned in order
		for (int i = 0; i < DESCRIPTIONS_CREATED; i++) {
			Description description = descriptions.getDescriptions().get(i);
			assertThat(description.getDisplayName()).isEqualTo(String.format(displayNamePattern, i));
			assertThat(description.getUrl()).isEqualTo(String.format(urlPattern, i));
		}
	}
	
	private void testListSomeStores(int offset, int max) {
		
		// Create some descriptions
		int DESCRIPTIONS_CREATED = 10;
		String displayNamePattern = "Store %d";
		String urlPattern = serverUrl + "/default%d.rdf";
		
		for (int i = 0; i < DESCRIPTIONS_CREATED; i++) {
			createDescription(USER_NAME, PASSWORD, STORE_NAME, String.format(displayNamePattern, i), 
					String.format(urlPattern, i), "");
		}
		
		// Get some descriptions
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + STORE_NAME + "/description")
				.queryParam("offset", offset)
				.queryParam("max", max)
				.request(MediaType.APPLICATION_XML)
				.header("Authorization", getAuthorization(USER_NAME, PASSWORD))
				.get();
		
		// Check the response
		int expectedElements = offset + max > DESCRIPTIONS_CREATED ? DESCRIPTIONS_CREATED - offset : max;
		assertThat(response.getStatus()).isEqualTo(200);
		Descriptions descriptions = response.readEntity(Descriptions.class);
		assertThat(descriptions.getDescriptions().size()).isEqualTo(expectedElements);
		
		// Users are supposed to be returned in order
		for (int i = offset; i < offset + expectedElements; i++) {
			Description description = descriptions.getDescriptions().get(i - offset);
			assertThat(description.getDisplayName()).isEqualTo(String.format(displayNamePattern, i));
			assertThat(description.getUrl()).isEqualTo(String.format(urlPattern, i));
		}
	}
	
	@Test
	public void testListSomeStoresMaxInRange() {
		testListSomeStores(3, 4);
	}
	
	@Test
	public void testListSomeStoresMaxNotInRange() {
		testListSomeStores(5, 7);
	}
	
	private void testListStoresInvalidParams(int offset, int max) {
		Client client = ClientBuilder.newClient();
		Response response = client.target(endPoint + "/api/v2/store/" + STORE_NAME + "/description")
				.queryParam("offset", offset)
				.queryParam("max", max)
				.request(MediaType.APPLICATION_XML)
				.header("Authorization", getAuthorization(USER_NAME, PASSWORD))
				.get();
		
		checkAPIError(response, 400, null, MESSAGE_INVALID_OFFSET_MAX, ErrorType.BAD_REQUEST);

	}
	
	@Test
	public void testListStoresInvalidOffset() {
		testListStoresInvalidParams(-1, 2);
	}
	
	@Test
	public void testListStoresInvalidMax() {
		testListStoresInvalidParams(1, 0);
	}

	
}
