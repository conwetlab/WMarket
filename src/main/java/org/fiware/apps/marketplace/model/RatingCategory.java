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
	private String name;
	private List <RatingCategoryEntry> ratingCategoryEntries;
	private RatingObjectCategory ratingObjectCategory;
	
	
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
	@Column(name = "RATING_CATEGORY_NAME", unique = false, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "RATING_OBJ_CAT_ID", nullable=false)
	public RatingObjectCategory getRatingObjectCategory() {
		return ratingObjectCategory;
	}
	public void setRatingObjectCategory(RatingObjectCategory ratingObjectCategory) {
		this.ratingObjectCategory = ratingObjectCategory;
	}

}
