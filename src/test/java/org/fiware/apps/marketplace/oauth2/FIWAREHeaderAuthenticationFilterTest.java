package org.fiware.apps.marketplace.oauth2;

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
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class FIWAREHeaderAuthenticationFilterTest {

	private static final String BASE_URL = "/system/";
	private static final String HEADER_NAME = "Auth-Tkt";
	private FIWAREHeaderAuthenticationFilter filter = 
			new FIWAREHeaderAuthenticationFilter(BASE_URL, HEADER_NAME);

	public void testAttemptAuthentication(List<GrantedAuthority> roles)  {
		String authToken = "authTokenSimulation";

		// Profile
		FIWAREProfile profile = new FIWAREProfile();
		profile.addAttribute("nickName", "exampleNickName");
		profile.addAttribute("displayName", "EXAMPLE DISPLAY NAME");
		profile.addAttribute("email", "example@example.com");

		for (GrantedAuthority authority: roles) {
			profile.addRole(authority.getAuthority());
		}

		// Set the client
		FIWAREClient client = mock(FIWAREClient.class);
		when(client.getUserProfile(authToken)).thenReturn(profile);
		filter.setClient(client);

		// Set the HTTPRequest
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HEADER_NAME)).thenReturn(authToken);

		// Call the method
		try {
			Authentication auth = filter.attemptAuthentication(request, null);
			assertThat(auth).isInstanceOf(ClientAuthenticationToken.class);
			assertThat(auth.getAuthorities()).isEqualTo(roles);

			ClientAuthenticationToken castedAuth = (ClientAuthenticationToken) auth;
			assertThat(castedAuth.getUserProfile()).isEqualTo(profile);
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
	}

	@Test
	public void testAttemptAuthenticationNoRoles() throws AuthenticationException, IOException, ServletException {
		testAttemptAuthentication(new ArrayList<GrantedAuthority>());
	}
	
	@Test
	public void testAttemptAuthenticationOneRol() throws AuthenticationException, IOException, ServletException {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		testAttemptAuthentication(authorities);
	}
	
	@Test
	public void testAttemptAuthenticationSomeRoles() throws AuthenticationException, IOException, ServletException {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
		
		testAttemptAuthentication(authorities);
	}
	
	@Test
	public void testExceptionRisenOnAuthError() {
		String authToken = "authTokenSimulation";

		// FIWARE Client
		FIWAREClient client = mock(FIWAREClient.class);
		doThrow(new RuntimeException("Exception Message")).when(client).getUserProfile(authToken);
		
		// Set the HTTPRequest
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HEADER_NAME)).thenReturn(authToken);

		// Call the method
		try {
			filter.attemptAuthentication(request, null);
			failBecauseExceptionWasNotThrown(BadCredentialsException.class);
		} catch (BadCredentialsException ex) {
			// Nothing to do... This is what it was expected
		} catch (Exception ex) {
			fail("Exception " + ex + " not expected");
		}
		
		
		
	}

}
