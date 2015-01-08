package org.fiware.apps.marketplace.model;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
