package org.fiware.apps.marketplace.model;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.fiware.apps.marketplace.exceptions.ParseException;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

@Entity
@Table(name = "price_components")
@XmlRootElement(name = "priceComponents")
@IgnoreMediaTypes("application/*+json")
public class PriceComponent {
	
	// Hibernate
	private int id;
	private PricePlan pricePlan;
	
	private String title;
	private String comment;
	private float value;		// 7.7, 8.9,...
	private String currency;	// EUR, USD,...
	private String unit;		// single payment, months, MB,...
	
	/**
	 * Empty constructor for Hibernate
	 */
	public PriceComponent() {
		
	}
	
	/**
	 * Creates a Price Component from a raw price component extracted from RDF
	 * @param rawPriceComponent The raw price component
	 * @param pricePlan The price plan that contains the price component
	 * @throw ParseException When the raw price component is not valid
	 */
	public PriceComponent(Map<String, List<Object>> rawPriceComponent, PricePlan pricePlan) throws ParseException {
		
		// Check Price Component properties
		List<Object> priceComponentLabels = rawPriceComponent.get("label");
		if (priceComponentLabels == null || priceComponentLabels.isEmpty()) {
			throw new ParseException("Offering " + pricePlan.getOffering().getDisplayName() + 
					" contains a Price Component without title");
		}
		
		List<Object> rawPriceSpecifications = rawPriceComponent.get("hasPrice");
		if (rawPriceSpecifications == null || rawPriceSpecifications.isEmpty()) {
			throw new ParseException("Offering " + pricePlan.getOffering().getDisplayName() + 
					" contains a Price Component without price specification");
		}
		
		@SuppressWarnings("unchecked")
		Map<String, List<Object>> rawPriceSpecification = (Map<String, List<Object>>) rawPriceSpecifications.get(0);

		// Check price specification
		if (rawPriceSpecification.get("hasCurrency").isEmpty()) {
			throw new ParseException("Offering " + pricePlan.getOffering().getDisplayName() + 
					" contains a Price Component without currency");
		}
		
		if (rawPriceSpecification.get("hasUnitOfMeasurement").isEmpty()) {
			throw new ParseException("Offering " + pricePlan.getOffering().getDisplayName() + 
					" contains a Price Component without unit of measurement");
		}
		
		if (rawPriceSpecification.get("hasCurrencyValue").isEmpty()) {
			throw new ParseException("Offering " + pricePlan.getOffering().getDisplayName() + 
					" contains a Price Component without value");
		}
		
		this.pricePlan = pricePlan;
		this.title = (String) rawPriceComponent.get("label").get(0);
		List<Object> pcDescriptions = rawPriceComponent.get("description");
		this.comment = pcDescriptions.size() == 1 ? (String) pcDescriptions.get(0) : "";
		
		// Complete price component
		this.currency = (String) rawPriceSpecification.get("hasCurrency").get(0);
		this.unit = (String) rawPriceSpecification.get("hasUnitOfMeasurement").get(0);
		this.value = Float.parseFloat((String) rawPriceSpecification.get("hasCurrencyValue").get(0));
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@XmlTransient
	@ManyToOne(optional = false)
	@JoinColumn(name = "price_plan", nullable=false)
	public PricePlan getPricePlan() {
		return pricePlan;
	}

	public void setPricePlan(PricePlan pricePlan) {
		this.pricePlan = pricePlan;
	}

	@XmlElement
	@Column(name = "title")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement
	@Column(name = "comment")
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@XmlElement
	@Column(name = "currency")
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@XmlElement
	@Column(name = "value")
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	@XmlElement
	@Column(name = "unit")
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * This object is MUTABLE since user can modify its attributes at ANY TIME. Please, be careful and
	 * set all the properties before including an instance in any collection or you may experience
	 * unexpected behaviors.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (currency == null ? 0 : currency.hashCode());
		result = prime * result + (comment == null ? 0 : comment.hashCode());
		result = prime * result + (title == null ? 0 : title.hashCode());
		result = prime * result + (unit == null ? 0 : unit.hashCode());
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof PriceComponent) {
			
			PriceComponent other = (PriceComponent) obj;
			
			return this.title.equals(other.title) &&
					this.comment.equals(other.comment) &&
					this.currency.equals(other.currency) &&
					this.unit.equals(other.unit) &&
					this.value == other.value;
			
		} 
		
		return false;
	}
	
}
