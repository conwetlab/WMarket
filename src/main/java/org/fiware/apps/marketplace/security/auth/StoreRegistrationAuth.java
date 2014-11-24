package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Store;
import org.springframework.stereotype.Service;

@Service("storeRegistrationAuth")
public class StoreRegistrationAuth {
	
	private boolean isTheSameUser(Store store) {
		boolean canAccess = false;
		Localuser loggedUser = AuthUtils.getAuthUtils().getLoggedUser();
		
		// logged User can be null if the user is not logged in...
		if (loggedUser != null && loggedUser.equals(store.getCreator())) {
			canAccess = true;
		}
		
		return canAccess;
	}
	
	public boolean canCreate() {
		return AuthUtils.getAuthUtils().getLoggedUser() != null;
	}
	
	public boolean canUpdate(Store store) {
		return this.isTheSameUser(store);
	}
	
	public boolean canDelete(Store store) {
		return this.isTheSameUser(store);
	}
	
	public boolean canGet(Store store) {
		return true;
	}
	
	public boolean canList() {
		return true;
	}
	

}
