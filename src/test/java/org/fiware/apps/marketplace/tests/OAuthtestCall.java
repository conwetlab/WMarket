package org.fiware.apps.marketplace.tests;

import java.io.File;
import java.util.Properties;

public class OAuthtestCall {


	public static void main(String[] args) throws Exception {
		
		Properties props = new Properties();
		File propFile = null;
		
		props.setProperty("consumerKey", "fwa");
		props.setProperty("consumerSecret", "");
		props.setProperty("requestUrl", "http://localhost:8080/FiwareMarketplace/v1/registration/stores/");
		props.setProperty("authorizationUrl", "http://localhost:8080/FiwareMarketplace/v1/registration/stores/");
		props.setProperty("accessUrl", "http://localhost:8080/FiwareMarketplace/v1/registration/stores/");
		props.setProperty("tokenSecret", "");
		
		OAuthtest oauthHelper;
		oauthHelper = new OAuthtest(props, propFile);

		oauthHelper.request();

	}
	
    
}