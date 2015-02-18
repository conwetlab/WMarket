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

import junit.framework.TestCase;

import org.fiware.apps.marketplace.client.ServiceClient;
import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Ignore;
import org.junit.Test;

public class RegisterServiceTest extends TestCase {
	
	
	private static final String BASE_URL = "http://localhost:8080/FiwareMarketplace/v1";	
	private static StoreClient storeClient = new StoreClient(BASE_URL,"demo", "demo");
	private static ServiceClient serviceClient = new ServiceClient(BASE_URL,"demo", "demo");
	
	@Test	
	@Ignore
	public void testRegistration()  {
		/*
		Store store  = new Store();		
	
		store.setName("testStore");
		store.setUrl("http://www.tests.de");
		
		assertTrue(storeClient.save(store));
		store.setDescription("Store Description");
		assertTrue(storeClient.update(store, "testStore"));
		storeClient.find("testStore");
		
		Service service = new Service();
		service.setName("myService");
		service.setUrl("http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/WarrantyManagementSolution_Master.rdf");
		
		assertTrue(serviceClient.save("testStore", service));
		
		service = serviceClient.find("testStore", "myService");
		assertEquals("myService", service.getName());
		
		service.setName("myService2");
		
		assertTrue(serviceClient.update("testStore", "myService", service));
		
		service = serviceClient.find("testStore", "myService2");
		assertEquals("myService2", service.getName());
	
	/*
		assertTrue(serviceClient.delete("testStore", "myService2"));
		assertTrue(storeClient.delete("testStore"));
	
		*/
		

	}
}
