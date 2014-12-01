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
@Table(name = "Services", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "store" }) }) // each service name has to unique for a store context
@XmlRootElement(name = "resource")
public class Service {
	
	private Integer id;
	private String url;
	private String name;
	private String description;
	private Date registrationDate;
	private Store store;
	private User lasteditor;	
	private User creator;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer  getId() {
		return id;
	}
	
	public void setId(Integer  id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	@Column(name = "url", unique = true, nullable = false)
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlElement
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "store", nullable=false)
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}
	
	@XmlElement
	@ManyToOne(optional = false)
	@JoinColumn(name = "creator", nullable=false)
	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	@XmlElement
	@ManyToOne(optional = false)
	@JoinColumn(name = "last_editor", nullable=false)
	public User getLasteditor() {
		return lasteditor;
	}
	
	public void setLasteditor(User lasteditor) {
		this.lasteditor = lasteditor;
	}
	
	@XmlElement
	@Column(name = "registration_date")
	public Date getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
}
