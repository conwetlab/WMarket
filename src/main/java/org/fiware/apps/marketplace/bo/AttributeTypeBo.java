package org.fiware.apps.marketplace.bo;

import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.model.ServiceAttributeType;

public interface AttributeTypeBo {
	public void save(ServiceAttributeType attributeType);

	public void delete(ServiceAttributeType attributeType);

	public ServiceAttributeType getByUri(String uri);

	public List<ServiceAttributeType> getAllAttributeTypesAsList();

	public HashMap<String, ServiceAttributeType> getAllAttributeTypesAsMap();
}
