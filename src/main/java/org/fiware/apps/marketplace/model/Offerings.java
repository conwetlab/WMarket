package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

@XmlRootElement(name = "offerings")
@IgnoreMediaTypes("application/*+json")
public class Offerings {

	private List<Offering> offerings = null;

	public Offerings() {
		this.setOfferings(new ArrayList<Offering>());
	}

	public Offerings(List<Offering> offerings) {
		this.setOfferings(offerings);
	}

	@XmlElement(name = "offering")
	@JsonProperty("offerings")
	public List<Offering> getOfferings() {
		return this.offerings;
	}

	public void setOfferings(List<Offering> offerings) {
		this.offerings = offerings;
	}
}
