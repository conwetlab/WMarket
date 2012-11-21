package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.fiware.apps.marketplace.client.UserClient;
import org.fiware.apps.marketplace.model.Localuser;
import org.junit.Test;

public class ParticipantTest {
	
	
	private static final String BASE_URL = "http://localhost:8080/FiwareMarketplace/v1";	
	private static UserClient userClient = new UserClient(BASE_URL, "demo1234", "demo1234");
	
	@Test	
	public void testRegistration()  {
	
		Localuser user  = new Localuser();	
		user.setCompany("SAP");
		user.setEmail("demo123@sap.com");
		user.setPassword("demo");
		user.setUsername("demo");

		assertTrue(userClient.save(user));

		user  = userClient.find("demo");
		assertEquals(user.getCompany(), "SAP");
		
		user.setCompany("SAP AG");
		assertTrue(userClient.update(user, "demo"));
		
		user  = userClient.find("demo");
		assertEquals(user.getCompany(), "SAP AG");
		/*
		assertTrue(userClient.delete("demouser"));
		assertNull(userClient.find("demouser"));
		*/
		
	}

}
