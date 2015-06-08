package org.fiware.apps.marketplace.bo.impl;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.RatingBoImpl;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.RatingNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.RateableEntity;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.validators.RatingValidator;
import org.fiware.apps.marketplace.security.auth.RatingAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RatingBoImplTest {
	
	@Mock private UserBo userBoMock;
	@Mock private RatingValidator ratingValidatorMock;
	@Mock private RatingAuth ratingAuthMock;
	@InjectMocks private RatingBoImpl ratingBo;
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.ratingBo = spy(this.ratingBo);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateRatingNotAuthorized() throws Exception {

		Rating rating = mock(Rating.class);
		
		// Configure mock
		doReturn(false).when(ratingAuthMock).canCreate(rating);
		
		// Call the function
		RateableEntity entity = mock(RateableEntity.class);
		ratingBo.createRating(entity, rating);
	}
	
	@Test(expected=ValidationException.class)
	public void testCreateRatingInvalid() throws Exception {
		
		Rating rating = new Rating();
		
		// Configure mock
		ValidationException ex = new ValidationException("score", "invalid");
		doThrow(ex).when(ratingValidatorMock).validateRating(rating);
		doReturn(true).when(ratingAuthMock).canCreate(rating);
		
		// Call the function
		RateableEntity entity = mock(RateableEntity.class);
		ratingBo.createRating(entity, rating);
	}
	
	
	private void testCreateRating(List<Rating> ratings, int score, double expectedAverage) {
		
		try {
				
			// Configure offering
			RateableEntity entity = mock(RateableEntity.class);
			when(entity.getRatings()).thenReturn(ratings);
			
			Rating rating = new Rating();
			rating.setScore(score);
			
			// Configure mock
			doReturn(true).when(ratingAuthMock).canCreate(rating);
			
			// Call the function
			ratingBo.createRating(entity, rating);
			
			// Verify that offering average score has been updated
			verify(entity).setAverageScore(expectedAverage);
		
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}

	}
	
	@Test
	public void testCreateRatingANonRatedOffering() {
		int score = 4;
		testCreateRating(new ArrayList<Rating>(), score, score);
	}
	
	@Test
	public void testCreteRatingARatedOffering() {
		
		// Create a list of previous ratings
		List<Rating> ratings = new ArrayList<>();
		double sum = 0;
		
		for (int i = 1; i < 4; i++) {
			Rating rating = new Rating();
			rating.setScore(i);
			ratings.add(rating);
			
			sum += i;
		}
				
		int score = 1;
		double newAverage = (sum + score) / (ratings.size() + 1);
		
		testCreateRating(ratings, score, newAverage);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=RatingNotFoundException.class)
	public void testUpdateRatingRatingNotFoundException() throws Exception {
		
		RateableEntity entity = mock(RateableEntity.class);
		when(entity.getRatings()).thenReturn(new ArrayList<Rating>());

		// Call the function
		Rating rating = new Rating();
		ratingBo.updateRating(entity, 9, rating);

	}
	
	private RateableEntity initializeUpdateRating(int ratingId, boolean canUpdate) throws Exception {
		
		// Initialize the offering and its ratings
		RateableEntity entity = mock(RateableEntity.class);
		List<Rating> ratings = new ArrayList<>();
		Rating storedRating = new Rating();
		storedRating.setId(ratingId);
		ratings.add(storedRating);
		when(entity.getRatings()).thenReturn(ratings);

		// Configure mock
		doReturn(canUpdate).when(ratingAuthMock).canUpdate(storedRating);
		
		return entity;

	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateRatingNotAuthorized() throws Exception {

		int ratingId = 9;
		
		// Initialize the offering and its ratings
		RateableEntity entity = initializeUpdateRating(ratingId, false);

		// Call the function
		Rating rating = new Rating();
		ratingBo.updateRating(entity, ratingId, rating);
		
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateRatingInvalid() throws Exception {
		
		int ratingId = 9;
		Rating rating = new Rating();
		
		// Configure mocks
		RateableEntity entity = initializeUpdateRating(ratingId, true);
		ValidationException ex = new ValidationException("score", "invalid");
		doThrow(ex).when(ratingValidatorMock).validateRating(rating);
		
		// Call the function
		ratingBo.updateRating(entity, ratingId, rating);
	}
	
	@Test
	public void testUpdateRating() throws Exception {
		
		int ratingId = 9;
		
		// Initialize the offering and its ratings
		RateableEntity entity = mock(RateableEntity.class);
		List<Rating> ratings = new ArrayList<>();
		
		// The rating to be updated
		Rating storedRating = spy(new Rating());
		storedRating.setId(ratingId);
		storedRating.setScore(3);
		ratings.add(storedRating);
		
		// Add additional ratings
		double sum = 0;
		for (int i = 1; i < 4; i++) {
			Rating additionalRating = new Rating();
			additionalRating.setScore(i);
			ratings.add(additionalRating);
			
			sum += i;
		}

		// Mocks
		doReturn(ratings).when(entity).getRatings();
		doReturn(true).when(ratingAuthMock).canUpdate(storedRating);

		// Call the function
		Rating updatedRating = new Rating();
		updatedRating.setScore(2);
		updatedRating.setComment("default comment");
		ratingBo.updateRating(entity, ratingId, updatedRating);
		
		// Verify that stored rating has been updated
		verify(storedRating).setScore(updatedRating.getScore());
		verify(storedRating).setComment(updatedRating.getComment());
		
		// Verify that the average is updated
		double average = (sum + updatedRating.getScore()) / ratings.size();
		verify(entity).setAverageScore(average);		
		
	}

}
