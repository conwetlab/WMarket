package org.fiware.apps.marketplace.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.fiware.apps.marketplace.model.Service;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class ServiceClient extends MarketplaceClient {


	public ServiceClient(String endpoint, String user, String pwd) {
		super(endpoint, user, pwd);	
	}

	private static String REGISTRATION_SERVICE_SAVE = "/offering/store";
	private static String REGISTRATION_SERVICE_UPDATE = "/offering/store";
	private static String REGISTRATION_SERVICE_DELETE= "/offering/store";
	private static String REGISTRATION_SERVICE_FIND= "/offering/store";
			


	public boolean save(String storeName, Service service){
		ClientRequest request = createRequest(endpoint+REGISTRATION_SERVICE_SAVE+"/"+storeName+"/offering");	
		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Service.class);
			
			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(service, writer);			

			String input = writer.toString();
			System.out.println(input);
			request.body("application/xml", input);
			ClientResponse<String> response;
			response = request.put(String.class);		
			ClientUtil.visualize(request, response, "Save Service");
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

	public boolean update(String storeName, String serviceName, Service service){
		ClientRequest request = createRequest(endpoint+REGISTRATION_SERVICE_UPDATE+"/"+storeName+"/offering/"+serviceName);	

		request.accept("application/xml");

		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Service.class);
			
			StringWriter writer = new StringWriter();
			ctx.createMarshaller().marshal(service, writer);			

			String input = writer.toString();
			request.body("application/xml", input);
			ClientResponse<String> response;
			response = request.post(String.class);	
			ClientUtil.visualize(request, response, "Update Service");
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

	public boolean delete(String storeName, String serviceName){
		try {
			ClientRequest request = createRequest(endpoint+REGISTRATION_SERVICE_DELETE+"/"+storeName+"/offering/"+serviceName);
			
			request.accept("application/xml");
			ClientResponse<String> response = request.delete(String.class);		
			ClientUtil.visualize(request, response, "Delete Service");
			if(response.getStatus() == 200){
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	
	public Service find(String storeName, String serviceName){
		Service r= null;
		String xml = "";
		try {
			ClientRequest request = createRequest(endpoint+REGISTRATION_SERVICE_FIND+"/"+storeName+"/offering/"+serviceName);
			
			request.accept("application/xml");
			ClientResponse<String> response = request.get(String.class);		
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
			ClientUtil.visualize(request, response, "Find Service");
			String output;			
			while ((output = br.readLine()) != null) {				
				xml += output;
			}

			JAXBContext ctx;			
			ctx = JAXBContext.newInstance(Service.class);

			r = (Service) ctx.createUnmarshaller().unmarshal(new StringReader(xml));	

		} catch (Exception e) {
			return null;
		}
		return r;	

	}



}
