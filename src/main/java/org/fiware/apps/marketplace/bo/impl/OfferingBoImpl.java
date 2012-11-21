package org.fiware.apps.marketplace.bo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.model.Offering;
import org.springframework.stereotype.Service;

@Service("offeringBo")
public class OfferingBoImpl implements OfferingBo {

	// TODO Store data in a database or similar

	private HashMap<String, Offering> offeringMap;

	public OfferingBoImpl() {
		offeringMap = new HashMap<String, Offering>();
	}

	@Override
	public void save(Offering offering) {
		if (!offeringMap.containsKey(offering.getOfferingUri()))
			offeringMap.put(offering.getOfferingUri(), offering);
	}

	@Override
	public void delete(Offering offering) {
		if (offeringMap.containsKey(offering.getOfferingUri()))
			offeringMap.remove(offering.getOfferingUri());
	}

	@Override
	public Offering getByUri(String uri) {
		if (offeringMap.containsKey(uri))
			return offeringMap.get(uri);
		else
			return null;
	}

	@Override
	public List<Offering> getAllOfferings() {
		return new ArrayList<Offering>(offeringMap.values());
	}
}
