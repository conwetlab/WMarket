package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "Stores")
@XmlRootElement(name = "resource")
public class Store {
	
	private Integer  id;
	private String url;
	private String name;
	private String description;
	private Date registrationDate;
	private List <Service> services;
	private User lasteditor;	
	private User creator;
	
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
	@Column(name = "name", unique = true, nullable = false)
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
	
	@XmlTransient
	@OneToMany(mappedBy="store",  cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Service> getServices() {
		return services;
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
	}

}
