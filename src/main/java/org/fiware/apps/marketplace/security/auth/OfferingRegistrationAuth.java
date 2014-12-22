package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Service;


@org.springframework.stereotype.Service("offeringRegistrationAuth")
public class OfferingRegistrationAuth extends RegistrationAuth<Service> {

	@Override
	protected User getEntityOwner(Service service) {
		return service.getCreator();
	}
	
	/**
	 * Method to know if a user can list the offerings belonging to a Store
	 * @param store The store whose offerings will be listed
	 * @return By default it returns true
	 */
	public boolean canList(Store store) {
		return true;
	}

}
