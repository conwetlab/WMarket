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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@XmlRootElement(name = "resource")
public class Store {
	private Integer  id;
	private String url;
	private String name;
	private String Description;
	private Date registrationDate;
	private List <Service> services;
	private Localuser lasteditor;	
	private Localuser creator;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "STORE_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	public void setId(Integer  id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "STORE_NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	@Column(name = "STORE_URL", unique = true, nullable = false)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
	@XmlElement
	@Column(name = "STORE_DESC")
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	
	@XmlElement
	@Column(name = "STORE_DATE")
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
	
	@XmlElement
	@ManyToOne(optional = false)
	@JoinColumn(name = "LOCALUSER_LAST_EDITOR_ID", nullable=false)
	public Localuser getLasteditor() {
		return lasteditor;
	}
	public void setLasteditor(Localuser lasteditor) {
		this.lasteditor = lasteditor;
	}
	
	@XmlElement
	@ManyToOne(optional = false)
	@JoinColumn(name = "LOCALUSER_CREATOR_ID", nullable=false)
	public Localuser getCreator() {
		return creator;
	}
	public void setCreator(Localuser creator) {
		this.creator = creator;
	}

}
