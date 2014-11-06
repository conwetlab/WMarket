package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@XmlRootElement(name = "user")
public class Localuser {
	
	private Integer id;
	private String username;
	private String password;
	private String email;
	private Date registrationDate;
	private String company;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "LOCALUSER_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer  id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "LOCALUSER_USERNAME", unique = true, nullable = false)
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlTransient
	@Column(name = "LOCALUSER_PASSWORD", nullable = false)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlTransient
	@Column(name = "LOCALUSER_EMAIL", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@XmlElement
	@Column(name = "LOCALUSER_REGISTRATION_DATE", nullable = false)
	public Date getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	@XmlElement
	@Column(name = "LOCALUSER_COMPANY")
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}

}
