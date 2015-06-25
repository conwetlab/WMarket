package org.fiware.apps.marketplace.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Bean that can be used to return a more detailed review. Detailed reviews
 * contains all the information about the user (display name, company,...) while
 * normal reviews only contains the user name 
 * @author aitor
 *
 */
public class DetailedReview extends Review {
	
	public DetailedReview(Review review) {
		
		this.setId(review.getId());
		this.setScore(review.getScore());
		this.setComment(review.getComment());
		this.setReviewableEntity(this.getReviewableEntity());
		this.setLastModificationDate(review.getLastModificationDate());
		this.setPublicationDate(review.getPublicationDate());
		this.setUser(review.getUser());
		
		// Set user password & mail to null in order to avoid returning these values
		// in the JSON or the XML. Otherwise, these fields will be included but
		// their value will be null
		this.getUser().setPassword(null);
		this.getUser().setEmail(null);
	}
	
	@XmlTransient
	@Override
	public User getUser() {
		return super.getUser();
	}
	
	@XmlElement(name="user")
	public User getDetailedUser() {
		return super.getUser();
	}

}