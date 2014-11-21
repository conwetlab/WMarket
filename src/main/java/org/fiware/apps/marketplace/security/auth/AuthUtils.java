package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.bo.LocaluserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
	
	// Avoid users to instanciate this class
	private AuthUtils() {
		this.localuserBo = (LocaluserBo) ApplicationContextProvider.getApplicationContext().getBean("localuserBo");
	}
	
	public static AuthUtils getAuthUtils() {
		return singleton;
	}
	
	//Singleton
	private static AuthUtils singleton = new AuthUtils();
	
	//Instance attributes
	private LocaluserBo localuserBo;

	public Localuser getLoggedUser() {
		try {
			return localuserBo.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
		} catch (UserNotFoundException ex) {
			//This exception should never happen: a logged user should be found in the database...
			return null;
		}
	}
	
}
