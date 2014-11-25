package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Localuser;
import org.springframework.stereotype.Service;

@Service("userRegistrationAuth")
public class UserRegistrationAuth extends RegistrationAuth<Localuser> {
	
	@Override
	protected Localuser getEntityOwner(Localuser user) {
		return user;
	}
	
	@Override
	public boolean canCreate() {
		return true;
	}

}
