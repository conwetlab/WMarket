package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "SERVICE_NAME", "STORE_ID" }) }) // each service name has to unique for a store context
@XmlRootElement(name = "resource")
public class Service {
	
	private Integer  id;
	private String url;
	private String name;
	private String Description;
	private Date registrationDate;
	private Store store;
	private Localuser lasteditor;	
	private Localuser creator;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "SERVICE_ID", unique = true, nullable = false)
	public Integer  getId() {
		return id;
	}
	
	public void setId(Integer  id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "SERVICE_NAME",  nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	@Column(name = "SERVICE_URL", unique = true, nullable = false)
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
			
	
	@XmlElement
	@Column(name = "SERVICE_DESC")
	public String getDescription() {
		return Description;
	}
	
	public void setDescription(String description) {
		Description = description;
	}
	
	@XmlElement
	@Column(name = "SERVICE_REG_DATE")
	public Date getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	

	@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "STORE_ID", nullable=false)
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}
	
	@XmlElement
	//@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "LOCALUSER_LAST_EDITOR_ID", nullable=false)
	public Localuser getLasteditor() {
		return lasteditor;
	}
	
	public void setLasteditor(Localuser lasteditor) {
		this.lasteditor = lasteditor;
	}
	
	@XmlElement
	//@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "LOCALUSER_CREATOR_ID", nullable=false)
	public Localuser getCreator() {
		return creator;
	}
	
	public void setCreator(Localuser creator) {
		this.creator = creator;
	}
	
}
