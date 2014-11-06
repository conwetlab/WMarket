package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "serviceManifestation")
public class ServiceManifestation {
	
	private Integer id;
	private String offeringUri;
	private String offeringTitle;
	private String storeUrl;
	private List<String> serviceUris;
	private String pricePlanUri;
	private String pricePlanTitle;
	private List<String> priceComponentUris;
	private String name;
	private List<ServiceAttribute> attributes;

	public ServiceManifestation() {
		attributes = new ArrayList<ServiceAttribute>();
		serviceUris = new ArrayList<String>();
		priceComponentUris = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return "(" + id + ") " + name;
	}

	@XmlAttribute
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public String getOfferingUri() {
		return offeringUri;
	}

	@XmlTransient
	public String getPricePlanUri() {
		return pricePlanUri;
	}

	public void setOfferingUri(String offeringUri) {
		this.offeringUri = offeringUri;
	}

	public void setPricePlanUri(String pricePlanUri) {
		this.pricePlanUri = pricePlanUri;
	}

	public void addAttribute(ServiceAttribute attribute) {
		this.attributes.add(attribute);
	}

	public void addAttributes(List<ServiceAttribute> attributes) {
		this.attributes.addAll(attributes);
	}

	@XmlTransient
	public List<ServiceAttribute> getAttributes() {
		return this.attributes;
	}

	@XmlTransient
	public List<String> getServiceUris() {
		return serviceUris;
	}

	@XmlTransient
	public List<String> getPriceComponentUris() {
		return priceComponentUris;
	}

	public void addServiceUri(String serviceUri) {
		this.serviceUris.add(serviceUri);
	}

	public void addServiceUris(List<String> serviceUris) {
		this.serviceUris.addAll(serviceUris);
	}

	public void addPriceComponentUri(String priceComponentUri) {
		this.priceComponentUris.add(priceComponentUri);
	}

	public void addPriceComponentUris(List<String> priceComponentUris) {
		this.priceComponentUris.addAll(priceComponentUris);
	}

	public void setPricePlanTitle(String pricePlanTitle) {
		this.pricePlanTitle = pricePlanTitle;
	}

	@XmlTransient
	public String getPricePlanTitle() {
		return this.pricePlanTitle;
	}

	@XmlTransient
	public String getOfferingTitle() {
		return offeringTitle;
	}

	public void setOfferingTitle(String offeringTitle) {
		this.offeringTitle = offeringTitle;
	}

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}

	@XmlTransient
	public String getStoreUrl() {
		return storeUrl;
	}

}
