package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Localuser;
import org.springframework.stereotype.Service;

@Service("userRegistrationAuth")
public class UserRegistrationAuth {
	
	private boolean isTheSameUser(Localuser user) {
		boolean canAccess = false;
		Localuser loggedUser = AuthUtils.getAuthUtils().getLoggedUser();
		
		// logged User can be null if the user is not logged in...
		if (loggedUser != null && loggedUser.equals(user)) {
			canAccess = true;
		}
		
		return canAccess;
	}
	
	public boolean canCreate() {
		return true;
	}
	
	public boolean canUpdate(Localuser user) {
		return this.isTheSameUser(user);
	}
	
	public boolean canDelete(Localuser user) {
		return this.isTheSameUser(user);
	}
	
	public boolean canGet(Localuser user) {
		return true;
	}
	
	public boolean canList() {
		return true;
	}
	

}
