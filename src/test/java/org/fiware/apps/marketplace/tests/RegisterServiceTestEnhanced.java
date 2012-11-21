package org.fiware.apps.marketplace.tests;

import static org.junit.Assert.*;

import org.fiware.apps.marketplace.client.ServiceClient;
import org.fiware.apps.marketplace.client.StoreClient;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.junit.Test;

public class RegisterServiceTestEnhanced {
	
	private static final String BASE_URL = "http://localhost:8080/FiwareMarketplace/v1";	
	private static StoreClient storeClient = new StoreClient(BASE_URL,"demo", "demo");
	private static ServiceClient serviceClient = new ServiceClient(BASE_URL,"demo", "demo");
	
	@Test
	/**
	 * Test for registration of authentic cloud services
	 */
	public void testRegistrationEnhanced() {
		String storeName = "AuthenticCloudServicesStore";
		String amazonEC2Name = "Amazon_EC2";
		String ElasticHostsCloudHostingName = "ElasticHosts_CloudHosting";
		String RackspaceCloudServersName = "Rackspace_CloudServers";
		String VISIReliaCloudName = "VISI_ReliaCloud";
		
		String storeURL = "http://www.test.de";
		String amazonEC2URL = "http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/cloudServices/Amazon_EC2_001.rdf";
		String ElasticHostsCloudHostingURL = "http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/cloudServices/ElasticHosts_CloudHosting_001.rdf";
		String RackspaceCloudServersURL = "http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/cloudServices/Rackspace_CloudServers_001.rdf";
		String VISIReliaCloudURL = "http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/cloudServices/Visi_ReliaCloud_001.rdf";

		//Delete if already existing
		serviceClient.delete(storeName, amazonEC2Name);
		serviceClient.delete(storeName, ElasticHostsCloudHostingName);
		serviceClient.delete(storeName, RackspaceCloudServersName);
		serviceClient.delete(storeName, VISIReliaCloudName);
		storeClient.delete(storeName);
		
		createStoreTest(storeName, storeURL, "Store for services that are based on real cloud services.");
		createServiceTest(amazonEC2Name, amazonEC2URL, storeName);
		createServiceTest(ElasticHostsCloudHostingName, ElasticHostsCloudHostingURL, storeName);
		createServiceTest(RackspaceCloudServersName, RackspaceCloudServersURL, storeName);
		createServiceTest(VISIReliaCloudName, VISIReliaCloudURL, storeName);

		/*
		//Clean 
		assertTrue(serviceClient.delete(storeName, amazonEC2Name));
		assertTrue(serviceClient.delete(storeName, ElasticHostsCloudHostingName));
		assertTrue(serviceClient.delete(storeName, RackspaceCloudServersName));
		assertTrue(serviceClient.delete(storeName, VISIReliaCloudName));
		assertTrue(storeClient.delete(storeName));
		*/
	}
	
	private Store createStoreTest(String storeName, String storeURL, String storeDesc){
		Store store = new Store();
		store.setName(storeName);
		store.setUrl(storeURL);
		store.setDescription(storeDesc);

		assertTrue(storeClient.save(store));
		assertTrue(storeClient.update(store, storeName));
		assertNotNull(storeClient.find(storeName));
		
		return store;
	}
	
	private Service createServiceTest(String serviceName, String serviceURL, String storeName){
		Service service = new Service();
		service.setName(serviceName);
		service.setUrl(serviceURL);

		//Save fails if URL path contains an upper case letter after "/": "Content is not allowed in prolog" (Jena)
		assertTrue(serviceClient.save(storeName, service));	
		assertNotNull(serviceClient.find(storeName, serviceName));
		
		return service;
	}
}
