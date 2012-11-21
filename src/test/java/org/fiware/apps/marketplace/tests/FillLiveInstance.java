package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.fiware.apps.marketplace.client.ServiceClient;
import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.client.UserClient;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Test;

public class FillLiveInstance {
	private static final String BASE_URL = "http://130.206.81.36:8080/FiwareMarketplace/v1";	
	UserClient userClient = new UserClient(BASE_URL, "demo1234", "demo1234");
	
	@Test	
	public void testRegistration()  {

		
		Localuser user  = new Localuser();	
		user.setCompany("IntegrationCompany");
		user.setEmail("integration123@integrationcompany.com");
		user.setPassword("integrationUser123");
		user.setUsername("integrationUser123");

		//assertTrue(userClient.save(user));


		StoreClient storeClient = new StoreClient(BASE_URL,"integrationUser123", "integrationUser123");
		ServiceClient serviceClient = new ServiceClient(BASE_URL,"integrationUser123", "integrationUser123");
		
			
		Store store  = new Store();		
		
		store.setName("integrationStore");
		store.setDescription("Integration Store Description");
		store.setUrl("http://www.integrationStore.com");		
		//assertTrue(storeClient.save(store));
		

		Service service = new Service();
		service.setName("integrationOffering");
		service.setUrl("http://130.206.81.36:8080/FiwareRepository/v1/integrationCollection/IntegrationService.usdl");
		
		//assertTrue(serviceClient.update("integrationStore", "integrationOffering", service));
		assertTrue(serviceClient.save("integrationStore", service));
		
		/*
		Service	service = serviceClient.find("integrationStore", "integrationOffering");
		
		assertTrue(serviceClient.update("integrationStore", "integrationStore", service));
		
		*/

		
		
	}
}
