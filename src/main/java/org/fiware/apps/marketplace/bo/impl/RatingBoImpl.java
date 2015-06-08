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
import java.util.List;

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.RatingNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.RateableEntity;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.validators.RatingValidator;
import org.fiware.apps.marketplace.security.auth.RatingAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ratingBo")
public class RatingBoImpl implements RatingBo {
	
	@Autowired private UserBo userBo;
	@Autowired private RatingValidator ratingValidator;
	@Autowired private RatingAuth ratingAuth;
	
	private double calculateRatingAverage(RateableEntity entity) {
		int sum = 0;
		List<Rating> ratings = entity.getRatings();
		
		for (Rating rating: ratings) {
			sum += rating.getScore();
		}
		
		// Cast is required. Otherwise, average will be an integer
		return (double) sum / (double) ratings.size();
		
	}
	
	private Rating getRatingById(RateableEntity entity, int ratingId) throws RatingNotFoundException {
		Rating tmpRating = new Rating();
		tmpRating.setId(ratingId);
		int bbddRatingIndex = entity.getRatings().indexOf(tmpRating);
		
		if (bbddRatingIndex >= 0) {
			return entity.getRatings().get(bbddRatingIndex);
		} else {
			throw new RatingNotFoundException(String.format("Rating %d not found in %s %s", ratingId, 
					entity.getClass().getSimpleName(), entity.toString()));
		}
		
	}
	
	@Override
	@Transactional
	public void createRating(RateableEntity entity, Rating newRating) 
			throws NotAuthorizedException, ValidationException {
		
		// Check if the user is allowed to rate the offering. An exception will be
		// risen if the user is not allowed to do it.
		if (!ratingAuth.canCreate(newRating)) {
			// It is supposed not to happen
			throw new NotAuthorizedException("rate " + entity.getClass().getSimpleName());
		}
		
		// Validate rating (exception will be risen if the rating is not valid)
		ratingValidator.validateRating(newRating);
				
		try {
			// Set rating options
			Date currentDate = new Date();
			newRating.setDate(currentDate);
			newRating.setLastModificationDate(currentDate);
			newRating.setUser(userBo.getCurrentUser());
			newRating.setRatingEntity(entity);

			// Insert rating
			entity.getRatings().add(newRating);
			
			// Calculate average score
			entity.setAverageScore(calculateRatingAverage(entity));
			
			// The creating process is automatically done since this method is transactional
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	@Transactional
	public void updateRating(RateableEntity entity, int ratingId, Rating updatedRating) 
			throws RatingNotFoundException, NotAuthorizedException, ValidationException {
		
		Rating bbddRating = getRatingById(entity, ratingId);
		
		// Check if the user is allowed to rate the offering. An exception will be
		// risen if the user is not allowed to do it.
		if (!ratingAuth.canUpdate(bbddRating)) {
			throw new NotAuthorizedException(String.format("update rating %d in %s %s", ratingId,
					entity.getClass().getSimpleName(), entity.toString()));
		}
		
		// Validate rating (exception will be risen if the rating is not valid)
		ratingValidator.validateRating(updatedRating);

		// Update rating
		bbddRating.setComment(updatedRating.getComment());
		bbddRating.setScore(updatedRating.getScore());
		bbddRating.setLastModificationDate(new Date());
		
		// Calculate average score
		entity.setAverageScore(calculateRatingAverage(entity));
		
		// The update process is automatically done since this method is transactional			
	}

	@Override
	@Transactional
	public List<Rating> getRatings(RateableEntity entity)
			throws NotAuthorizedException {
		
		if (!ratingAuth.canList()) {
			throw new NotAuthorizedException("get ratings");
		} else {
			return entity.getRatings();
		}
	}
	
	@Override
	@Transactional
	public Rating getRating(RateableEntity entity, int ratingId) 
			throws NotAuthorizedException, RatingNotFoundException {
		
		Rating rating = getRatingById(entity, ratingId);	
		
		if (!ratingAuth.canList()) {
			throw new NotAuthorizedException(String.format("get rating %d from %s %s", ratingId, 
					entity.getClass().getSimpleName(), entity.toString()));
		} else {
			return rating;
		}
		
	}

}
