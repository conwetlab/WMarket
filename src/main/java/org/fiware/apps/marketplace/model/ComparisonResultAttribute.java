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
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fiware.apps.marketplace.helpers.AttributeUnitFactorResolver;

@XmlRootElement(name="comparedAttribute")
public class ComparisonResultAttribute {
	private Double value;
	private Double minValue;
	private Double maxValue;
	private Double score;
	private Integer index;
	private String unit;
	private String label;
	private String type;
	private String typeLabel;
	private String typeUri;
	private String uri;
	private List<ComparisonResultAttribute> valueReferences;
	
	public ComparisonResultAttribute () {
		
	}
	
	public ComparisonResultAttribute (ServiceAttribute attribute, HashMap<String, ServiceAttributeType> typeMap) {
		ServiceAttributeType type = typeMap.get(attribute.getTypeUri());
		this.typeLabel = type.getPreferedLabel();
		this.label = attribute.getLabel();
		
		if(type.getClass().equals(ServiceNominalAttributeType.class)) {
			this.type = "nominal";
		} else if (type.getClass().equals(ServiceOrdinalAttributeType.class)) {
			this.type = "ordinal";
		} else if(type.getClass().equals(ServiceRatioAttributeType.class)) {
			this.type = "ratio";
			this.unit = AttributeUnitFactorResolver.getPlaintext(((ServiceQuantitativeAttribute)attribute).getUnit());
			this.value = ((ServiceQuantitativeAttribute)attribute).getValue();
			this.minValue = ((ServiceQuantitativeAttribute)attribute).getMinValue();
			this.maxValue = ((ServiceQuantitativeAttribute)attribute).getMaxValue();
		}
		
		if(attribute.getValueReferences() != null && attribute.getValueReferences().size() > 0) {
			for(ServiceAttribute valueReference : attribute.getValueReferences()) {
				addValueReference(valueReference, typeMap);
			}
		}
		
		this.uri = attribute.getUri();
		this.typeUri = attribute.getTypeUri();
	}
	
	public ComparisonResultAttribute (ServiceAttribute attribute, HashMap<String, ServiceAttributeType> typeMap, Double score, Integer index) {
		this(attribute, typeMap);
		this.score = score;
		this.index = index;
	}

	@XmlAttribute
	public Double getValue() {
		return value;
	}

	@XmlAttribute
	public Double getMinValue() {
		return minValue;
	}

	@XmlAttribute
	public Double getMaxValue() {
		return maxValue;
	}

	@XmlAttribute
	public Double getScore() {
		return score;
	}

	@XmlAttribute
	public String getUnit() {
		return unit;
	}
	
	@XmlAttribute
	public String getTypeLabel() {
		return typeLabel;
	}

	@XmlAttribute
	public String getLabel() {
		return label;
	}
	
	@XmlAttribute
	public String getType() {
		return this.type;
	}

	@XmlAttribute
	public Integer getIndex() {
		return this.index;
	}

	@XmlAttribute
	public String getTypeUri() {
		return typeUri;
	}

	@XmlAttribute
	public String getUri() {
		return uri;
	}

	@XmlElement(name="valueReference")
	public ComparisonResultAttribute[] getValueReferences() {
		if(this.valueReferences == null)
			return null;
		
		return this.valueReferences.toArray(new ComparisonResultAttribute[this.valueReferences.size()]);
	}
	
	private void addValueReference(ServiceAttribute valueReference, HashMap<String, ServiceAttributeType> typeMap) {
		if(this.valueReferences == null)
			this.valueReferences = new ArrayList<ComparisonResultAttribute>();
		this.valueReferences.add(new ComparisonResultAttribute(valueReference, typeMap));
	}
}
