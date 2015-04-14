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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import org.fiware.apps.marketplace.oauth2.FIWAREHeaderAuthenticationFilter.FIWAREHeaderAuthenticationRequestMatcher;
import org.junit.Test;

public class FIWAREHeaderAuthenticationRequestMatcherTest {
	
	private static final String BASE_URL = "/system/";
	private static final String HEADER_NAME = "Auth-Tkt";
	
	private FIWAREHeaderAuthenticationRequestMatcher matcher = 
			new FIWAREHeaderAuthenticationRequestMatcher(BASE_URL, HEADER_NAME);
			
	public void testMatcher(String servletPath, String pathInfo, String queryString, 
			String headerValue, boolean matches) {
		
		// Setup the Servlet
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getServletPath()).thenReturn(servletPath);
		when(request.getPathInfo()).thenReturn(pathInfo);
		when(request.getQueryString()).thenReturn(queryString);
		when(request.getHeader(HEADER_NAME)).thenReturn(headerValue);
		
		assertThat(matcher.matches(request)).isEqualTo(matches);
	}
	
	@Test
	public void testMatchesNoQueryNoPath() {
		testMatcher(BASE_URL, null, null, "bearer Header_Val", true);
	}
	
	@Test
	public void testMatchesNoQuery() {
		testMatcher(BASE_URL, "/store", null, "bearer Header-Val!!#@", true);
	}
	
	@Test
	public void testMatches() {
		testMatcher(BASE_URL, "/store", "foo=boo", "bearer Header-_.,^Val", true);
	}
	
	@Test
	public void testCaseInsensitive() {
		testMatcher(BASE_URL, "/store", "foo=boo", "Bearer Header-_.,^Val", true);
	}
	
	@Test
	public void testNoMatchesNoQueryNoPathInvalidPath() {
		testMatcher("/api/", null, null, "Header Val", false);
	}
	
	@Test
	public void testNoMatchesNoQueryInvalidPath() {
		testMatcher("/api/", "/store", null, "Header Val", false);
	}
	
	@Test
	public void testNoMatchesInvalidPath() {
		testMatcher("/api/", "/store", "foo=boo", "Header Val", false);
	}
	
	@Test
	public void testNoMatchesNoQueryNoPathInvalidPathNoHeader() {
		testMatcher("/api/", null, null, null, false);
	}
	
	@Test
	public void testNoMatchesNoQueryInvalidPathNoHeader() {
		testMatcher("/api/", "/store", null, null, false);
	}
	
	@Test
	public void testNoMatchesInvalidPathNoHeader() {
		testMatcher("/api/", "/store", "foo=boo", null, false);
	}
	
	@Test
	public void testNoMatchesNoQueryNoPathNoHeader() {
		testMatcher(BASE_URL, null, null, null, false);
	}
	
	@Test
	public void testNoMatchesNoQueryNoHeader() {
		testMatcher(BASE_URL, "/store", null, null, false);
	}
	
	@Test
	public void testNoMatchesNoHeader() {
		testMatcher(BASE_URL, "/store", "foo=boo", null, false);
	}
}
