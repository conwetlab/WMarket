package org.fiware.apps.marketplace.bo.impl;

import java.util.HashMap;

import org.fiware.apps.marketplace.bo.AttributeTypeStatisticsBo;
import org.fiware.apps.marketplace.model.ServiceAttributeTypeStatistics;
import org.springframework.stereotype.Service;

@Service("attributeTypeStatisticsBo")
public class AttributeTypeStatisticsBoImpl implements AttributeTypeStatisticsBo {

	private HashMap<String, ServiceAttributeTypeStatistics> typeProbabilities;
	
	public AttributeTypeStatisticsBoImpl() {
		typeProbabilities = new HashMap<String, ServiceAttributeTypeStatistics>();
	}
	
	@Override
	public void save(String attributeTypeUri, ServiceAttributeTypeStatistics probabilityOfOccurence) {
		if(typeProbabilities.containsKey(attributeTypeUri))
			return;
		typeProbabilities.put(attributeTypeUri, probabilityOfOccurence);
	}

	@Override
	public void delete(String attributeTypeUri) {
		if(typeProbabilities.containsKey(attributeTypeUri))
			typeProbabilities.remove(attributeTypeUri);
	}

	@Override
	public ServiceAttributeTypeStatistics getByUri(String attributeTypeUri) {
		if(typeProbabilities.containsKey(attributeTypeUri))
			return typeProbabilities.get(attributeTypeUri);
		return null;
	}

	@Override
	public HashMap<String, ServiceAttributeTypeStatistics> getAllAttributeTypeStatistics() {
		return typeProbabilities;
	}
}
