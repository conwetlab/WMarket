package org.fiware.apps.marketplace.tests;

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.model.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App 
{
    public static void main( String[] args )
    {
    	ApplicationContext appContext = 
    	  new ClassPathXmlApplicationContext("spring/config/BeanLocations.xml");
 
    	ServiceBo serviceBo = (ServiceBo)appContext.getBean("serviceBo");
 
    	/** insert **/
    	Service service = new Service();
    	service.setUrl("testurl");
    	service.setName("HAIO");
    	serviceBo.save(service);
 
    	/** select **/
    	Service service2 = serviceBo.findByName("testurl");
    	System.out.println(service2);
 
    	/** update **/
    	service2.setName("HAIO-1");
    	serviceBo.update(service2);
 
    	/** delete **/
    	//serviceBo.delete(service2);
 
    	System.out.println("Done");
    }
}