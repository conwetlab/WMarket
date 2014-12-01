package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
	
	// Avoid users to instantiate this class
	private AuthUtils() {
		this.localuserBo = (UserBo) ApplicationContextProvider.getApplicationContext().getBean("userBo");
	}
	
	public static AuthUtils getAuthUtils() {
		return singleton;
	}
	
	//Singleton
	private static AuthUtils singleton = new AuthUtils();
	
	//Instance attributes
	private UserBo localuserBo;

	public User getLoggedUser() throws UserNotFoundException {
		return localuserBo.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
}
