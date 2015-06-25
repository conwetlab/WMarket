package org.fiware.apps.marketplace.controllers.rest.v2;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.DetailedReview;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.Reviews;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StoreReviewServiceTest {
	
	private UriInfo uri;

	@Mock private StoreBo storeBoMock;
	@InjectMocks private StoreReviewService reviewsService;
	
	private static final String PATH = "/api/v2/store/storeName/review";
	private static final String STORE_NAME = "store";
	private static final int REVIEW_ID = 9;
	
	@Before 
	public void setUp() throws UserNotFoundException {
		MockitoAnnotations.initMocks(this);

		uri = mock(UriInfo.class);
		when(uri.getPath()).thenReturn(PATH);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testCreateReviewException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {

			Review review = new Review();

			// Mocks
			doThrow(ex).when(storeBoMock).createReview(STORE_NAME, review);

			// Actual call
			Response res = reviewsService.createReview(uri, STORE_NAME, review);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}

	@Test
	public void testCreateReviewNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("rate offering");
		testCreateReviewException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
		
	@Test
	public void testCreateReviewStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testCreateReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
		
	@Test
	public void testCreateReviewValidationException() {
		String field = "score";
		ValidationException ex = new ValidationException(field, "invalid");
		testCreateReviewException(ex, 400, ErrorType.VALIDATION_ERROR, ex.getMessage(), field);
	}
	
	@Test
	public void testCreateReview() throws Exception {
		
		Review review = new Review();
		
		// Mocks
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(1, Review.class).setId(REVIEW_ID);
				return null;
			}
		}).when(storeBoMock).createReview(STORE_NAME, review);
		
		// Actual call
		Response res = reviewsService.createReview(uri, STORE_NAME, review);
		
		// Check response and headers
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(res.getHeaders().get("Location").get(0).toString()).isEqualTo(PATH + "/" + REVIEW_ID);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateReviewException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {

			Review review = new Review();

			// Mocks
			doThrow(ex).when(storeBoMock).updateReview(STORE_NAME, REVIEW_ID, review);

			// Actual call
			Response res = reviewsService.updateReview(STORE_NAME, REVIEW_ID, review);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}

	@Test
	public void testUpdateReviewNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("update offering review");
		testUpdateReviewException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testUpdateReviewStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testUpdateReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
		
	@Test
	public void testUpdateReviewReviewNotFound() {
		ReviewNotFoundException ex = new ReviewNotFoundException("review not found");
		testUpdateReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testUpdateReviewValidationException() {
		String field = "score";
		ValidationException ex = new ValidationException(field, "invalid");
		testUpdateReviewException(ex, 400, ErrorType.VALIDATION_ERROR, ex.getMessage(), field);
	}
	
	@Test
	public void testUpdateReview() throws Exception {
		
		Review review = new Review();
		
		// Actual call
		Response res = reviewsService.updateReview(STORE_NAME, REVIEW_ID, review);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		
		// Verify that storeBo has been properly called
		verify(storeBoMock).updateReview(STORE_NAME, REVIEW_ID, review);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET REVIEWS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetReviewsException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {
			
			int offset = 0;
			int max = 100;
			String orderBy = "id";
			boolean desc = false;
			
			// Mocks
			doThrow(ex).when(storeBoMock).getReviewsPage(STORE_NAME, offset, max, orderBy, desc);
			
			// Actual call
			Response res = reviewsService.getReviews(STORE_NAME, offset, max, orderBy, desc, false);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}
	
	@Test
	public void testGetReviewsNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("retrieve offering reviews");
		testGetReviewsException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testGetReviewsStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testGetReviewsException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetReviews() throws Exception {
		
		int offset = 0;
		int max = 100;
		String orderBy = "id";
		boolean desc = false;
				
		// Mock
		@SuppressWarnings("unchecked")
		List<Review> reviews = mock(List.class); 
		doReturn(reviews).when(storeBoMock).getReviewsPage(STORE_NAME, offset, max, orderBy, desc);
		
		// Actual call
		Response res = reviewsService.getReviews(STORE_NAME, offset, max, orderBy, desc, false);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Reviews) res.getEntity()).getReviews()).isEqualTo(reviews);
	}
	
	@Test
	public void testGetDetailedReviews() throws Exception {
		
		// Mocking
		User user = mock(User.class);
		Review review1 = mock(Review.class);
		when(review1.getUser()).thenReturn(user);
		Review review2 = mock(Review.class);
		when(review2.getUser()).thenReturn(user);
		
		List<Review> reviews = new ArrayList<>();
		reviews.add(review1);
		reviews.add(review2);
		
		int offset = 0;
		int max = 100;
		String orderBy = "id";
		boolean desc = false;
		
		doReturn(reviews).when(storeBoMock).getReviewsPage(STORE_NAME, offset, max, orderBy, desc);
		
		// Actual call
		Response res = reviewsService.getReviews(STORE_NAME, offset, max, orderBy, desc, true);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		
		List<Review> returnedReviews = ((Reviews) res.getEntity()).getReviews();
		assertThat(returnedReviews).isNotEmpty();
		
		// Reviews should be instances of DetailedReview
		for (Review review: reviews) {
			assertThat(review).isInstanceOf(DetailedReview.class);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET REVIEW //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetReviewException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {			
			// Mocks
			doThrow(ex).when(storeBoMock).getReview(STORE_NAME, REVIEW_ID);
			
			// Actual call
			Response res = reviewsService.getReview(STORE_NAME, REVIEW_ID, false);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}
	
	@Test
	public void testGetReviewNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("retrieve offering reviews");
		testGetReviewException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testGetReviewStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testGetReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetReviewReviewNotFound() {
		ReviewNotFoundException ex = new ReviewNotFoundException("review not found");
		testGetReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetReview() throws Exception {
		// Actual call
		Review review = mock(Review.class); 
		doReturn(review).when(storeBoMock).getReview(STORE_NAME, REVIEW_ID);
		Response res = reviewsService.getReview(STORE_NAME, REVIEW_ID, false);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Review) res.getEntity()).isEqualTo(review);
	}
	
	@Test
	public void testGetDetailedReview() throws Exception {
		// Actual call
		User user = mock(User.class);
		Review review = mock(Review.class); 
		when(review.getUser()).thenReturn(user);
		doReturn(review).when(storeBoMock).getReview(STORE_NAME, REVIEW_ID);
		Response res = reviewsService.getReview(STORE_NAME, REVIEW_ID, true);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		
		DetailedReview returnedReview = (DetailedReview) res.getEntity();
		assertThat(returnedReview).isInstanceOf(DetailedReview.class);
		assertThat(returnedReview.getId()).isEqualTo(review.getId());
		assertThat(returnedReview.getUser()).isEqualTo(review.getUser());
		assertThat(returnedReview.getScore()).isEqualTo(review.getScore());
		assertThat(returnedReview.getComment()).isEqualTo(review.getComment());
		assertThat(returnedReview.getLastModificationDate()).isEqualTo(review.getLastModificationDate());
		
		// Check that Password & mail has been set to null in order to avoid including them in the JSON/XML
		verify(user).setPassword(null);
		verify(user).setEmail(null);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteReviewException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {			
			// Mocks
			doThrow(ex).when(storeBoMock).deleteReview(STORE_NAME, REVIEW_ID);
			
			// Actual call
			Response res = reviewsService.deleteReview(STORE_NAME, REVIEW_ID);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}
	
	@Test
	public void testDeleteReviewNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("retrieve offering reviews");
		testDeleteReviewException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testDeleteReviewStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testDeleteReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testDeleteReviewReviewNotFound() {
		ReviewNotFoundException ex = new ReviewNotFoundException("review not found");
		testDeleteReviewException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testDeleteReview() throws Exception {
		// Actual call
		Response res = reviewsService.deleteReview(STORE_NAME, REVIEW_ID);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(204);
		
		// Verify that storeBoMock has been properly called
		verify(storeBoMock).deleteReview(STORE_NAME, REVIEW_ID);
	}

}
