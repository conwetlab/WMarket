package org.fiware.apps.marketplace.model;

import java.util.HashMap;

public class ServiceAttributeTypeStatistics {
	private int occurrences = 0;
	private double occurrenceProbability = 0.0;
	private String uri;
	private HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap = new HashMap<String, ServiceAttributeTypeStatistics>();
	
	private Double minOfValue;
	private Double maxOfValue;
	private Double minOfMinValue;
	private Double maxOfMinValue;
	private Double minOfMaxValue;
	private Double maxOfMaxValue;

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public HashMap<String, ServiceAttributeTypeStatistics> getTypeStatsMap() {
		return typeStatsMap;
	}
	
	public void setTypeStatsMap(HashMap<String, ServiceAttributeTypeStatistics> typeStatsMap) {
		this.typeStatsMap = typeStatsMap;
	}

	public double getOccurrenceProbability() {
		return occurrenceProbability;
	}

	public void setOccurrenceProbability(double occurrenceProbability) {
		this.occurrenceProbability = occurrenceProbability;
	}

	public Double getMinOfValue() {
		return minOfValue;
	}

	public Double getMaxOfValue() {
		return maxOfValue;
	}

	public void setMinOfValue(Double minOfValue) {
		this.minOfValue = minOfValue;
	}

	public void setMaxOfValue(Double maxOfValue) {
		this.maxOfValue = maxOfValue;
	}

	public Double getMinOfMinValue() {
		return minOfMinValue;
	}

	public Double getMaxOfMinValue() {
		return maxOfMinValue;
	}

	public Double getMinOfMaxValue() {
		return minOfMaxValue;
	}

	public Double getMaxOfMaxValue() {
		return maxOfMaxValue;
	}

	public void setMinOfMinValue(Double minOfMinValue) {
		this.minOfMinValue = minOfMinValue;
	}

	public void setMaxOfMinValue(Double maxOfMinValue) {
		this.maxOfMinValue = maxOfMinValue;
	}

	public void setMinOfMaxValue(Double minOfMaxValue) {
		this.minOfMaxValue = minOfMaxValue;
	}

	public void setMaxOfMaxValue(Double maxOfMaxValue) {
		this.maxOfMaxValue = maxOfMaxValue;
	}
}
