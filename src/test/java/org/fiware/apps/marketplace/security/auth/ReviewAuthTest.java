package org.fiware.apps.marketplace.security.auth;

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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.ReviewableEntity;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ReviewAuthTest {
	
	@Mock private UserBo userBoMock;
	@InjectMocks private static ReviewAuth authHelper;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	private User createBasicUser(int id) {
		User user = new User();
		user.setId(id);
		return user;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// CREATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=UnsupportedOperationException.class) 
	public void canCreateReviewBasicMethodNotSupported() {
		authHelper.canCreate(new Review());
	}

	@Test
	public void canCreateReview() throws UserNotFoundException {
		Review review = new Review();
		
		// Mocking
		ReviewableEntity entity = mock(ReviewableEntity.class);
		List<Review> reviews = new ArrayList<>();
		when(entity.getReviews()).thenReturn(reviews);
		
		when(userBoMock.getCurrentUser()).thenReturn(new User());
		assertThat(authHelper.canCreate(entity, review)).isTrue();
	}

	@Test
	public void canNotCreateReviewUserNotFound() throws UserNotFoundException {
		ReviewableEntity entity = mock(ReviewableEntity.class);
		Review review = new Review();
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		assertThat(authHelper.canCreate(entity, review)).isFalse();
	}
	
	@Test
	public void canNotCreateReviewUserAlreadyReviewed() throws UserNotFoundException {
		
		Review review = new Review();
		User user = new User();
		user.setId(2);
		
		// The user has already reviewed the entity
		List<Review> reviews = new ArrayList<>();
		Review existingReview = new Review();
		existingReview.setUser(user);
		reviews.add(existingReview);
		
		// Set reviews
		ReviewableEntity entity = mock(ReviewableEntity.class);
		when(entity.getReviews()).thenReturn(reviews);
		
		when(userBoMock.getCurrentUser()).thenReturn(user);
		assertThat(authHelper.canCreate(entity, review)).isFalse();
		
	}


	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// UPDATE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void updateReview(User creator, User modifier, boolean canUpdate) {	
		try {
			when(userBoMock.getCurrentUser()).thenReturn(modifier);

			Review review = new Review();
			review.setUser(creator);

			assertThat(authHelper.canUpdate(review)).isEqualTo(canUpdate);
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testCanUpdateReviewSameUser() {
		User creator = createBasicUser(1);
		updateReview(creator, creator, true);
	}

	@Test
	public void testCanUpdateReview() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		updateReview(creator, updater, true);
	}
	
	@Test
	public void testCanNotUpdateReviewNotSameUser() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		updateReview(creator, updater, false);
	}
	
	@Test
	public void testCanNotUpdateReviewNotLoggedIn() {
		User creator = createBasicUser(1);
		updateReview(creator, null, false);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// GET /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void canGet() {
		Review review = mock(Review.class);
		assertThat(authHelper.canGet(review)).isTrue();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// LIST /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void canList() {
		assertThat(authHelper.canList()).isTrue();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// DELETE ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void deleteReview(User creator, User modifier, boolean canUpdate) {	
		try {
			when(userBoMock.getCurrentUser()).thenReturn(modifier);

			Review review = new Review();
			review.setUser(creator);

			assertThat(authHelper.canDelete(review)).isEqualTo(canUpdate);
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testCanDeleteReviewSameUser() {
		User creator = createBasicUser(1);
		deleteReview(creator, creator, true);
	}

	@Test
	public void testCanDeleteReview() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		deleteReview(creator, updater, true);
	}
	
	@Test
	public void testCanNotDeleteReviewNotSameUser() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		deleteReview(creator, updater, false);
	}
	
	@Test
	public void testCanNotDeleteReviewNotLoggedIn() {
		User creator = createBasicUser(1);
		deleteReview(creator, null, false);
	}

}
