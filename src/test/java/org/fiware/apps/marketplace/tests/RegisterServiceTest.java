package org.fiware.apps.marketplace.tests;

import junit.framework.TestCase;

import org.fiware.apps.marketplace.client.ServiceClient;
import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Test;

public class RegisterServiceTest extends TestCase {
	
	
	private static final String BASE_URL = "http://localhost:8080/FiwareMarketplace/v1";	
	private static StoreClient storeClient = new StoreClient(BASE_URL,"demo", "demo");
	private static ServiceClient serviceClient = new ServiceClient(BASE_URL,"demo", "demo");
	
	@Test	
	public void testRegistration()  {

		Store store  = new Store();		
	
		store.setName("testStore");
		store.setUrl("http://www.tests.de");
		
		assertTrue(storeClient.save(store));
		store.setDescription("Store Description");
		assertTrue(storeClient.update(store, "testStore"));
		storeClient.find("testStore");
		
		Service service = new Service();
		service.setName("myService");
		service.setUrl("http://appsnserv.lab.fi-ware.eu/cloudservices/rdf/WarrantyManagementSolution_Master.rdf");
		
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
