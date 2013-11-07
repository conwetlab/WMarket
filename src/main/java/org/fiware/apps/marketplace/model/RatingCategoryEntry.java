package org.fiware.apps.marketplace.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "ratingCategoryEntry")
public class RatingCategoryEntry {

	private Integer id;
	private Integer value;
	private RatingCategory ratingCategory;
	private Rating rating;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "RATING_CATEGORY_ENTRY_ID", unique = true, nullable = false)

	@XmlAttribute 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement
	@Column(name = "RATING_CATEGORY_ENTRY_VALUE", unique = false, nullable = false)
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}


	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_CATEGORY_ID", nullable=false)
	public RatingCategory getRatingCategory() {
		return ratingCategory;
	}
	public void setRatingCategory(RatingCategory ratingCategory) {
		this.ratingCategory = ratingCategory;
	}


	@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_ID", nullable=false)
	public Rating getRating() {
		return rating;
	}
	public void setRating(Rating rating) {
		this.rating = rating;
	}


}
