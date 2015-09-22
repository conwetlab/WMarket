package org.fiware.apps.marketplace.security.entryPoints;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.springframework.security.web.ClientAuthenticationEntryPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * By default, clients attempts to log in users when they access a protected resource without credentials. However,
 * this behavior is not appropriate when the API is being used, since developers expect a 401 HTTP error when the user
 * is not authenticated. This class changes this behavior to work as expected by developers:
 * <ul>
 * <li>401 is returned when an unauthenticated users attempts to access a protected resource in the API</li>
 * <li>Users are redirected to the login page when they try to access a protected resource outside the API</li>
 * </ul>
 * @author aitor
 *
 */
public class RESTAwareClientAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
	
	private ClientAuthenticationEntryPoint entryPoint = new ClientAuthenticationEntryPoint();

	@Override
	public void afterPropertiesSet() throws Exception {
		entryPoint.afterPropertiesSet();
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		if (request.getServletPath().startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		} else {
			entryPoint.commence(request, response, authException);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Client<Credentials, UserProfile> getClient() {
		return entryPoint.getClient();
	}
	
	public void setClient(final Client<Credentials, UserProfile> client) {
		entryPoint.setClient(client);
	}

}
