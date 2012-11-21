package org.fiware.apps.marketplace.bo;

import java.util.List;

import org.fiware.apps.marketplace.model.Offering;

public interface OfferingBo {
	public void save(Offering offering);
	public void delete(Offering offering);
	public Offering getByUri(String uri);
	public List<Offering> getAllOfferings();
}
