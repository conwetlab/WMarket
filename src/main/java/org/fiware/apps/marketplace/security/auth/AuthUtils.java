package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authUtils")
public class AuthUtils {
	
	//Instance attributes
	private UserBo localuserBo = (UserBo) ApplicationContextProvider.getApplicationContext().getBean("userBo");

	public User getLoggedUser() throws UserNotFoundException {
		String userName;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// When OAuth2 is being used, we should cast the authentication to read the correct user name
		if (authentication instanceof ClientAuthenticationToken) {
			userName = ((ClientAuthenticationToken) authentication).getUserProfile().getId();
		} else {
			userName = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		
		return localuserBo.findByName(userName);
	}
	
}
