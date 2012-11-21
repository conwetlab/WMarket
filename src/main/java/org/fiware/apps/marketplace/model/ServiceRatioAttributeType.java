package org.fiware.apps.marketplace.model;

public class ServiceRatioAttributeType extends ServiceAttributeType {
	
	public static final String baseTypeUri = "http://purl.org/goodrelations/v1#QuantitativeValue";
	
	@Override
	public String toString() {
		return "(r) " + super.toString();
	}
}