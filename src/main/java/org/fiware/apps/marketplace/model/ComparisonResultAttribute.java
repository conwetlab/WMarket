package org.fiware.apps.marketplace.model;

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
