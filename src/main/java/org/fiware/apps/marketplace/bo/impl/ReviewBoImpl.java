package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.fiware.apps.marketplace.bo.ReviewBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ReviewableEntity;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.ReviewValidator;
import org.fiware.apps.marketplace.security.auth.ReviewAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("reviewBo")
public class ReviewBoImpl implements ReviewBo {
	
	@Autowired private UserBo userBo;
	@Autowired private ReviewValidator reviewValidator;
	@Autowired private ReviewAuth reviewAuth;
	
	private double calculateReviewAverage(ReviewableEntity entity) {
		double sum = 0;
		List<Review> reviews = entity.getReviews();
		
		for (Review review: reviews) {
			sum += review.getScore();
		}
		
		// Cast is required. Otherwise, average will be an integer
		// If the reviews list is empty, average score is zero. This should be controlled. Otherwise,
		// the system will fail when the last review is deleted.
		return reviews.size() == 0 ? 0 : sum / (double) reviews.size();
		
	}
	
	private Review getReviewById(ReviewableEntity entity, int reviewId) throws ReviewNotFoundException {
		Review tmpReview = new Review();
		tmpReview.setId(reviewId);
		int bbddReviewIndex = entity.getReviews().indexOf(tmpReview);
		
		if (bbddReviewIndex >= 0) {
			return entity.getReviews().get(bbddReviewIndex);
		} else {
			throw new ReviewNotFoundException(String.format("Review %d not found in %s %s", reviewId, 
					entity.getClass().getSimpleName(), entity.toString()));
		}
		
	}
	
	@Override
	@Transactional
	public void createReview(ReviewableEntity entity, Review newReview) 
			throws NotAuthorizedException, ValidationException {
		
		// Check if the user is allowed to rate the offering. An exception will be
		// risen if the user is not allowed to do it.
		if (!reviewAuth.canCreate(entity, newReview)) {
			throw new NotAuthorizedException("review " + entity.getClass().getSimpleName(), 
					"An entity can only be reviewed once");
		}
		
		// Validate review (exception will be risen if the review is not valid)
		reviewValidator.validateReview(newReview);
				
		try {
			// Set review options
			Date currentDate = new Date();
			newReview.setDate(currentDate);
			newReview.setLastModificationDate(currentDate);
			newReview.setUser(userBo.getCurrentUser());
			newReview.setReviewableEntity(entity);

			// Insert review
			entity.getReviews().add(newReview);
			
			// Calculate average score
			entity.setAverageScore(calculateReviewAverage(entity));
			
			// The creating process is automatically done since this method is transactional
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	@Transactional
	public void updateReview(ReviewableEntity entity, int reviewId, Review updatedReview) 
			throws ReviewNotFoundException, NotAuthorizedException, ValidationException {
		
		Review bbddReview = getReviewById(entity, reviewId);
		
		// Check if the user is allowed to rate the offering. An exception will be
		// risen if the user is not allowed to do it.
		if (!reviewAuth.canUpdate(bbddReview)) {
			throw new NotAuthorizedException(String.format("update review %d in %s %s", reviewId,
					entity.getClass().getSimpleName(), entity.toString()));
		}
		
		// Validate review (exception will be risen if the review is not valid)
		reviewValidator.validateReview(updatedReview);

		// Update review
		bbddReview.setComment(updatedReview.getComment());
		bbddReview.setScore(updatedReview.getScore());
		bbddReview.setLastModificationDate(new Date());
		
		// Calculate average score
		entity.setAverageScore(calculateReviewAverage(entity));
		
		// The update process is automatically done since this method is transactional			
	}

	@Override
	@Transactional
	public List<Review> getReviews(ReviewableEntity entity)
			throws NotAuthorizedException {
		
		// Raise exception is the user is not allowed to list the reviews
		if (!reviewAuth.canList()) {
			throw new NotAuthorizedException("get reviews");
		}
			
		return entity.getReviews();
	}
	
	@Override
	@Transactional
	public Review getReview(ReviewableEntity entity, int reviewId) 
			throws NotAuthorizedException, ReviewNotFoundException {
		
		Review review = getReviewById(entity, reviewId);	
		
		// Raise exception is the user is not allowed to get the review
		if (!reviewAuth.canList()) {
			throw new NotAuthorizedException(String.format("get review %d from %s %s", reviewId, 
					entity.getClass().getSimpleName(), entity.toString()));
		} 
		
		return review;
	}

	@Override
	@Transactional
	public void deleteReview(ReviewableEntity entity, int reviewId)
			throws ReviewNotFoundException, NotAuthorizedException {
		
		Review review = getReviewById(entity, reviewId);
		
		// Raise exception is the user is not allowed to delete the review
		if (!reviewAuth.canDelete(review)) {
			throw new NotAuthorizedException(String.format("delete review %d from %s %s", reviewId, 
					entity.getClass().getSimpleName(), entity.toString()));
		}
			
		entity.getReviews().remove(review);
			
		// Calculate average score
		entity.setAverageScore(calculateReviewAverage(entity));

		// The deletion process is automatically done since this method is transactional		
	}

	@Override
	public Review getUserReview(ReviewableEntity entity)
			throws ReviewNotFoundException, NotAuthorizedException {
		
		try {
			
			Review userReview = null;
			User user = userBo.getCurrentUser();
			Iterator<Review> it = entity.getReviews().iterator();
			
			while (userReview == null && it.hasNext()) {
				Review review = it.next();
				userReview = review.getUser().equals(user) ? review : null;
			}
			
			// Throw exception if the user has not reviewed the offering
			if (userReview == null) {
				throw new ReviewNotFoundException("User " + user.getUserName() + " has not reviewed this entity");
			}
			
			return userReview;
			
		} catch (UserNotFoundException e) {
			throw new RuntimeException(e);
		}	
		
	}

}
