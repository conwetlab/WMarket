package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "ratingObject")
public class RatingObject {
	
	private Integer id;
	String objectId;
	private List<Rating> ratings;
	private RatingSystem ratingSystem;
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_OBJECT_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "RATING_OBJECT_OBJECT_ID", unique = true, nullable = false)
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="ratingObject",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<Rating> getRatings() {
		return ratings;
	}
	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}
	
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_SYTEM", nullable=false)
	public RatingSystem getRatingSystem() {
		return ratingSystem;
	}
	public void setRatingSystem(RatingSystem ratingSystem) {
		this.ratingSystem = ratingSystem;
	}
}
