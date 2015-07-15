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
