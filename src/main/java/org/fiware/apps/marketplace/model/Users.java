package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
public class Users {
	
    private List<Localuser> users = null;
    
    public Users() {
    	this.setUsers(new ArrayList<Localuser>());
    }
    
    public Users(List<Localuser> users) {
    	this.setUsers(users);
    }
 
    @XmlElement(name = "user")
    public List<Localuser> getUsers() {
        return this.users;
    }
 
    public void setUsers(List<Localuser> users) {
    	this.users = users;
    }
}
