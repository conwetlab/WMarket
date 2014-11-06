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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "ratingObjectCategory")
public class RatingObjectCategory {
	
	private Integer id;
	private String name;
	private List <RatingObject> ratingObjects;
	private List <RatingCategory> ratingCategorys;
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_OBJ_CAT_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
		
	@XmlID
	@XmlAttribute 
	@Column(name = "RATING_OBJECT_CATEGORY_NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
		
	@XmlTransient
	@OneToMany(mappedBy="ratingObjectCategory",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<RatingObject> getRatingObjects() {
		return ratingObjects;
	}

	public void setRatingObjects(List<RatingObject> ratingObjects) {
		this.ratingObjects = ratingObjects;
	}
	
	
	@XmlTransient
	@OneToMany(mappedBy="ratingObjectCategory",  cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<RatingCategory> getRatingCategorys() {
		return ratingCategorys;
	}
	
	public void setRatingCategorys(List<RatingCategory> ratingCategorys) {
		this.ratingCategorys = ratingCategorys;
	}
	
}
