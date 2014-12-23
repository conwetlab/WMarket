package org.fiware.apps.marketplace.model;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
