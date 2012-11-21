package org.fiware.apps.marketplace.client;

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


	public boolean save(Store store){

		
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

	}
}
