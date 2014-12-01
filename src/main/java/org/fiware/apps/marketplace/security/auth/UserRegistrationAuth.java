package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.User;
import org.springframework.stereotype.Service;

@Service("userRegistrationAuth")
public class UserRegistrationAuth extends RegistrationAuth<User> {
	
	@Override
	protected User getEntityOwner(User user) {
		return user;
	}
	
	@Override
	public boolean canCreate() {
		return true;
	}

}
