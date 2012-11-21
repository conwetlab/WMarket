package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "ratingSystem")
public class RatingSystem {
	
	private Integer id;
	private String name;
	private String description;
	private List <RatingCategory> ratingCategories;
	private List <RatingObject> ratingObjects;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_SYSTEM_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
		
	@XmlID
	@XmlAttribute 
	@Column(name = "RATING_SYSTEM_NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	@Column(name = "RATING_SYSTEM_DESCRIPTION", unique = false, nullable = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="ratingSystem",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<RatingCategory> getRatingCategories() {
		return ratingCategories;
	}
	public void setRatingCategories(List<RatingCategory> ratingCategories) {
		this.ratingCategories = ratingCategories;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="ratingSystem",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<RatingObject> getRatingObjects() {
		return ratingObjects;
	}
	public void setRatingObjects(List<RatingObject> ratingObjects) {
		this.ratingObjects = ratingObjects;
	}
	
	
}
