package org.fiware.apps.marketplace.tests;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.fiware.apps.marketplace.client.ServiceClient;
import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.client.UserClient;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Ignore;
import org.junit.Test;

public class FillLiveInstance {
	private static final String BASE_URL = "http://130.206.81.36:8080/FiwareMarketplace/v1";	
	UserClient userClient = new UserClient(BASE_URL, "demo1234", "demo1234");
	
	@Test
	@Ignore
	public void testRegistration()  {

		
		User user  = new User();	
		user.setCompany("IntegrationCompany");
		user.setEmail("integration123@integrationcompany.com");
		user.setPassword("integrationUser123");
		user.setUserName("integrationUser123");

		//assertTrue(userClient.save(user));


		StoreClient storeClient = new StoreClient(BASE_URL,"integrationUser123", "integrationUser123");
		ServiceClient serviceClient = new ServiceClient(BASE_URL,"integrationUser123", "integrationUser123");
		
			
		Store store  = new Store();		
		
		store.setName("integrationStore");
		store.setDescription("Integration Store Description");
		store.setUrl("http://www.integrationStore.com");		
		//assertTrue(storeClient.save(store));
		

		Description service = new Description();
		service.setName("integrationOffering");
		service.setUrl("http://130.206.81.36:8080/FiwareRepository/v1/integrationCollection/IntegrationService.usdl");
		
		//assertTrue(serviceClient.update("integrationStore", "integrationOffering", service));
		//assertTrue(serviceClient.save("integrationStore", service));
		
		/*
		Service	service = serviceClient.find("integrationStore", "integrationOffering");
		
		assertTrue(serviceClient.update("integrationStore", "integrationStore", service));
		
		*/

		
		
	}
}
