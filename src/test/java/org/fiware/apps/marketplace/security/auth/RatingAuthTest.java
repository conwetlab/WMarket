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
import static org.mockito.Mockito.when;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RatingAuthTest {
	
	@Mock private UserBo userBoMock;
	@InjectMocks private static RatingAuth authHelper;
	
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
	////////////////////////////////// TEST CREATE RATING /////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void canCreateRating() throws UserNotFoundException {
		Rating rating = new Rating();
		when(userBoMock.getCurrentUser()).thenReturn(new User());
		assertThat(authHelper.canCreate(rating)).isTrue();
	}

	@Test
	public void canNotCreateRating() throws UserNotFoundException {
		Rating rating = new Rating();
		doThrow(new UserNotFoundException("")).when(userBoMock).getCurrentUser();
		assertThat(authHelper.canCreate(rating)).isFalse();
	}



	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// TEST UPDATE RATING /////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void updateRating(User creator, User modifier, boolean canUpdate) {	
		try {
			when(userBoMock.getCurrentUser()).thenReturn(modifier);

			Rating rating = new Rating();
			rating.setUser(creator);

			assertThat(authHelper.canUpdate(rating)).isEqualTo(canUpdate);
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testCanUpdateRatingSameUser() {
		User creator = createBasicUser(1);
		updateRating(creator, creator, true);
	}

	@Test
	public void testCanUpdateRating() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(1);
		updateRating(creator, updater, true);
	}
	
	@Test
	public void testCanNotUpdateRatingNotSameUser() {
		User creator = createBasicUser(1);
		User updater = createBasicUser(2);
		updateRating(creator, updater, false);
	}
	
	@Test
	public void testCanNotUpdateRatingNotLoggedIn() {
		User creator = createBasicUser(1);
		updateRating(creator, null, false);
	}

}
