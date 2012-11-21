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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "rating")
public class Rating {
		
	private Integer id;
	private String feedback;
	private Localuser user;
	private Boolean anonym = true;
	private List<RatingCategoryEntry> ratingCategoryEntries;
	private RatingObject ratingObject;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_ID", unique = true, nullable = false)
	@XmlID
	@XmlAttribute 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlElement
	@Column(name = "RATING_FEEDBACK", unique = false, nullable = true)
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "RATING_USER", nullable=true)
	public Localuser getUser() {
		return user;
	}
	public void setUser(Localuser user) {
		this.user = user;
	}
	
	@XmlElement
	@Column(name = "RATING_ANONYM", unique = false, nullable = false)
	public Boolean getAnonym() {
		return anonym;
	}
	public void setAnonym(Boolean anonym) {
		this.anonym = anonym;
	}
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_OBJECT_ID", nullable=false)
	public RatingObject getRatingObject() {
		return ratingObject;
	}
	public void setRatingObject(RatingObject ratingObject) {
		this.ratingObject = ratingObject;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="rating",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<RatingCategoryEntry> getRatingCategoryEntries() {
		return ratingCategoryEntries;
	}
	public void setRatingCategoryEntries(
			List<RatingCategoryEntry> ratingCategoryEntries) {
		this.ratingCategoryEntries = ratingCategoryEntries;
	}

}
