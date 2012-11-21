package org.fiware.apps.marketplace.model;

public class ServiceQuantitativeAttribute extends ServiceAttribute {

	// TODO suitable unit implementation

	private Double value;
	private Double minValue;
	private Double maxValue;
	private String unit;

	@Override
	public String toString() {
		return super.toString() + " " + value + "," + minValue + "," + maxValue + ";" + unit;
	}
	
	public Double getValue() {
		return value;
	}

	public Double getMinValue() {
		return minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public String getUnit() {
		return unit;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
