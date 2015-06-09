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

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.OfferingBoImpl;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.RatingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.RatingValidator;
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
	@Mock private RatingValidator ratingValidatorMock;
	@Mock private RatingBo ratingBoMock;
	@InjectMocks private OfferingBoImpl offeringBo;
	
	private final static String STORE_NAME = "store";
	private final static String DESCRIPTION_NAME = "description";
	private final static String OFFERING_NAME = "offering";
	private final static int RATING_ID = 9;


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
	//////////////////////////////////// CREATE RATING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testCreateRatingNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		Rating rating = new Rating();
		offeringBo.createRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, rating);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testCreateRatingStoreNotFoundException() throws Exception {
		testCreateRatingNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testCreateRatingDescriptionNotFoundException() throws Exception {
		testCreateRatingNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testCreateRatingOfferingNotFoundException() throws Exception {
		testCreateRatingNotFound(new OfferingNotFoundException(""));
	}
	
	private void testCreateRatingException(Exception e) throws Exception {
		
		// Configure mock
		Offering offering = mock(Offering.class);
		Rating rating = mock(Rating.class);
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(e).when(ratingBoMock).createRating(offering, rating);

		// Call the function
		offeringBo.createRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, rating);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateRatingNotAuthorizedException() throws Exception {
		testCreateRatingException(new NotAuthorizedException("create rating"));
	}
	
	@Test(expected=ValidationException.class)
	public void testCreateRatingValidationException() throws Exception {
		testCreateRatingException(new ValidationException("score", "create rating"));
	}
	
	@Test
	public void testCreateRating() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Call the function
		Rating rating = new Rating();
		offeringBo.createRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, rating);
		
		// Verify that ratingBo has been called
		verify(ratingBoMock).createRating(offering, rating);
		
	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// UPDATE RATING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateRatingNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		Rating rating = new Rating();
		offeringBo.updateRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9, rating);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testUpdateRatingStoreNotFoundException() throws Exception {
		testUpdateRatingNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testUpdateRatingDescriptionNotFoundException() throws Exception {
		testUpdateRatingNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testUpdateRatingOfferingNotFoundException() throws Exception {
		testUpdateRatingNotFound(new OfferingNotFoundException(""));
	}
	
	private void testUpdateRatingException(Exception e) throws Exception {
		
		// Configure mock
		Offering offering = mock(Offering.class);
		Rating rating = mock(Rating.class);
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(e).when(ratingBoMock).updateRating(offering, RATING_ID, rating);

		// Call the function
		offeringBo.updateRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, RATING_ID, rating);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateRatingNotAuthorizedException() throws Exception {
		testUpdateRatingException(new NotAuthorizedException("update rating"));
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateRatingValidationException() throws Exception {
		testUpdateRatingException(new ValidationException("score", "update rating"));
	}
	
	@Test
	public void testUpdateRating() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Call the function
		int ratingId = 9;
		Rating rating = new Rating();
		offeringBo.updateRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, ratingId, rating);
		
		// Verify that ratingBo has been called
		verify(ratingBoMock).updateRating(offering, ratingId, rating);
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATINGS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetRatingsNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME);

		// Call the function
		offeringBo.getRatings(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testGetRatingsStoreNotFoundException() throws Exception {
		testGetRatingsNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testGetRatingsDescriptionNotFound() throws Exception {
		testGetRatingsNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testGetRatingsOfferingNotFoundException() throws Exception {
		testGetRatingsNotFound(new OfferingNotFoundException(""));
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetRatingsNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Configure mocks
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(new NotAuthorizedException("")).when(ratingBoMock).getRatings(offering);
		
		// Actual call
		offeringBo.getRatings(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME);
	}
	
	@Test
	public void testGetRatings() throws Exception {
		
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		@SuppressWarnings("unchecked")
		List<Rating> ratings = mock(List.class);
		doReturn(ratings).when(ratingBoMock).getRatings(offering);
		
		// Actual call
		assertThat(offeringBo.getRatings(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME)).isEqualTo(ratings);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATING //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetRatingNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME);

		// Call the function
		offeringBo.getRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testGetRatingStoreNotFoundException() throws Exception {
		testGetRatingNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testGetRatingDescriptionNotFound() throws Exception {
		testGetRatingNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testGetRatingOfferingNotFoundException() throws Exception {
		testGetRatingNotFound(new OfferingNotFoundException(""));
	}
	
	private void testGetRatingException(Exception ex) throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		doThrow(ex).when(ratingBoMock).getRating(offering, RATING_ID);
		
		// Actual call
		offeringBo.getRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, RATING_ID);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetRatingNotAuthorized() throws Exception {
		testGetRatingException(new NotAuthorizedException(""));
	}
	
	@Test(expected=RatingNotFoundException.class)
	public void testGetRatingRatingNotFound() throws Exception {
		testGetRatingException(new RatingNotFoundException(""));
	}
	
	@Test
	public void testGetRating() throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		Rating rating = mock(Rating.class);
		doReturn(rating).when(ratingBoMock).getRating(offering, RATING_ID);
		
		// Actual call
		assertThat(offeringBo.getRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, RATING_ID))
				.isEqualTo(rating);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// DELETE RATING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteRatingNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		// Call the function
		offeringBo.deleteRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 9);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testDeleteRatingStoreNotFoundException() throws Exception {
		testDeleteRatingNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testDeleteRatingDescriptionNotFound() throws Exception {
		testDeleteRatingNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testDeleteRatingOfferingNotFoundException() throws Exception {
		testDeleteRatingNotFound(new OfferingNotFoundException(""));
	}
	
	private void testDeleteRatingException(Exception ex) throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);

		doThrow(ex).when(ratingBoMock).deleteRating(offering, RATING_ID);
		
		// Actual call
		offeringBo.deleteRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, RATING_ID);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testDeleteRatingNotAuthorized() throws Exception {
		testDeleteRatingException(new NotAuthorizedException(""));
	}
	
	@Test(expected=RatingNotFoundException.class)
	public void testDeleteRatingRatingNotFound() throws Exception {
		testDeleteRatingException(new RatingNotFoundException(""));
	}
	
	@Test
	public void testDeleteRating() throws Exception {
				
		Offering offering = mock(Offering.class);

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		// Actual call
		offeringBo.deleteRating(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, RATING_ID);
		
		// Verify that ratingBo has been called
		verify(ratingBoMock).deleteRating(offering, RATING_ID);

	}
}
