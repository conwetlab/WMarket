package org.fiware.apps.marketplace.oauth2;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2014-2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Filter that will identify a user when the Authorization header is specified in a request to the API
 * @author aitor
 *
 */
public class FIWAREHeaderAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private String headerName;
	private FIWAREClient client;
	
	private static final Pattern AUTHORIZATION_PATTERN = 
			Pattern.compile("^bearer ([^\\s]+)$", Pattern.CASE_INSENSITIVE);

	protected FIWAREHeaderAuthenticationFilter() {
		this("/api/", "Authorization");
	}

	protected FIWAREHeaderAuthenticationFilter(String baseUrl, String headerName) {
		// Super class constructor must be called. 
		super(new FIWAREHeaderAuthenticationRequestMatcher(baseUrl, headerName));

		// Store header name
		this.headerName = headerName;

		// Needed to continue with the process of the request
		setContinueChainBeforeSuccessfulAuthentication(true);

		// Set the authentication in the context
		setSessionAuthenticationStrategy(new FIWAREHeaderAuthenticationStrategy());

		// This handler doesn't do anything but it's required to replace the default one
		setAuthenticationSuccessHandler(new FIWAREHeaderAuthenticationSuccessHandler());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {

		Authentication auth = null;
		String authHeader = request.getHeader(headerName);
		Matcher matcher = AUTHORIZATION_PATTERN.matcher(authHeader);
				
		try {
			
			// We only have one possible match
			if (matcher.find()) {
				String authToken = matcher.group(1);
				
				// This method can return an exception when the Token is invalid
				// In this case, the exception is caught and the correct exceptions is thrown...
				UserProfile profile = client.getUserProfile(authToken);

				// Define authorities
				Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		        for (String role: profile.getRoles()) {
		            authorities.add(new SimpleGrantedAuthority(role));
		        }

				// new token with credentials (like previously) and user profile and authorities
				OAuthCredentials credentials = new OAuthCredentials(null, authToken, "", client.getName());
				auth = new ClientAuthenticationToken(credentials, client.getName(), profile, authorities);	
			} else {
				// This is not supposed to happen
				throw new IllegalStateException("Pattern is suppossed to match.");
			}

		} catch (Exception ex) {
			// This exception should be risen in order to return a 401
			throw new BadCredentialsException("The provided token is invalid or the system was not able to check it");
		}

		return auth;
	}

	public FIWAREClient getClient() {
		return this.client;
	}

	public void setClient(FIWAREClient client) {
		this.client = client;
	}

	// AUXILIAR CLASSES //

	/**
	 * Request Matcher that specifies when the filter should be executed. In this case we 
	 * want the filter to be executed when the following two conditions are true:
	 * 1) The request is to the API (/api/...)
	 * 2) The X-Auth-Token header is present (Authorization: ...)
	 * @author aitor
	 *
	 */
	static class FIWAREHeaderAuthenticationRequestMatcher implements RequestMatcher {

		private String baseUrl;
		private String headerName;

		public FIWAREHeaderAuthenticationRequestMatcher(String baseUrl, String headerName) {
			this.baseUrl = baseUrl;
			this.headerName = headerName;
		}

		@Override
		public boolean matches(HttpServletRequest request) {

			String authHeader = request.getHeader(headerName);

			// Get path
			String url = request.getServletPath();
			String pathInfo = request.getPathInfo();
			String query = request.getQueryString();

			if (pathInfo != null || query != null) {
				StringBuilder sb = new StringBuilder(url);

				if (pathInfo != null) {
					sb.append(pathInfo);
				}

				if (query != null) {
					sb.append('?').append(query);
				}
				url = sb.toString();
			}
			
			return url.startsWith(baseUrl) && authHeader != null && 
					AUTHORIZATION_PATTERN.matcher(authHeader).matches();
		}
	}

	/**
	 * Actions to be carried out when the authentication is successful. In this case
	 * no actions are required.
	 * @author aitor
	 * 
	 */
	static class FIWAREHeaderAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
						throws IOException, ServletException {
			// Nothing to do... The chain will continue
		}
	}

	/**
	 * Set the Session in the Security Context when the Authorization token is valid
	 * @author aitor
	 *
	 */
	static class FIWAREHeaderAuthenticationStrategy implements SessionAuthenticationStrategy {

		@Override
		public void onAuthentication(Authentication authentication,
				HttpServletRequest request, HttpServletResponse response)
						throws SessionAuthenticationException {
			// Set the authentication in the current context
			SecurityContextHolder.getContext().setAuthentication(authentication);	
		}

	}
}
