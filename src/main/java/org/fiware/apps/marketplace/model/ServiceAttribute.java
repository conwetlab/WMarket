package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.mysql.jdbc.StringUtils;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement
public abstract class ServiceAttribute {
	private String typeUri;
	private String uri;
	private String label;
	private List<ServiceAttribute> valueReferences;

	public ServiceAttribute() {
	}

	public void setTypeUri(String typeUri) {
		this.typeUri = typeUri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addValueReference(ServiceAttribute valueReference) {
		if (valueReferences == null)
			this.valueReferences = new ArrayList<ServiceAttribute>();
		this.valueReferences.add(valueReference);
	}

	public void setReferencedAttributes(List<ServiceAttribute> referencedAttributes) {
		this.valueReferences = referencedAttributes;
	}

	@Override
	public String toString() {
		return getShortUri() + " : " + getShortTypeUri() + " (refs " + valueReferences.size() + ")";
	}

	@XmlTransient
	public String getUri() {
		return uri;
	}
	
	@XmlAttribute(name = "type")
	public String getAttributeClassName() {
		return this.getClass().getSimpleName();
	}

	@XmlAttribute(name = "shortUri")
	public String getShortUri() {
		if (!StringUtils.isNullOrEmpty(uri)) {
			if (uri.contains("#"))
				return uri.substring(uri.lastIndexOf("#") + 1);
		}
		return null;
	}

	@XmlAttribute(name = "label")
	public String getLabel() {
		return label;
	}

	@XmlTransient
	public String getTypeUri() {
		return typeUri;
	}

	@XmlAttribute(name = "shortTypeUri")
	public String getShortTypeUri() {
		if (!StringUtils.isNullOrEmpty(typeUri)) {
			if (typeUri.contains("#"))
				return typeUri.substring(typeUri.lastIndexOf("/") + 1);
		}
		return null;
	}

	@XmlElementWrapper(name = "valueRefs")
	@XmlElement("valueRef")
	public List<ServiceAttribute> getValueReferences() {
		return this.valueReferences;
	}
	
	@XmlAttribute(name = "unit")
	public String getUnit() {
		if(this.getClass() == ServiceQuantitativeAttribute.class)
			return ((ServiceQuantitativeAttribute)this).getUnit();
		return null;
	}

	@XmlAttribute(name = "value")
	public Double getValue() {
		if(this.getClass() == ServiceQuantitativeAttribute.class)
			return ((ServiceQuantitativeAttribute)this).getValue();
		return null;
	}

	@XmlAttribute(name = "minValue")
	public Double getMinValue() {
		if(this.getClass() == ServiceQuantitativeAttribute.class)
			return ((ServiceQuantitativeAttribute)this).getValue();
		return null;
	}

	@XmlAttribute(name = "maxValue")
	public Double getMaxValue() {
		if(this.getClass() == ServiceQuantitativeAttribute.class)
			return ((ServiceQuantitativeAttribute)this).getValue();
		return null;
	}
}
