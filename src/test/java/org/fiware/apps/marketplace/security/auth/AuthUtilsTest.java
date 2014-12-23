package org.fiware.apps.marketplace.security.auth;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtilsTest {

	@Mock private UserBo userBoMock;
	@InjectMocks private AuthUtils authUtils;

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	private void testGeneric(User user, SecurityContext context) {
		try {
			String userName = user.getUserName();

			// Configure the mocks
			when(userBoMock.findByName(userName)).thenReturn(user);

			// Configure the context returned by the SecurityContextHolder
			SecurityContextHolder.setContext(context);

			// Call the function
			User returnedUser = authUtils.getLoggedUser();

			verify(userBoMock).findByName(userName);
			assertThat(returnedUser).isEqualTo(user);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
	}

	@Test
	public void testOAuth2() {
		// User
		final String userName = "oauth2username";
		User user = new User();
		user.setUserName(userName);

		// The context
		SecurityContext context = new SecurityContext() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setAuthentication(Authentication auth) {
			}

			@Override
			public Authentication getAuthentication() {
				UserProfile userProfile = new UserProfile();
				userProfile.setId(userName);
				ClientAuthenticationToken auth = new ClientAuthenticationToken(null, "FIWARE", userProfile, null);	
				return auth;
			}
		};

		testGeneric(user, context);
	}
	
	@Test
	public void testLocalLogin() {
		// The user
		final String userName = "oauth2username";
		User user = new User();
		user.setUserName(userName);

		// The context
		SecurityContext context = new SecurityContext() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setAuthentication(Authentication arg0) {

			}

			@Override
			public Authentication getAuthentication() {
				UserProfile userProfile = new UserProfile();
				userProfile.setId(userName);
				Authentication auth = new Authentication() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public String getName() {
						return userName;
					}
					
					@Override
					public void setAuthenticated(boolean arg0) throws IllegalArgumentException {						
					}
					
					@Override
					public boolean isAuthenticated() {
						return true;
					}
					
					@Override
					public Object getPrincipal() {
						return null;
					}
					
					@Override
					public Object getDetails() {
						return null;
					}
					
					@Override
					public Object getCredentials() {
						return null;
					}
					
					@Override
					public Collection<? extends GrantedAuthority> getAuthorities() {
						return null;
					}
				};

				return auth;
			}
		};

		testGeneric(user, context);
	}
	
	



}
