package org.fiware.apps.marketplace.oauth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.pac4j.springframework.security.authentication.CopyRolesUserDetailsService;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

/**
 * Filter that will identify a user when the X-Auth-Header is specified in a request to the API
 * @author aitor
 *
 */
public class FIWAREHeaderAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

	private String headerName;
	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
	private AuthenticationUserDetailsService<ClientAuthenticationToken> userDetailsService =
			new CopyRolesUserDetailsService();

	private FIWAREClient client;

	protected FIWAREHeaderAuthenticationFilter() {
		this("/api/", "X-Auth-Token");
	}

	protected FIWAREHeaderAuthenticationFilter(String baseUrl, String headerName) {
		// Super class constructor must be called. 
		super(new FIWAREHeaderAuthenticationRequestMatcher(baseUrl, headerName));

		// Store header name
		this.headerName = headerName;

		// Needed to continue with the process of the request
		setContinueChainBeforeSuccessfulAuthentication(true);

		// Set the authentication in the context
		setSessionAuthenticationStrategy(new FIWAREHeaderAuthenticationAuthenticationStrategy());

		// This handler doesn't do anything but it's required to replace the default one
		setAuthenticationSuccessHandler(new FIWAREHeaderAuthenticationSuccessHandler());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {

		Authentication auth = null;
		String authToken = request.getHeader(headerName);

		try {
			// This method can return an exception when the Token is invalid
			// In this case, the exception is caught and the correct exceptions is thrown...
			UserProfile profile = client.getUserProfile(authToken);

			// by default, no authorities
			Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

			// get user details and check them
			if (this.userDetailsService != null) {
				final ClientAuthenticationToken tmpToken = new ClientAuthenticationToken(null, 
						client.getName(), profile, null);
				final UserDetails userDetails = this.userDetailsService.loadUserDetails(tmpToken);

				if (userDetails != null) {
					this.userDetailsChecker.check(userDetails);
					authorities = userDetails.getAuthorities();
				}
			}

			// new token with credentials (like previously) and user profile and authorities
			OAuthCredentials credentials = new OAuthCredentials(null, authToken, "", client.getName());
			auth = new ClientAuthenticationToken(credentials, client.getName(), profile, authorities);

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
	 * 2) The X-Auth-Token header is present (X-Auth-Token: ...)
	 * @author aitor
	 *
	 */
	private static class FIWAREHeaderAuthenticationRequestMatcher implements RequestMatcher {

		private String baseUrl;
		private String headerName;

		public FIWAREHeaderAuthenticationRequestMatcher(String baseUrl, String headerName) {
			this.baseUrl = baseUrl;
			this.headerName = headerName;
		}

		@Override
		public boolean matches(HttpServletRequest request) {

			String authToken = request.getHeader(headerName);

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

			return url.startsWith(baseUrl) && authToken != null && StringUtils.hasText(authToken);}

	}

	/**
	 * Actions to be carried out when the authentication is successful. In this case
	 * no actions are required.
	 * @author aitor
	 * 
	 */
	private static class FIWAREHeaderAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

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
	private static class FIWAREHeaderAuthenticationAuthenticationStrategy implements SessionAuthenticationStrategy {

		@Override
		public void onAuthentication(Authentication authentication,
				HttpServletRequest request, HttpServletResponse response)
						throws SessionAuthenticationException {
			// Set the authentication in the current context
			SecurityContextHolder.getContext().setAuthentication(authentication);	
		}

	}
}
