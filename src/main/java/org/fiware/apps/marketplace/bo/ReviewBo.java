package org.fiware.apps.marketplace.bo;

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

import java.util.List;

import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ReviewableEntity;
import org.fiware.apps.marketplace.model.Review;


public interface ReviewBo {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a review for the given entity
	 * @param entity The entity that is going to be reviewed
	 * @param newReview The review itself
	 * @throws NotAuthorizedException If the user is not authorized to review the entity
	 * @throws ValidationException If the review is not valid
	 */
	public void createReview(ReviewableEntity entity, Review newReview) 
			throws NotAuthorizedException, ValidationException;
	
	/**
	 * Updates a review
	 * @param entity The entity whose review wants to be updated
	 * @param reviewId The ID of the review that is going to be updated
	 * @param updatedReview The updated review
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given entity
	 * @throws NotAuthorizedException If the current user is not authorized to update the review
	 * @throws ValidationException If the updated review is not valid
	 */
	public void updateReview(ReviewableEntity entity, int reviewId, Review updatedReview) 
			throws ReviewNotFoundException, NotAuthorizedException, ValidationException;
	
	/**
	 * Deletes a review
	 * @param entity The entity whose review wants to be deleted
	 * @param reviewId The ID of the review that is going to be deleted
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given entity
	 * @throws NotAuthorizedException If the current user is not authorized to delete the review
	 */
	public void deleteReview(ReviewableEntity entity, int reviewId) throws ReviewNotFoundException, 
			NotAuthorizedException;
	
	/**
	 * Returns a review
	 * @param entity The entity whose review want to be retrieved
	 * @param reviewId The ID of the review to be retrieved
	 * @return The review of the entity with the given ID
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the review
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given entity
	 */
 	public Review getReview(ReviewableEntity entity, int reviewId) throws NotAuthorizedException, 
 			ReviewNotFoundException;
 	
 	/**
 	 * Returns the review of the current user in a given entity
 	 * @param entity The entity that the current user has reviewed
 	 * @return The review of the current user for the given entity
 	 * @throws ReviewNotFoundException If the user has not reviewed the entity
 	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the review
 	 */
	public Review getUserReview(ReviewableEntity entity) throws ReviewNotFoundException, 
			NotAuthorizedException;

	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the reviews of a given entity
	 * @param entity The entity whose reviews want to be retrieved
	 * @return All the reviews of the given entity
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given entity
	 */
	public List<Review> getReviews(ReviewableEntity entity) throws NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the reviews of a given entity
	 * @param entity The entity whose reviews want to be retrieved
	 * @param offset The first review to be retrieved
	 * @param max The max number of reviews to be returned
	 * @param orderBy The field that will be used to order the returned reviews
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the reviews of a given entity
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given entity
	 */
	public List<Review> getReviewsPage(ReviewableEntity entity, int offset, int max, String orderBy, boolean desc)
			throws NotAuthorizedException;
	

}
