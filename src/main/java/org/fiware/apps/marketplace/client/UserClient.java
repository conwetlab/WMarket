package org.fiware.apps.marketplace.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.fiware.apps.marketplace.model.Localuser;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class UserClient extends MarketplaceClient {


	public UserClient(String endpoint, String user, String pwd) {
		super(endpoint, user, pwd);	
	}

	private static String USER_SERVICE_SAVE = "/registration/userManagement/user";
	private static String USER_SERVICE_UPDATE = "/registration/userManagement/user";
	private static String USER_SERVICE_DELETE= "/registration/userManagement/user";
	private static String USER_SERVICE_FIND= "/registration/userManagement/user";
			

	public boolean save(Localuser user){

		
		ClientRequest request = createRequest(endpoint+USER_SERVICE_SAVE);


		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Localuser.class);

			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(user, writer);			

			String input = writer.toString();
			System.out.println(input);
			request.body("application/xml", input);

			ClientResponse<String> response;
			response = request.put(String.class);		
			ClientUtil.visualize(request, response, "Save User");

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

	public boolean update(Localuser user, String name){
		ClientRequest request = createRequest(endpoint+USER_SERVICE_UPDATE+"/"+name);

		request.accept("application/xml");

		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Localuser.class);

			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(user, writer);			

			String input = writer.toString();
			request.body("application/xml", input);
			ClientResponse<String> response;
			response = request.post(String.class);	
			ClientUtil.visualize(request, response, "Update User");
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
			ClientRequest request = createRequest(endpoint+USER_SERVICE_DELETE+"/"+id);

			request.accept("application/xml");
			ClientResponse<String> response = request.delete(String.class);		
			ClientUtil.visualize(request, response, "Delete User");
			if(response.getStatus() == 200){
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public Localuser find(String name){
		Localuser r= null;
		String xml = "";
		try {
			ClientRequest request = createRequest(endpoint+USER_SERVICE_FIND+"/"+name);
	
			request.accept("application/xml");
			ClientResponse<String> response = request.get(String.class);		
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;			
			while ((output = br.readLine()) != null) {				
				xml += output;
			}

			JAXBContext ctx;			
			ctx = JAXBContext.newInstance(Localuser.class);
			ClientUtil.visualize(request, response, "Find User");
			r = (Localuser) ctx.createUnmarshaller().unmarshal(new StringReader(xml));	

		} catch (Exception e) {
			return null;
		}
		return r;	

	}
}
