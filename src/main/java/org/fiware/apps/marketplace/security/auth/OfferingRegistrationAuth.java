package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Service;


@org.springframework.stereotype.Service("offeringRegistrationAuth")
public class OfferingRegistrationAuth extends RegistrationAuth<Service> {

	@Override
	protected Localuser getEntityOwner(Service service) {
		return service.getCreator();
	}

}
