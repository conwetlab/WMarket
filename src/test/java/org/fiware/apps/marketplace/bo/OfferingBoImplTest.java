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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.impl.OfferingBoImpl;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.OfferingRating;
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
	@InjectMocks private OfferingBoImpl offeringBo;

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

		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";

		Offering offering = mock(Offering.class);

		// Configure mocks
		when(offeringDaoMock.findDescriptionByNameStoreAndDescription(storeName, descriptionName, 
				offeringName)).thenReturn(offering);
		when(offeringAuthMock.canBookmark(any(Offering.class))).thenReturn(false);

		// Call the function
		offeringBo.bookmark(storeName, descriptionName, offeringName);
	}

	private void testBookmarkNotFound(Exception e) throws Exception {

		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";

		// Configure mock
		doThrow(e).when(offeringDaoMock).findDescriptionByNameStoreAndDescription(storeName, descriptionName, 
				offeringName);

		// Call the function
		offeringBo.bookmark(storeName, descriptionName, offeringName);
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

		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";

		Offering offering = mock(Offering.class);
		User user = mock(User.class);
		@SuppressWarnings("unchecked")
		List<Offering> bookmarked = mock(List.class);

		// Configure mocks
		when(offeringDaoMock.findDescriptionByNameStoreAndDescription(storeName, descriptionName, 
				offeringName)).thenReturn(offering);
		when(offeringAuthMock.canBookmark(any(Offering.class))).thenReturn(true);
		when(userBoMock.getCurrentUser()).thenReturn(user);
		when(user.getBookmarks()).thenReturn(bookmarked);
		when(bookmarked.contains(offering)).thenReturn(offeringBookmarked);

		// Call the function
		offeringBo.bookmark(storeName, descriptionName, offeringName);

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
	//////////////////////////////////////// RATE /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testRateNotFound(Exception e) throws Exception {

		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";

		// Configure mock
		doThrow(e).when(offeringDaoMock).findDescriptionByNameStoreAndDescription(storeName, descriptionName, 
				offeringName);

		// Call the function
		OfferingRating rating = new OfferingRating();
		offeringBo.rate(storeName, descriptionName, offeringName, rating);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testRateStoreNotFoundException() throws Exception {
		testRateNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testRateDescriptionNotFoundException() throws Exception {
		testRateNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testRateOfferingNotFoundException() throws Exception {
		testRateNotFound(new OfferingNotFoundException(""));
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testRateNotAuthorized() throws Exception {

		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";
		Offering offering = mock(Offering.class);
		
		// Configure mock
		doReturn(false).when(offeringAuthMock).canRate(offering);
		doReturn(offering).when(offeringDaoMock).findDescriptionByNameStoreAndDescription(storeName, 
				descriptionName, offeringName);
		
		// Call the function
		OfferingRating rating = new OfferingRating();
		offeringBo.rate(storeName, descriptionName, offeringName, rating);
	}
	
	@Test(expected=ValidationException.class)
	public void testRateInvalid() throws Exception {
		
		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";
		Offering offering = mock(Offering.class);
		OfferingRating rating = new OfferingRating();
		
		// Configure mock
		ValidationException ex = new ValidationException("score", "invalid");
		doThrow(ex).when(ratingValidatorMock).validateRating(rating);
		doReturn(true).when(offeringAuthMock).canRate(offering);
		doReturn(offering).when(offeringDaoMock).findDescriptionByNameStoreAndDescription(storeName, 
				descriptionName, offeringName);
		
		// Call the function
		offeringBo.rate(storeName, descriptionName, offeringName, rating);
	}
	
	private void testRate(List<OfferingRating> ratings, int score, double expectedAverage) {
		
		try {
	
			String storeName = "store";
			String descriptionName = "description";
			String offeringName = "offering";
			
			// Configure offering
			Offering offering = mock(Offering.class);
			when(offering.getRatings()).thenReturn(ratings);
			
			OfferingRating rating = new OfferingRating();
			rating.setScore(score);
			
			// Configure mock
			doReturn(true).when(offeringAuthMock).canRate(offering);
			doReturn(offering).when(offeringDaoMock).findDescriptionByNameStoreAndDescription(storeName, 
					descriptionName, offeringName);
			
			// Call the function
			offeringBo.rate(storeName, descriptionName, offeringName, rating);
			
			// Verify that offering average score has been updated
			verify(offering).setAverageScore(expectedAverage);
		
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}

	}
	
	@Test
	public void testRateANonRatedOffering() {
		int score = 4;
		testRate(new ArrayList<OfferingRating>(), score, score);
	}
	
	@Test
	public void testRateARatedOffering() {
		
		// Create a list of previous ratings
		List<OfferingRating> ratings = new ArrayList<>();
		double sum = 0;
		
		for (int i = 1; i < 4; i++) {
			OfferingRating rating = new OfferingRating();
			rating.setScore(i);
			ratings.add(rating);
			
			sum += i;
		}
				
		int score = 1;
		double newAverage = (sum + score) / (ratings.size() + 1);
		
		testRate(ratings, score, newAverage);
	}
}
