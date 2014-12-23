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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "ratingObject")
public class RatingObject implements Comparable<RatingObject> {

	private Integer id;
	String objectId;
	private Set<Rating> ratings;
	private RatingObjectCategory ratingObjectCategory;
	//private float average;

	public static final int MAX_RATING = 5;
	public static final int MIN_RATING = 1;	

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
	@Column(name = "RATING_OBJECT_OBJECT_ID", unique = false, nullable = false)
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@XmlTransient
	@OneToMany(mappedBy="ratingObject",  cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<Rating> getRatings() {
		return ratings;
	}
	
	public void setRatings(Set<Rating> ratings) {
		this.ratings = ratings;
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

	@XmlElement
	@Transient
	public float getAverage() {
		//FIXME: Average should be stored
		
		float result = (float) 0.0;
		int ratingCount= 0;
		for (Rating r : ratings){
			if(r.getOverallRating() >= MIN_RATING && r.getOverallRating() <= MAX_RATING){
				result += r.getOverallRating();  
				ratingCount++;
			}
		}

		return result/ratingCount;
	}
	
	/*public void setAverage(float average) {
		this.average = average;
	}*/
	
	@Override
	public int compareTo(RatingObject o) {
		if (getAverage() < o.getAverage()) 
			return 1; 
		else if (getAverage() == o.getAverage()) 
			return 0; 
		else
			return -1; 
	}
}
