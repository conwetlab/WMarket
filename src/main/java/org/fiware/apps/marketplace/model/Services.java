package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "services")
public class Services {
	
    private List<Service> services = null;
    
    public Services() {
    	this.setServices(new ArrayList<Service>());
    }
    
    public Services(List<Service> services) {
    	this.setServices(services);
    }
 
    @XmlElement(name = "service")
    @JsonProperty("services")
    public List<Service> getServices() {
        return this.services;
    }
 
    public void setServices(List<Service> services) {
    	this.services = services;
    }
}
