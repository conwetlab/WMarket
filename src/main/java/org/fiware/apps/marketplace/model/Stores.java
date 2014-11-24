package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stores")
public class Stores {
	
    private List<Store> stores = null;
    
    public Stores() {
    	this.setStores(new ArrayList<Store>());
    }
    
    public Stores(List<Store> stores) {
    	this.setStores(stores);
    }
 
    @XmlElement(name = "store")
    public List<Store> getStores() {
        return this.stores;
    }
 
    public void setStores(List<Store> stores) {
    	this.stores = stores;
    }
}
