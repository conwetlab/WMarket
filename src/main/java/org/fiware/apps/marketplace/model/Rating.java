package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name = "rating")
public class Rating {
		
	private Integer id;
	private String name;	
	private String feedback;	
	private Set<RatingCategoryEntry> ratingCategoryEntries;	
	private RatingObject ratingObject;
	private Date date;
	//To be activated when the overallRating is enabled
	//private float overallRating;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_ID", unique = true, nullable = false)
	@XmlAttribute 
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlElement
	@Column(name = "RATING_NAME", unique = false, nullable = true )
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	@Column(name = "RATING_FEEDBACK", unique = false, nullable = true)
	public String getFeedback() {
		return feedback;
	}
	
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}	
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_OBJECT_ID", nullable=false)
	public RatingObject getRatingObject() {
		return ratingObject;
	}
	
	public void setRatingObject(RatingObject ratingObject) {
		this.ratingObject = ratingObject;
	}
	
	@XmlElementWrapper
	@XmlElement(name = "ratingCategoryEntry") 
	@OneToMany(mappedBy="rating",  cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<RatingCategoryEntry> getRatingCategoryEntries() {
		return ratingCategoryEntries;
	}
	
	public void setRatingCategoryEntries(
			Set<RatingCategoryEntry> ratingCategoryEntries) {
		this.ratingCategoryEntries = ratingCategoryEntries;
	}
	
	@XmlElement
	@Column(name = "RATING_DATE", nullable = false)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement
	@Transient
	public float getOverallRating() {
		//FIXME: The overall rating should be stored, not calculated
		float result = 0;
		if(ratingCategoryEntries != null){
			for (RatingCategoryEntry r : ratingCategoryEntries){
				result += r.getValue();
			}
			return result/ratingCategoryEntries.size();
		}
		return Float.NaN;
	}
	
	/* public void setOverallRating(float overallRating) {
		this.overallRating = overallRating;
	}*/

}
