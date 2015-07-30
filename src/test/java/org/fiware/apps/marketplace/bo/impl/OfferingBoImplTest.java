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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fiware.apps.marketplace.bo.ReviewBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.OfferingBoImpl;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.dao.ViewedOfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.ViewedOffering;
import org.fiware.apps.marketplace.model.validators.ReviewValidator;
import org.fiware.apps.marketplace.security.auth.OfferingAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OfferingBoImplTest {

	@Mock private OfferingDao offeringDaoMock;
	@Mock private OfferingAuth offeringAuthMock;
	@Mock private UserBo userBoMock;
	@Mock private ReviewValidator reviewValidatorMock;
	@Mock private ReviewBo reviewBoMock;
	@Mock private ViewedOfferingDao viewedOfferingDaoMock;
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
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(false).when(offeringAuthMock).canCreate(offering);
		
		// Call the function
		offeringBo.save(offering);
		
		// Verify that DAO has NOT been called
		verify(offeringDaoMock, never()).save(offering);
	}
	
	@Test
	public void testCreate() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(true).when(offeringAuthMock).canCreate(offering);
		
		// Call the function
		offeringBo.save(offering);
		
		// Verify that DAO has been called
		verify(offeringDaoMock).save(offering);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(false).when(offeringAuthMock).canUpdate(offering);
		
		// Call the function
		offeringBo.update(offering);
		
		// Verify that DAO has NOT been called
		verify(offeringDaoMock, never()).update(offering);
	}
	
	@Test
	public void testUpdate() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(true).when(offeringAuthMock).canUpdate(offering);
		
		// Call the function
		offeringBo.update(offering);
		
		// Verify that DAO has been called
		verify(offeringDaoMock).update(offering);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testDeleteNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(false).when(offeringAuthMock).canDelete(offering);
		
		// Call the function
		offeringBo.delete(offering);
		
		// Verify that DAO has NOT been called
		verify(offeringDaoMock, never()).delete(offering);
	}
	
	@Test
	public void testDelete() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		// Mock
		doReturn(true).when(offeringAuthMock).canDelete(offering);
		
		// Call the function
		offeringBo.delete(offering);
		
		// Verify that DAO has been called
		verify(offeringDaoMock).delete(offering);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// FIND OFFERING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test(expected=OfferingNotFoundException.class)
	public void findOfferingByNameStoreAndDescriptionNotFound() throws Exception {
		
		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";
		doThrow(new OfferingNotFoundException("")).when(offeringDaoMock)
				.findByNameStoreAndDescription(storeName, descriptionName, offeringName);
		
		offeringBo.findOfferingByNameStoreAndDescription(storeName, descriptionName, offeringName);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void findOfferingByNameStoreAndDescriptionNotAuthorized() throws Exception {
		
		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";
		Offering offering = mock(Offering.class);
		
		doReturn(offering).when(offeringDaoMock)
				.findByNameStoreAndDescription(storeName, descriptionName, offeringName);
		doReturn(false).when(offeringAuthMock).canGet(offering);
		
		// Call the function
		offeringBo.findOfferingByNameStoreAndDescription(storeName, descriptionName, offeringName);		
	}
	
	public void findOfferingByNameStoreAndDescription(boolean viewed, Date lastVisit, boolean increaseViews, 
			boolean removeOldViewed) throws Exception {
		
		if (!viewed && !increaseViews) {
			throw new IllegalArgumentException("increaseViews cannot be false when viewd is false");
		}
		
		String storeName = "store";
		String descriptionName = "description";
		String offeringName = "offering";
		String userName = "user";
		int nViews = 10;
		User user = mock(User.class);
		Offering offering = mock(Offering.class);
		List<ViewedOffering> viewedOfferings = new ArrayList<>();
		ViewedOffering viewedOffering = null;
		
		if (viewed) {
			viewedOffering = new ViewedOffering();
			viewedOffering.setUser(user);
			viewedOffering.setOffering(offering);
			viewedOffering.setDate(lastVisit);
			
			viewedOfferings.add(viewedOffering);
		}
		
		if (removeOldViewed) {
			for (int i = 0; i < 10; i++) {
				ViewedOffering otherViewedOffering = new ViewedOffering();
				otherViewedOffering.setOffering(mock(Offering.class));
				viewedOfferings.add(otherViewedOffering);
			}
		}

		// Mocking
		doReturn(offering).when(offeringDaoMock)
				.findByNameStoreAndDescription(storeName, descriptionName, offeringName);
		doReturn(true).when(offeringAuthMock).canGet(offering);
		doReturn(userName).when(user).getUserName();
		doReturn(nViews).when(offering).getViews();
		doReturn(user).when(userBoMock).getCurrentUser();
		doReturn(viewedOfferings).when(viewedOfferingDaoMock).getUserViewedOfferings(userName);
		
		// Call the function and check the returned value
		Offering returnedOffering = offeringBo.findOfferingByNameStoreAndDescription(storeName, 
				descriptionName, offeringName);
		assertThat(returnedOffering).isSameAs(offering);
		
		// Assert that a new viewed offering has been created
		if (viewed) {
			verify(viewedOfferingDaoMock, never()).save(any(ViewedOffering.class));			
		} else {
			ArgumentCaptor<ViewedOffering> captor = ArgumentCaptor.forClass(ViewedOffering.class);
			verify(viewedOfferingDaoMock).save(captor.capture());
			viewedOffering = captor.getValue();
		}
		
		// Check that the number of views has been increased
		if (increaseViews) {
			verify(offering).setViews(nViews + 1);
		} else {
			verify(offering, never()).setViews(anyInt());
		}
		
		// Check viewed offering values
		assertThat(viewedOffering.getOffering()).isEqualTo(offering);
		assertThat(viewedOffering.getUser()).isEqualTo(user);
		assertThat(viewedOffering.getDate()).isCloseTo(new Date(), 1000);

		// Check that old viewed offerings have been remove (just in case)
		if (!removeOldViewed) {
			// Verify that no viewed offering has been removed
			verify(viewedOfferingDaoMock, never()).delete(any(ViewedOffering.class));
		} else {
			// Verify that old viewed offerings have been removed
			for (int i = 9; i < viewedOfferings.size(); i++) {
				verify(viewedOfferingDaoMock).delete(viewedOfferings.get(i));
			}
		}
	}
	
	@Test
	public void findOfferingByNameStoreAndDescriptionOfferingNotViewedNotRemove() throws Exception {
		findOfferingByNameStoreAndDescription(false, new Date(), true, false);
	}
	
	@Test
	public void findOfferingByNameStoreAndDescriptionOfferingViewedNotRemove() throws Exception {
		Date twelveHoursAgo = new Date(new Date().getTime() - (12 * 3600 * 1000) - 1);
		findOfferingByNameStoreAndDescription(true, twelveHoursAgo, false, false);
	}
	
	@Test
	public void findOfferingByNameStoreAndDescriptionOfferingViewedNotRemoveIncreaseViews() throws Exception {
		Date yesterday = new Date(new Date().getTime() - (24 * 3600 * 1000) - 1);
		findOfferingByNameStoreAndDescription(true, yesterday, true, false);
	}
	
	@Test
	public void findOfferingByNameStoreAndDescriptionOfferingNotViewedRemove() throws Exception {
		findOfferingByNameStoreAndDescription(false, new Date(), true, true);
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
	/////////////////////////////////// GET REVIEWS PAGE //////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testGetReviewsPageNotFound(Exception e) throws Exception {

		// Configure mock
		doThrow(e).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, DESCRIPTION_NAME, 
				OFFERING_NAME);

		// Call the function
		offeringBo.getReviewsPage(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 0, 100, "id", false);
	}

	@Test(expected=StoreNotFoundException.class)
	public void testGetReviewsPageStoreNotFoundException() throws Exception {
		testGetReviewsPageNotFound(new StoreNotFoundException(""));
	}

	@Test(expected=DescriptionNotFoundException.class)
	public void testGetReviewsPageDescriptionNotFound() throws Exception {
		testGetReviewsPageNotFound(new DescriptionNotFoundException(""));
	}

	@Test(expected=OfferingNotFoundException.class)
	public void testGetReviewsPageOfferingNotFoundException() throws Exception {
		testGetReviewsPageNotFound(new OfferingNotFoundException(""));
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetReviewsPageNotAuthorized() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		int offset = 0;
		int max = 100;
		String orderBy = "id";
		boolean desc = true;
		
		// Configure mocks
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		doThrow(new NotAuthorizedException("")).when(reviewBoMock).getReviewsPage(offering, offset, max, orderBy, desc);
		
		// Actual call
		offeringBo.getReviewsPage(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, offset, max, orderBy, desc);
	}
	
	@Test
	public void testGetReviewsPage() throws Exception {
		
		Offering offering = mock(Offering.class);
		
		int offset = 0;
		int max = 100;
		String orderBy = "id";
		boolean desc = true;

		// Configure mock
		doReturn(offering).when(offeringDaoMock).findByNameStoreAndDescription(STORE_NAME, 
				DESCRIPTION_NAME, OFFERING_NAME);
		
		@SuppressWarnings("unchecked")
		List<Review> reviews = mock(List.class);
		doReturn(reviews).when(reviewBoMock).getReviewsPage(offering, offset, max, orderBy, desc);
		
		// Actual call
		assertThat(offeringBo.getReviewsPage(STORE_NAME, DESCRIPTION_NAME, OFFERING_NAME, 
				offset, max, orderBy, desc)).isEqualTo(reviews);

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
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// LAST VIEWED OFFERINGS ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetLastViewedOfferingsPageNotAuthorized() throws Exception {
		
		// Mock
		doReturn(false).when(offeringAuthMock).canListLastViewed();
		
		// Call the function
		offeringBo.getLastViewedOfferingsPage(0, 100);
	}
	
	@Test
	public void testGetLastViewedOfferingsPage() throws Exception {
		
		String userName = "user";
		User user = mock(User.class);
		
		// List of viewed offerings
		List<ViewedOffering> viewedOfferings = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ViewedOffering vo = new ViewedOffering();
			vo.setOffering(mock(Offering.class));
		}
		
		// Mock
		doReturn(userName).when(user).getUserName();
		doReturn(user).when(userBoMock).getCurrentUser();
		doReturn(true).when(offeringAuthMock).canListLastViewed();
		doReturn(viewedOfferings).when(viewedOfferingDaoMock).getUserViewedOfferingsPage(
				anyString(), anyInt(), anyInt());
		
		// Call the function
		List<Offering> returnedOfferings = offeringBo.getLastViewedOfferingsPage(0, 100);
		
		// Check returned offerings
		for (int i = 0; i < viewedOfferings.size(); i++) {
			assertThat(returnedOfferings.get(i)).isSameAs(viewedOfferings.get(i).getOffering());
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// OFFERINGS VIEWED BY OTHERS//////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetOfferingsViewedByOtherUsersNotAuthorized() throws Exception {
		
		// Mock
		doReturn(false).when(offeringAuthMock).canListLastViewedByOthers();
		
		// Call the function
		offeringBo.getOfferingsViewedByOtherUsers(7);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetOfferingsViewedByOtherUsersInvalidMax() throws Exception {
		offeringBo.getOfferingsViewedByOtherUsers(21);
	}
	
	@Test
	public void testGetOfferingsViewedByOtherUsers() throws Exception {
		
		String userName = "user";
		User user = mock(User.class);
		
		// List of viewed offerings
		List<ViewedOffering> viewedOfferings = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ViewedOffering vo = new ViewedOffering();
			vo.setOffering(mock(Offering.class));
		}
		
		// Mock
		doReturn(userName).when(user).getUserName();
		doReturn(user).when(userBoMock).getCurrentUser();
		doReturn(true).when(offeringAuthMock).canListLastViewedByOthers();
		doReturn(viewedOfferings).when(viewedOfferingDaoMock).getOfferingsViewedByOtherUsers(anyString(), anyInt());
		
		// Call the function
		List<Offering> returnedOfferings = offeringBo.getOfferingsViewedByOtherUsers(7);
		
		// Check returned offerings
		for (int i = 0; i < viewedOfferings.size(); i++) {
			assertThat(returnedOfferings.get(i)).isSameAs(viewedOfferings.get(i).getOffering());
		}
	}
	
}
