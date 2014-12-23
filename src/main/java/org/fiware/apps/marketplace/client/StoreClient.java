package org.fiware.apps.marketplace.client;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
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


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.fiware.apps.marketplace.model.Store;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;


public class StoreClient extends MarketplaceClient{

	public StoreClient(String endpoint, String user, String pwd) {
		super(endpoint, user, pwd);		
	}

	private static String REGISTRATION_STORE_SAVE = "/registration/store";
	private static String REGISTRATION_STORE_UPDATE = "/registration/store";
	private static String REGISTRATION_STORE_DELETE = "/registration/store";
	private static String REGISTRATION_STORE_FIND = "/registration/store";


	/*public boolean save(Store store){

		
		ClientRequest request = createRequest(endpoint+REGISTRATION_STORE_SAVE);



		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Store.class);

			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(store, writer);			

			String input = writer.toString();
			System.out.println(input);
			request.body("application/xml", input);

			ClientResponse<String> response;
			response = request.put(String.class);		
			ClientUtil.visualize(request, response, "Save Store");

			if(response.getStatus() == 201){
				return true;
			}	
		}
		catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean update(Store store, String name){
		ClientRequest request = createRequest(endpoint+REGISTRATION_STORE_UPDATE+"/"+name);
		request.accept("application/xml");

		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Store.class);

			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(store, writer);			

			String input = writer.toString();
			request.body("application/xml", input);
			ClientResponse<String> response;
			response = request.post(String.class);	
			ClientUtil.visualize(request, response, "Update Store");
			if(response.getStatus() == 200){
				return true;
			}	
		}
		catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;

	}

	public boolean delete(String id){
		try {
			ClientRequest request = createRequest(endpoint+REGISTRATION_STORE_DELETE+"/"+id);

			request.accept("application/xml");
			ClientResponse<String> response = request.delete(String.class);		
			ClientUtil.visualize(request, response, "Delete Store");
			if(response.getStatus() == 200){
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public Store find(String name){
		Store r= null;
		String xml = "";
		try {
			ClientRequest request = createRequest(endpoint+REGISTRATION_STORE_FIND+"/"+name);
	
			request.accept("application/xml");
			ClientResponse<String> response = request.get(String.class);		
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;			
			while ((output = br.readLine()) != null) {				
				xml += output;
			}

			JAXBContext ctx;			
			ctx = JAXBContext.newInstance(Store.class);
			ClientUtil.visualize(request, response, "Find Store");
			r = (Store) ctx.createUnmarshaller().unmarshal(new StringReader(xml));	

		} catch (Exception e) {
			return null;
		}
		return r;	

	}*/
}
