package org.fiware.apps.marketplace.model;

public class ServiceNominalAttributeType extends ServiceAttributeType {

	public static final String baseTypeUri = "http://purl.org/goodrelations/v1#QualitativeValue";
	
	@Override
	public String toString(){
		return "(n) " + super.toString();
	}
}