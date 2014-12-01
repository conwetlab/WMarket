package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
public class Users {
	
    private List<User> users = null;
    
    public Users() {
    	this.setUsers(new ArrayList<User>());
    }
    
    public Users(List<User> users) {
    	this.setUsers(users);
    }
 
    @XmlElement(name = "user")
    public List<User> getUsers() {
        return this.users;
    }
 
    public void setUsers(List<User> users) {
    	this.users = users;
    }
}
