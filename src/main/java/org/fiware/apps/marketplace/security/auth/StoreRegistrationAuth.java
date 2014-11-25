package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Store;
import org.springframework.stereotype.Service;

@Service("storeRegistrationAuth")
public class StoreRegistrationAuth extends RegistrationAuth<Store> {

	@Override
	protected Localuser getEntityOwner(Store store) {
		return store.getCreator();
	}

}
