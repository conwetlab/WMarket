package org.fiware.apps.marketplace.tests;

import junit.framework.TestCase;

import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Test;

public class RegisterStoreTest extends TestCase {
	
	
	private static final String BASE_URL = "http://localhost:8080/FiwareMarketplace/v1";	
	private static StoreClient storeClient = new StoreClient(BASE_URL, "demo", "demo");
	
	@Test	
	public void testRegistration()  {
		Store store  = new Store();		

		store.setName("testName1235");
		store.setUrl("http://www.test1235.de");
		
		assertTrue(storeClient.save(store));

		store  = storeClient.find("testName1235");
		assertEquals(store.getUrl(), "http://www.test1235.de");
		
		store.setUrl("http://www.xxx.de");
		assertTrue(storeClient.update(store, "testName1235"));
		
		store  = storeClient.find("testName1235");
		assertEquals(store.getUrl(), "http://www.xxx.de");
		
		assertTrue(storeClient.delete("testName1235"));
		assertNull(storeClient.find("testName1235"));
		
		
	}
	

}
