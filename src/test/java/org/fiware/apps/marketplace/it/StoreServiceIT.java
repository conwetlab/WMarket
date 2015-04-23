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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Test;

public class StoreServiceIT extends AbstractIT {
	
	private final static String USER_NAME = "marketplace";
	private final static String PASSWORD = "password1!a";
	private final static String EMAIL = "example@example.com";
	
	private final static String MESSAGE_NAME_IN_USE = "This name is already in use.";
	private final static String MESSAGE_URL_IN_USE = "This URL is already in use.";
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// AUXILIAR //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private Response getStore(String userName, String password, String name) {
		Client client = ClientBuilder.newClient();
		return client.target(endPoint + "/api/v2/store/" + name).request(MediaType.APPLICATION_JSON)
				.header("Authorization", getAuthorization(userName, password)).get();
	}
	
	private void checkStore(String userName, String password, String name, String displayName, 
			String url, String comment) {
		
		Store store = getStore(userName, password, name).readEntity(Store.class);
		
		assertThat(store.getName()).isEqualTo(name);
		assertThat(store.getDisplayName()).isEqualTo(displayName);
		assertThat(store.getUrl()).isEqualTo(url);
		assertThat(store.getComment()).isEqualTo(comment);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testCreation() {
		
		// Before creating the store, the user must be created
		createUser(USER_NAME, EMAIL, PASSWORD);
		
		String storeDisplayName = "WMarket";
		String storeName = "wmarket";
		String storeUrl = "https://store.lab.fiware.org";
		String storeComment = "Example Comment";
		
		Response response = createStore(USER_NAME, PASSWORD, storeDisplayName, storeUrl, storeComment, null);
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeaderString("Location")).isEqualTo(endPoint + "/api/v2/store/" + storeName);
		
		// Check that the store actually exists
		checkStore(USER_NAME, PASSWORD, storeName, storeDisplayName, storeUrl, storeComment);
	}
	
	private void testCreationInvalidField(String displayName, String url, String comment, String invalidField,
			String message) {
		// Before creating the store, the user must be created
		createUser(USER_NAME, EMAIL, PASSWORD);
		
		Response response = createStore(USER_NAME, PASSWORD, displayName, url, comment, null);
		checkAPIError(response, 400, invalidField, message, ErrorType.VALIDATION_ERROR);
	}
	
	@Test
	public void testCreationDisplayNameInvalid() {
		testCreationInvalidField("Wstore!", "https://store.lab.fiware.org", "", "displayName", 
				MESSAGE_INVALID_DISPLAY_NAME);
	}
	
	@Test
	public void testCreationDisplayNameTooShort() {
		testCreationInvalidField("a", "https://store.lab.fiware.org", "", "displayName", 
				String.format(MESSAGE_TOO_SHORT, 3));
	}
	
	@Test
	public void testCreationDisplayNameTooLong() {
		testCreationInvalidField("abcdefghijklmnopqrstuvwxyz", "https://store.lab.fiware.org", "", "displayName", 
				String.format(MESSAGE_TOO_LONG, 20));
	}
	
	@Test
	public void testCreationURLInvalid() {
		testCreationInvalidField("Wstore", "https:/store.lab.fiware.org", "", "url", 
				MESSAGE_INVALID_URL);
	}
	
	@Test
	public void testCreationCommentTooLong() {
		testCreationInvalidField("Wstore", "https://store.com", "12345678901234567890123456789012345678901234567890"
				+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456"
				+ "7890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", 
				"comment", String.format(MESSAGE_TOO_LONG, 200));
	}
	
	private void testCreationFieldAlreayExists(String displayName1, String displayName2, String url1, String url2,
			String field, String expectedMessage) {
		// Before creating the store, the user must be created
		createUser(USER_NAME, EMAIL, PASSWORD);
		
		createStore(USER_NAME, PASSWORD, displayName1, url1, "", null);
		Response response = createStore(USER_NAME, PASSWORD, displayName2, url2, "", null);
		
		// name is based on display name and name is checked before display name...
		checkAPIError(response, 400, field, expectedMessage, ErrorType.VALIDATION_ERROR);

	}
	
	@Test
	public void testCreationDisplayNameAlreadyExists() {		
		String displayName = "Wmarket";
		testCreationFieldAlreayExists(displayName, displayName, "https://store.com", "http://market.com", 
				"name", MESSAGE_NAME_IN_USE);
	}
	
	@Test
	public void testCreationURLAlreadyExists() {
		String url = "https://store.lab.fiware.org";
		testCreationFieldAlreayExists("wstore1", "wstore2", url, url, "url", MESSAGE_URL_IN_USE);
	}
	

}
