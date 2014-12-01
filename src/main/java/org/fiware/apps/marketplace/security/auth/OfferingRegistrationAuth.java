package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.Service;


@org.springframework.stereotype.Service("offeringRegistrationAuth")
public class OfferingRegistrationAuth extends RegistrationAuth<Service> {

	@Override
	protected User getEntityOwner(Service service) {
		return service.getCreator();
	}

}
