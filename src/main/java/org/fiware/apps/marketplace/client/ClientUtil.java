package org.fiware.apps.marketplace.client;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class ClientUtil {
	public static void visualize(ClientRequest request, ClientResponse<String>  response, String text ){
		System.out.println(text+"\n\n");
		System.out.println(text+" Request:\n\n");
		try {
			System.out.println("Resource URL:\n"+ request.getUri());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Header:\n"+ request.getHeaders().toString());
		System.out.println("Body:\n"+ request.getBody());
		
		System.out.println(text+"Response:\n\n");
		System.out.println("Response Status:\n"+ response.getStatus());	
		System.out.println("____________________________________________________________\n\n");
	}
}
