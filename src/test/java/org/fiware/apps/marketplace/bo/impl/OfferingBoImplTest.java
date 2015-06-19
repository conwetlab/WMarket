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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.fiware.apps.marketplace.bo.ReviewBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.OfferingBoImpl;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.ReviewValidator;
import org.fiware.apps.marketplace.security.auth.OfferingAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OfferingBoImplTest {

	// TODO: Add tests for other methods

	@Mock private OfferingDao offeringDaoMock;
	@Mock private OfferingAuth offeringAuthMock;
	@Mock private UserBo userBoMock;
	@Mock private ReviewValidator reviewValidatorMock;
	@Mock private ReviewBo reviewBoMock;
	@InjectMocks private OfferingBoImpl offeringBo;
	
	private final static String STORE_NAME = "store";
	private final static String DESCRIPTION_NAME = "description";
	private final static String OFFERING_NAME = "offering";
	private final static int REVIEW_ID = 9;


	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.offeringBo = spy(this.offeringBo);
	}
		
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// BOOKMARK ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test(expected=NotAuthorizedException.class)
	public void testBookmarkNotAuthorized() throws Exception {

		Offering offering = mock(Offering.class);

		// Configure mocks
		when(offeringDaoMock.findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME)).thenReturn(offering);
		when(offeringAuthMock.canBookmark(any(Offering.class))).thenReturn(false);

		// Call the function
		offeringBo.bookmark(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}

	private void testBookmarkNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		offeringBo.bookmark(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testBookmarkStoreNotFoundException() throws Exception {
		testBookmarkNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testBookmarkDescriptionNotFoundException() throws Exception {
		testBookmarkNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testBookmarkOfferingNotFoundException() throws Exception {
		testBookmarkNotFound(new OfferingNotFoundException(""));
	}

	private void testChangeBookmarkState(boolean offeringBookmarked) throws Exception {

		Offering offering = mock(Offering.class);
		User user = mock(User.class);
		@SuppressWarnings("unchecked")
		List<Offering> bookmarked = mock(List.class);

		// Configure mocks
		when(offeringDaoMock.findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME)).thenReturn(offering);
		when(offeringAuthMock.canBookmark(any(Offering.class))).thenReturn(true);
		when(userBoMock.getCurrentUser()).thenReturn(user);
		when(user.getBookmarks()).thenReturn(bookmarked);
		when(bookmarked.contains(offering)).thenReturn(offeringBookmarked);

		// Call the function
		offeringBo.bookmark(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);

		if (!offeringBookmarked) {
			// Check that the offering has been included in the list of bookmarked offerings
			verify(bookmarked).add(offering);
		} else {
			// Check that the offering has been removed from the list of bookmarked offerings
			verify(bookmarked).remove(offering);
		}
	}
	
	@Test
	public void testBookmark() throws Exception {
		testChangeBookmarkState(false);
	}
	
	@Test
	public void testUnbookmark() throws Exception {
		testChangeBookmarkState(true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// CREATE REVIEW ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testCreateReviewNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		Review review = new Review();
		offeringBo.createReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, review);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testCreateReviewStoreNotFoundException() throws Exception {
		testCreateReviewNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testCreateReviewDescriptionNotFoundException() throws Exception {
		testCreateReviewNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testCreateReviewOfferingNotFoundException() throws Exception {
		testCreateReviewNotFound(new OfferingNotFoundException(""));
	}
	
	private void testCreateReviewException(Exception e) throws Exception {
		
		// Configure mock
		Offering offering = mock(Offering.class);
		Review review = mock(Review.class);
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(e).when(reviewBoMock).createReview(offering, review);

		// Call the function
		offeringBo.createReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, review);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateReviewNotAuthorizedException() throws Exception {
		testCreateReviewException(new NotAuthorizedException("create review"));
	}
	
	@Test(expected=ValidationException.class)
	public void testCreateReviewValidationException() throws Exception {
		testCreateReviewException(new ValidationException("score", "create review"));
	}
	
	@Test
	public void testCreateReview() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Call the function
		Review review = new Review();
		offeringBo.createReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, review);
		
		// Verify that reviewBo has been called
		verify(reviewBoMock).createReview(offering, review);
		
	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// UPDATE REVIEW ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateReviewNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		Review review = new Review();
		offeringBo.updateReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9, review);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testUpdateReviewStoreNotFoundException() throws Exception {
		testUpdateReviewNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testUpdateReviewDescriptionNotFoundException() throws Exception {
		testUpdateReviewNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testUpdateReviewOfferingNotFoundException() throws Exception {
		testUpdateReviewNotFound(new OfferingNotFoundException(""));
	}
	
	private void testUpdateReviewException(Exception e) throws Exception {
		
		// Configure mock
		Offering offering = mock(Offering.class);
		Review review = mock(Review.class);
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(e).when(reviewBoMock).updateReview(offering, REVIEW_ID, review);

		// Call the function
		offeringBo.updateReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, REVIEW_ID, review);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateReviewNotAuthorizedException() throws Exception {
		testUpdateReviewException(new NotAuthorizedException("update review"));
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateReviewValidationException() throws Exception {
		testUpdateReviewException(new ValidationException("score", "update review"));
	}
	
	@Test
	public void testUpdateReview() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Call the function
		int reviewId = 9;
		Review review = new Review();
		offeringBo.updateReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, reviewId, review);
		
		// Verify that reviewBo has been called
		verify(reviewBoMock).updateReview(offering, reviewId, review);
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET REVIEWS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetReviewsNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME);

		// Call the function
		offeringBo.getReviews(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testGetReviewsStoreNotFoundException() throws Exception {
		testGetReviewsNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testGetReviewsDescriptionNotFound() throws Exception {
		testGetReviewsNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testGetReviewsOfferingNotFoundException() throws Exception {
		testGetReviewsNotFound(new OfferingNotFoundException(""));
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewsNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Configure mocks
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(new NotAuthorizedException("")).when(reviewBoMock).getReviews(offering);
		
		// Actual call
		offeringBo.getReviews(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}
	
	@Test
	public void testGetReviews() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		@SuppressWarnings("unchecked")
		List<Review> reviews = mock(List.class);
		doReturn(reviews).when(reviewBoMock).getReviews(offering);
		
		// Actual call
		assertThat(offeringBo.getReviews(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME)).isEqualTo(reviews);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET REVIEW //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetReviewNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME);

		// Call the function
		offeringBo.getReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testGetReviewStoreNotFoundException() throws Exception {
		testGetReviewNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testGetReviewDescriptionNotFound() throws Exception {
		testGetReviewNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testGetReviewOfferingNotFoundException() throws Exception {
		testGetReviewNotFound(new OfferingNotFoundException(""));
	}
	
	private void testGetReviewException(Exception ex) throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		doThrow(ex).when(reviewBoMock).getReview(offering, REVIEW_ID);
		
		// Actual call
		offeringBo.getReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, REVIEW_ID);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewNotAuthorized() throws Exception {
		testGetReviewException(new NotAuthorizedException(""));
	}
	
	@Test(expected=ReviewNotFoundException.class)
	public void testGetReviewReviewNotFound() throws Exception {
		testGetReviewException(new ReviewNotFoundException(""));
	}
	
	@Test
	public void testGetReview() throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		Review review = mock(Review.class);
		doReturn(review).when(reviewBoMock).getReview(offering, REVIEW_ID);
		
		// Actual call
		assertThat(offeringBo.getReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, REVIEW_ID))
				.isEqualTo(review);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// DELETE REVIEW ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteReviewNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		offeringBo.deleteReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testDeleteReviewStoreNotFoundException() throws Exception {
		testDeleteReviewNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testDeleteReviewDescriptionNotFound() throws Exception {
		testDeleteReviewNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testDeleteReviewOfferingNotFoundException() throws Exception {
		testDeleteReviewNotFound(new OfferingNotFoundException(""));
	}
	
	private void testDeleteReviewException(Exception ex) throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		doThrow(ex).when(reviewBoMock).deleteReview(offering, REVIEW_ID);
		
		// Actual call
		offeringBo.deleteReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, REVIEW_ID);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testDeleteReviewNotAuthorized() throws Exception {
		testDeleteReviewException(new NotAuthorizedException(""));
	}
	
	@Test(expected=ReviewNotFoundException.class)
	public void testDeleteReviewReviewNotFound() throws Exception {
		testDeleteReviewException(new ReviewNotFoundException(""));
	}
	
	@Test
	public void testDeleteReview() throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Actual call
		offeringBo.deleteReview(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, REVIEW_ID);
		
		// Verify that reviewBo has been called
		verify(reviewBoMock).deleteReview(offering, REVIEW_ID);

	}
}
