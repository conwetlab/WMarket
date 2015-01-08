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