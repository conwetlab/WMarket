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
@XmlRootElement(name = "ratingCategory")
public class RatingCategory {
	
	private Integer id;
	private RatingSystem ratingSystem;
	private String name;
	private String description;
	private Float weight;
	private List <RatingCategoryEntry> ratingCategoryEntries;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_CATEGORY_ID", unique = true, nullable = false)
	@XmlTransient
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlID
	@XmlAttribute 
	@Column(name = "RATING_CATEGORY_NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_SYTEM", nullable=false)
	public RatingSystem getRatingSystem() {
		return ratingSystem;
	}
	public void setRatingSystem(RatingSystem ratingSystem) {
		this.ratingSystem = ratingSystem;
	}
	
	@XmlElement
	@Column(name = "RATING_CATEGORY_DESCRIPTION", unique = false, nullable = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement
	@Column(name = "RATING_CATEGORY_WEIGHT", unique = false, nullable = false)
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	
	
	@XmlTransient
	@OneToMany(mappedBy="ratingCategory",  cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<RatingCategoryEntry> getRatingCategoryEntries() {
		return ratingCategoryEntries;
	}
	public void setRatingCategoryEntries(
			List<RatingCategoryEntry> ratingCategoryEntries) {
		this.ratingCategoryEntries = ratingCategoryEntries;
	}
	
}
