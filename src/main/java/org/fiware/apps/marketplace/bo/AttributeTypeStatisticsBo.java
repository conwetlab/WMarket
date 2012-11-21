package org.fiware.apps.marketplace.bo;

import java.util.HashMap;

import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;

public interface AttributeTypeStatisticsBo {
	public void save(String key, ServiceAttributeTypeStatistics serviceAttributeTypeStatistics);
	public void delete(String attributeTypeUri);
	public ServiceAttributeTypeStatistics getByUri(String attributeTypeUri);
	public HashMap<String, ServiceAttributeTypeStatistics> getAllAttributeTypeStatistics();
}
