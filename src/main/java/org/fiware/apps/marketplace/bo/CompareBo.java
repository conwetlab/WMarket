package org.fiware.apps.marketplace.bo;

import org.fiware.apps.marketplace.model.ComparisonResult;

public interface CompareBo {
	ComparisonResult compareService(String sourceIdString);
	ComparisonResult compareService(String sourceIdString, String targetIdString);
}
