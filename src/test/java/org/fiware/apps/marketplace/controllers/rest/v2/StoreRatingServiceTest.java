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

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.RatingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.Ratings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StoreRatingServiceTest {
	
	private UriInfo uri;

	@Mock private StoreBo storeBoMock;
	@InjectMocks private StoreRatingService ratingService;
	
	private static final String PATH = "/api/v2/store/storeName/rating";
	private static final String STORE_NAME = "store";
	private static final int RATING_ID = 9;
	
	@Before 
	public void setUp() throws UserNotFoundException {
		MockitoAnnotations.initMocks(this);

		uri = mock(UriInfo.class);
		when(uri.getPath()).thenReturn(PATH);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testCreateRatingException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {

			Rating rating = new Rating();

			// Mocks
			doThrow(ex).when(storeBoMock).createRating(STORE_NAME, rating);

			// Actual call
			Response res = ratingService.createRating(uri, STORE_NAME, rating);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}

	@Test
	public void testCreateRatingNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("rate offering");
		testCreateRatingException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
		
	@Test
	public void testCreateRatingStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testCreateRatingException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
		
	@Test
	public void testCreateRatingValidationException() {
		String field = "score";
		ValidationException ex = new ValidationException(field, "invalid");
		testCreateRatingException(ex, 400, ErrorType.VALIDATION_ERROR, ex.getMessage(), field);
	}
	
	@Test
	public void testCreateRating() throws Exception {
		
		Rating rating = new Rating();
		
		// Mocks
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(1, Rating.class).setId(RATING_ID);
				return null;
			}
		}).when(storeBoMock).createRating(STORE_NAME, rating);
		
		// Actual call
		Response res = ratingService.createRating(uri, STORE_NAME, rating);
		
		// Check response and headers
		assertThat(res.getStatus()).isEqualTo(201);
		assertThat(res.getHeaders().get("Location").get(0).toString()).isEqualTo(PATH + "/" + RATING_ID);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateRatingException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {

			Rating rating = new Rating();

			// Mocks
			doThrow(ex).when(storeBoMock).updateRating(STORE_NAME, RATING_ID, rating);

			// Actual call
			Response res = ratingService.updateRating(STORE_NAME, RATING_ID, rating);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}

	@Test
	public void testUpdateRatingNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("update offering rating");
		testUpdateRatingException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testUpdateRatingStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testUpdateRatingException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
		
	@Test
	public void testUpdateRatingRatingNotFound() {
		RatingNotFoundException ex = new RatingNotFoundException("rating not found");
		testUpdateRatingException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testUpdateRatingValidationException() {
		String field = "score";
		ValidationException ex = new ValidationException(field, "invalid");
		testUpdateRatingException(ex, 400, ErrorType.VALIDATION_ERROR, ex.getMessage(), field);
	}
	
	@Test
	public void testUpdateRating() throws Exception {
		
		Rating rating = new Rating();
		
		// Actual call
		Response res = ratingService.updateRating(STORE_NAME, RATING_ID, rating);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		
		// Verify that storeBo has been properly called
		verify(storeBoMock).updateRating(STORE_NAME, RATING_ID, rating);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATINGS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetRatingsException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {
			// Mocks
			doThrow(ex).when(storeBoMock).getRatings(STORE_NAME);
			
			// Actual call
			Response res = ratingService.getRatings(STORE_NAME);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}
	
	@Test
	public void testGetRatingsNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("retrieve offering ratings");
		testGetRatingsException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testGetRatingsStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testGetRatingsException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetRatings() throws Exception {
				
		// Actual call
		@SuppressWarnings("unchecked")
		List<Rating> ratings = mock(List.class); 
		doReturn(ratings).when(storeBoMock).getRatings(STORE_NAME);
		Response res = ratingService.getRatings(STORE_NAME);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Ratings) res.getEntity()).getRatings()).isEqualTo(ratings);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATING /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetRatingException(Exception ex, int statusCode, ErrorType errorType, 
			String message, String field) {

		try {			
			// Mocks
			doThrow(ex).when(storeBoMock).getRating(STORE_NAME, RATING_ID);
			
			// Actual call
			Response res = ratingService.getRating(STORE_NAME, RATING_ID);
			GenericRestTestUtils.checkAPIError(res, statusCode, errorType, message, field);
			
		} catch (Exception e1) {
			fail("Exception not expected", e1);
		}

	}
	
	@Test
	public void testGetRatingNotAuthorized() {
		NotAuthorizedException ex = new NotAuthorizedException("retrieve offering ratings");
		testGetRatingException(ex, 403, ErrorType.FORBIDDEN, ex.getMessage(), null);
	}
	
	@Test
	public void testGetRatingStoreNotFound() {
		StoreNotFoundException ex = new StoreNotFoundException("store not found");
		testGetRatingException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetRatingRatingNotFound() {
		RatingNotFoundException ex = new RatingNotFoundException("rating not found");
		testGetRatingException(ex, 404, ErrorType.NOT_FOUND, ex.getMessage(), null);
	}
	
	@Test
	public void testGetRating() throws Exception {
		// Actual call
		Rating rating = mock(Rating.class); 
		doReturn(rating).when(storeBoMock).getRating(STORE_NAME, RATING_ID);
		Response res = ratingService.getRating(STORE_NAME, RATING_ID);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat((Rating) res.getEntity()).isEqualTo(rating);
	}

}
