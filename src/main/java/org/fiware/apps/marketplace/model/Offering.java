package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

public class Offering {

	private String offeringTitle;
	private int offeringId;
	private String offeringUri;
	private List<String> pricePlanUris;
	private List<String> serviceUris;
	private String storeUrl;
	
	public Offering () {
		pricePlanUris = new ArrayList<String>();
		serviceUris = new ArrayList<String>();
	}
	
	@Override
	public String toString() {
		return offeringUri.substring(offeringUri.lastIndexOf("/") + 1);
	}

	public int getOfferingId() {
		return offeringId;
	}
	
	public void setOfferingUri(String uri) {
		offeringUri = uri;
	}

	public String getOfferingUri() {
		return offeringUri;
	}

	public void addPricePlanUri(String uri) {
		pricePlanUris.add(uri);
	}

	public List<String> getPricePlanUris() {
		return pricePlanUris;
	}
		
	public List<String> getServiceUris() {
		return serviceUris;
	}

	public void addServiceUri(String uri) {
		this.serviceUris.add(uri);
	}

	public void setTitle(String offeringTitle) {
		this.offeringTitle = offeringTitle;
	}
	
	public String getTitle() {
		return this.offeringTitle;
	}

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}
	
	public String getStoreUrl() {
		return storeUrl;
	}
}
