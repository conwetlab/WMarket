package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ServiceAttributeType {
	private String uri;
	private String baseTypeUri;
	private String broaderTypeUri;
	private List<String> narrowerTypeUris;
	private String preferedLabel;
	private String description;

	public ServiceAttributeType() {
		narrowerTypeUris = new ArrayList<String>();
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public String getBaseTypeUri() {
		return baseTypeUri;
	}

	public void setBaseTypeUri(String baseType) {
		this.baseTypeUri = baseType;
	}

	public String getBroaderTypeUri() {
		return broaderTypeUri;
	}

	public void setBroaderTypeUri(String broaderTypeUri) {
		this.broaderTypeUri = broaderTypeUri;
	}

	public List<String> getNarrowerTypeUris() {
		return narrowerTypeUris;
	}

	public void setNarrowerTypeUris(List<String> narrowerTypeUris) {
		this.narrowerTypeUris = narrowerTypeUris;
	}

	public void addNarrowerTypeUri(String narrowerTypeUri) {
		this.narrowerTypeUris.add(narrowerTypeUri);
	}

	public String getPreferedLabel() {
		return preferedLabel;
	}

	public void setPreferedLabel(String preferedLabel) {
		this.preferedLabel = preferedLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(uri);
		builder.append("\n\tbaseType:    " + baseTypeUri);
		builder.append("\n\tprefLabel:   " + preferedLabel);
		builder.append("\n\tdescription: " + description);
		builder.append("\n\tbroaderType: " + broaderTypeUri);
		for (String narrowerTypeUri : narrowerTypeUris) {
			builder.append("\n\narrowerType: " + narrowerTypeUri);
		}
		return builder.toString();
	}

}