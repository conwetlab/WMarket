package org.fiware.apps.marketplace.bo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.bo.AttributeTypeBo;
import org.fiware.apps.marketplace.model.ServiceAttributeType;
import org.springframework.stereotype.Service;

@Service("attributeTypeBo")
public class AttributeTypeBoImpl implements AttributeTypeBo {

	// TODO Store data in a database or similar

	private HashMap<String, ServiceAttributeType> attributeTypeMap;

	public AttributeTypeBoImpl() {
		attributeTypeMap = new HashMap<String, ServiceAttributeType>();
	}

	@Override
	public void save(ServiceAttributeType attributeType) {
		if (!attributeTypeMap.containsKey(attributeType.getUri()))
			attributeTypeMap.put(attributeType.getUri(), attributeType);
	}

	@Override
	public void delete(ServiceAttributeType attributeType) {
		if (attributeTypeMap.containsKey(attributeType.getUri()))
			attributeTypeMap.remove(attributeType.getUri());
	}

	@Override
	public ServiceAttributeType getByUri(String uri) {
		if (attributeTypeMap.containsKey(uri))
			return attributeTypeMap.get(uri);
		else
			return null;
	}

	@Override
	public List<ServiceAttributeType> getAllAttributeTypesAsList() {
		return new ArrayList<ServiceAttributeType>(attributeTypeMap.values());
	}
	
	@Override
	public HashMap<String, ServiceAttributeType> getAllAttributeTypesAsMap() {
		return attributeTypeMap;
	}
}
