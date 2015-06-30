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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.ReviewBoImpl;
import org.fiware.apps.marketplace.dao.ReviewDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ReviewableEntity;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.ReviewValidator;
import org.fiware.apps.marketplace.security.auth.ReviewAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReviewBoImplTest {
	
	@Mock private ReviewDao reviewDaoMock;
	@Mock private UserBo userBoMock;
	@Mock private ReviewValidator reviewValidatorMock;
	@Mock private ReviewAuth reviewAuthMock;
	@InjectMocks private ReviewBoImpl reviewBo;
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.reviewBo = spy(this.reviewBo);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateReviewNotAuthorized() throws Exception {

		Review review = mock(Review.class);
		
		// Configure mock
		doReturn(false).when(reviewAuthMock).canCreate(review);
		
		// Call the function
		ReviewableEntity entity = mock(ReviewableEntity.class);
		reviewBo.createReview(entity, review);
	}
	
	@Test(expected=ValidationException.class)
	public void testCreateReviewInvalid() throws Exception {
		
		Review review = new Review();
		ReviewableEntity entity = mock(ReviewableEntity.class);
		
		// Configure mock
		ValidationException ex = new ValidationException("score", "invalid");
		doThrow(ex).when(reviewValidatorMock).validateReview(review);
		doReturn(true).when(reviewAuthMock).canCreate(entity, review);
		
		// Call the function
		reviewBo.createReview(entity, review);
	}
	
	
	private void testCreateReview(List<Review> reviews, int score, double expectedAverage) {
		
		try {
				
			// Configure offering
			ReviewableEntity entity = mock(ReviewableEntity.class);
			when(entity.getReviews()).thenReturn(reviews);
			
			Review review = new Review();
			review.setScore(score);
			
			// Configure mock
			doReturn(true).when(reviewAuthMock).canCreate(entity, review);
			
			// Call the function
			reviewBo.createReview(entity, review);
			
			// Verify that offering average score has been updated
			verify(entity).setAverageScore(expectedAverage);
		
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}

	}
	
	@Test
	public void testCreateReviewANonRatedOffering() {
		int score = 4;
		testCreateReview(new ArrayList<Review>(), score, score);
	}
	
	@Test
	public void testCreteReviewARatedOffering() {
		
		// Create a list of previous reviews
		List<Review> reviews = new ArrayList<>();
		double sum = 0;
		
		for (int i = 1; i < 4; i++) {
			Review review = new Review();
			review.setScore(i);
			reviews.add(review);
			
			sum += i;
		}
				
		int score = 1;
		// 1 + 1 + 2 + 3 = 7 || 7 / 4 = 1.75 (decimal numbers preferred)
		double newAverage = (sum + score) / (reviews.size() + 1);
		
		testCreateReview(reviews, score, newAverage);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=ReviewNotFoundException.class)
	public void testUpdateReviewReviewNotFoundException() throws Exception {
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		when(entity.getReviews()).thenReturn(new ArrayList<Review>());

		// Call the function
		Review review = new Review();
		reviewBo.updateReview(entity, 9, review);

	}
	
	private ReviewableEntity initializeUpdateReviews(Review review, boolean canUpdate, 
			List<Review> additionalReviews) throws Exception {
		
		// Initialize the offering and its reviews
		ReviewableEntity entity = mock(ReviewableEntity.class);
		List<Review> reviews = new ArrayList<>();
		reviews.add(review);
		reviews.addAll(additionalReviews);
		when(entity.getReviews()).thenReturn(reviews);

		// Configure mock
		doReturn(canUpdate).when(reviewAuthMock).canUpdate(review);
		
		return entity;

	}
	
	private ReviewableEntity initializeUpdateReview(int reviewId, boolean canUpdate) throws Exception {
		Review review = new Review();
		review.setId(reviewId);
		return initializeUpdateReviews(review, canUpdate, new ArrayList<Review>());
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateReviewNotAuthorized() throws Exception {

		int reviewId = 9;
		
		// Initialize the offering and its reviews
		ReviewableEntity entity = initializeUpdateReview(reviewId, false);

		// Call the function
		Review review = new Review();
		reviewBo.updateReview(entity, reviewId, review);
		
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateReviewsInvalid() throws Exception {
		
		int reviewId = 9;
		Review review = new Review();
		
		// Configure mocks
		ReviewableEntity entity = initializeUpdateReview(reviewId, true);
		ValidationException ex = new ValidationException("score", "invalid");
		doThrow(ex).when(reviewValidatorMock).validateReview(review);
		
		// Call the function
		reviewBo.updateReview(entity, reviewId, review);
	}
	
	@Test
	public void testUpdateReview() throws Exception {
		
		int reviewId = 9;
		
		// The review to be updated
		Review storedReview = spy(new Review());
		storedReview.setId(reviewId);
		storedReview.setScore(3);

		// Additional reviews
		List<Review> additionalReviews = new ArrayList<>();
		double sum = 0;
		for (int i = 1; i < 4; i++) {
			Review additionalReview = new Review();
			additionalReview.setScore(i);
			additionalReviews.add(additionalReview);
			
			sum += i;
		}
		
		// Initialize
		ReviewableEntity entity = initializeUpdateReviews(storedReview, true, additionalReviews);
		
		// Call the function
		Review updatedReview = new Review();
		updatedReview.setScore(5);
		updatedReview.setComment("default comment");
		reviewBo.updateReview(entity, reviewId, updatedReview);
		
		// Verify that stored review has been updated
		verify(storedReview).setScore(updatedReview.getScore());
		verify(storedReview).setComment(updatedReview.getComment());
		
		// Verify that the average is updated
		// 1 + 2 + 3 + 5 = 11 || 11 / 4 = 2.75 (decimal numbers preferred)
		double average = (sum + updatedReview.getScore()) / (additionalReviews.size() + 1);
		verify(entity).setAverageScore(average);		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// LIST /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewsNotAuthorized() throws Exception {
		// Mocking
		doReturn(false).when(reviewAuthMock).canList();
		
		// Actual call
		ReviewableEntity entity = mock(ReviewableEntity.class);
		reviewBo.getReviews(entity);
	}
	
	@Test
	public void testGetReviews() throws Exception {
		// Mocking
		doReturn(true).when(reviewAuthMock).canList();
		
		// Actual call
		@SuppressWarnings("unchecked")
		List<Review> reviews = mock(List.class);
		ReviewableEntity entity = mock(ReviewableEntity.class);
		doReturn(reviews).when(entity).getReviews(); 
		assertThat(reviewBo.getReviews(entity)).isEqualTo(reviews);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// LIST PAGE //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewsPageNotAuthorized() throws Exception {
		// Mocking
		doReturn(false).when(reviewAuthMock).canList();
		
		// Actual call
		ReviewableEntity entity = mock(ReviewableEntity.class);
		reviewBo.getReviewsPage(entity, 0, 100, "id", false);
	}
	
	@Test
	public void testGetReviewsPage() throws Exception {
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		int offset = 8;
		int max = 2;
		String orderBy = "abc";
		boolean desc = false;
		
		// Mocking
		@SuppressWarnings("unchecked")
		List<Review> reviews = mock(List.class);
		doReturn(reviews).when(reviewDaoMock).getReviewsPage(entity, offset, max, orderBy, desc);
		doReturn(true).when(reviewAuthMock).canList();
		
		// Actual call
		assertThat(reviewBo.getReviewsPage(entity, offset, max, orderBy, desc)).isEqualTo(reviews);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// GET /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewNotAuthorized() throws Exception {
		
		int reviewId = 9;
		
		// Mocking
		doReturn(false).when(reviewAuthMock).canList();
				
		Review review = new Review();
		review.setId(reviewId);
		
		List<Review> reviews = new ArrayList<>();
		reviews.add(review);
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		doReturn(reviews).when(entity).getReviews();
		
		// Actual call
		reviewBo.getReview(entity, reviewId);
	}
	
	@Test(expected=ReviewNotFoundException.class)
	public void testGetReviewReviewNotFound() throws Exception {
		
		// Mocking
		doReturn(true).when(reviewAuthMock).canList();
				
		ReviewableEntity entity = mock(ReviewableEntity.class);
		List<Review> reviews = new ArrayList<>();
		doReturn(reviews).when(entity).getReviews();
		
		// Actual call
		reviewBo.getReview(entity, 9);
	}
	
	@Test
	public void testGetReview() throws Exception {
		int reviewId = 9;
		
		// Mocking
		doReturn(true).when(reviewAuthMock).canList();
				
		Review review = new Review();
		review.setId(reviewId);
		
		List<Review> reviews = new ArrayList<>();
		reviews.add(review);
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		doReturn(reviews).when(entity).getReviews();
		
		// Actual call
		assertThat(reviewBo.getReview(entity, reviewId)).isEqualTo(review);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// DELETE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test(expected=ReviewNotFoundException.class)
	public void testDeleteReviewReviewNotFoundException() throws Exception {
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		when(entity.getReviews()).thenReturn(new ArrayList<Review>());

		// Call the function
		reviewBo.deleteReview(entity, 9);

	}
	
	private ReviewableEntity initializeDeleteReviews(Review review, boolean canDelete, 
			List<Review> additionalReviews) throws Exception {
		
		// Initialize the offering and its reviews
		ReviewableEntity entity = mock(ReviewableEntity.class);
		List<Review> reviews = new ArrayList<>();
		reviews.add(review);
		reviews.addAll(additionalReviews);
		when(entity.getReviews()).thenReturn(reviews);

		// Configure mock
		doReturn(canDelete).when(reviewAuthMock).canDelete(review);
		
		return entity;

	}
	
	private ReviewableEntity initializeDeleteReviews(int reviewId, boolean canDelete) throws Exception {
		Review storedReview = new Review();
		storedReview.setId(reviewId);
		return initializeDeleteReviews(storedReview, canDelete, new ArrayList<Review>());
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testDeleteReviewNotAuthorized() throws Exception {

		int reviewId = 9;
		
		// Initialize the offering and its reviews
		ReviewableEntity entity = initializeDeleteReviews(reviewId, false);

		// Call the function
		reviewBo.deleteReview(entity, reviewId);
		
	}
	
	@Test
	public void testDeleteLastReview() throws Exception {
		
		int reviewId = 9;
		
		// Initialize the offering and its reviews
		ReviewableEntity entity = initializeDeleteReviews(reviewId, true);

		// Call the function
		reviewBo.deleteReview(entity, reviewId);
		
		// Verify that the average score is zero
		verify(entity).setAverageScore(0);
		
		// Verify that the review has been deleted from the list
		List<Review> reviews = entity.getReviews();
		assertThat(reviews).isEmpty();
	}
	
	@Test
	public void testDeleteReview() throws Exception {
		
		int reviewId = 9;
		
		// The review to be deleted
		Review storedReview = new Review();
		storedReview.setId(reviewId);
		storedReview.setScore(3);

		// Additional reviews
		double sum = 0;
		List<Review> additionalReviews = new ArrayList<>();
		for (int i = 1; i < 5; i++) {
			Review additionalReview = new Review();
			additionalReview.setScore(i);
			additionalReviews.add(additionalReview);
			
			sum += i;
		}
		
		// Initialize mocks
		ReviewableEntity entity = initializeDeleteReviews(storedReview, true, additionalReviews);

		// Call the function
		reviewBo.deleteReview(entity, reviewId);
		
		// Verify that stored review has been deleted
		List<Review> reviews = entity.getReviews();
		assertThat(reviews).doesNotContain(storedReview);
		
		// Verify that the average is updated. 
		// reviews does not contains the deleted reviews at this point.
		// 1 + 2 + 3 + 4 = 10 || 10 / 4 = 2.5 (decimal numbers preferred)
		double average = sum / (double) additionalReviews.size();
		verify(entity).setAverageScore(average);		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// GET USER REVIEW IN ENTITY //////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void initReviewEntity(ReviewableEntity entity, User currentUser, Review currentUserReview) {
		
		User reviewer = new User();
		reviewer.setId(currentUser.getId() + 1);
		
		// List of reviews
		List<Review> reviews = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Review review = new Review();
			review.setUser(reviewer);
			reviews.add(review);
		}
		
		// Add one review created by the current user
		if (currentUserReview != null) {
			reviews.add(currentUserReview);
		}
		
		// Mocking
		try {
			when(userBoMock.getCurrentUser()).thenReturn(currentUser);
			when(entity.getReviews()).thenReturn(reviews);
		} catch (Exception e) {
			fail("Exception not expected", e);
		}

	}
	
	@Test(expected=ReviewNotFoundException.class)
	public void testUserHasNotReviewedTheOffering() throws Exception {
		
		User user = new User();
		user.setId(0);
		ReviewableEntity entity = mock(ReviewableEntity.class);
		initReviewEntity(entity, user, null);
		
		// Call the function
		reviewBo.getUserReview(entity);
	}
	
	@Test
	public void testUserHasReviewedTheOffering() throws Exception {
		
		User user = new User();
		user.setId(0);
		
		Review review = new Review();
		review.setUser(user);
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		initReviewEntity(entity, user, review);
		
		// Call the function
		assertThat(reviewBo.getUserReview(entity)).isEqualTo(review);
	}
	
	@Test(expected=RuntimeException.class)
	public void testRuntimeException() throws Exception {
		
		ReviewableEntity entity = mock(ReviewableEntity.class);
		
		// Mock
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		
		// Call the function
		reviewBo.getUserReview(entity);
	}
}
