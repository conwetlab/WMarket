package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fiware.apps.marketplace.utils.xmladapters.HiddenFieldsXMLAdapter;
import org.fiware.apps.marketplace.utils.xmladapters.PasswordXMLAdapter;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

@Entity
@Table(name = "Users")
@XmlRootElement(name = "user")
@IgnoreMediaTypes("application/*+json")
public class User {
	
	private Integer id;
	private String userName;
	private String displayName;
	private String password;
	private String email;
	private Date registrationDate;
	private String company;
		
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer  id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "user_name", unique = true, nullable = false)
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement
	@Column(name = "display_name")
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@XmlElement
	// Avoid returning the mail in the API
	@XmlJavaTypeAdapter(HiddenFieldsXMLAdapter.class)
	@Column(name = "email", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@XmlElement
	// Avoid returning the password in the API
	// Encrypt the password received through the API
	@XmlJavaTypeAdapter(PasswordXMLAdapter.class)
	@Column(name = "password", nullable = false)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		// The password is encoded using the user name as salt
		this.password = password;
	}
	
	@XmlElement
	@Column(name = "registration_date", nullable = false)
	public Date getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	@XmlElement
	@Column(name = "company")
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id;
	}
	
	@Override
	public boolean equals(Object obj) {
		User other = (User) obj;
		return other.id == this.id;
	}

}
