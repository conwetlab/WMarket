package org.fiware.apps.marketplace.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

public abstract class MarketplaceClient {
	
	protected String endpoint;
	protected String user;
	protected String pwd;

	
	public MarketplaceClient(String endpoint, String user, String pwd) {		
		this.endpoint = endpoint;
		this.user = user;
		this.pwd = pwd;
	}
	
	protected ClientRequest createRequest(String uriString){
		URI uri=null;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		Credentials credentials = new UsernamePasswordCredentials(user, pwd);
		HttpClient httpClient = new HttpClient();
		httpClient.getState().setCredentials(AuthScope.ANY, credentials);
		httpClient.getParams().setAuthenticationPreemptive(true);

		ClientExecutor clientExecutor = new ApacheHttpClientExecutor(httpClient);

		ClientRequestFactory fac = new ClientRequestFactory(clientExecutor, uri);
		ClientRequest request = fac.createRequest(uriString);		
		
		return request;

	}


}
