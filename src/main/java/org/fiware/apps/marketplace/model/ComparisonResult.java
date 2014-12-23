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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "comparisonResults")
public class ComparisonResult {
	
	private ServiceContainer source;
	private List<ServiceContainer> targets;
	
	public static class ServiceContainer {
		private Integer id;
		private String name;
		private String offeringUri;
		private String offeringTitle;
		private String storeUrl;
		private String pricePlanUri;
		private String pricePlanTitle;
		private Double totalScore;
		private List<ComparisonResultAttribute> attributes;
		
		public ServiceContainer() {
			
		}
		
		public ServiceContainer(ServiceManifestation service, HashMap<String, ServiceAttributeType> typeMap) {
			this(service);
			for (int i = 0; i < service.getAttributes().size(); i++) {
				this.attributes.add(new ComparisonResultAttribute(service.getAttributes().get(i), typeMap, null, i));
			}
		}
		
		public ServiceContainer(ServiceManifestation service) {
			this.id = service.getId();
			this.name = service.getName();
			this.attributes = new ArrayList<ComparisonResultAttribute>();
			this.offeringUri = service.getOfferingUri();
			this.offeringTitle = service.getOfferingTitle();
			this.storeUrl = service.getStoreUrl();
			this.pricePlanUri = service.getPricePlanUri();
			this.pricePlanTitle = service.getPricePlanTitle();
		}
				
		@XmlAttribute
		public Integer getId() {
			return id;
		}

		@XmlAttribute
		public String getName() {
			return name;
		}

		@XmlAttribute
		public Double getTotalScore() {
			return totalScore;
		}

		@XmlElement(name="attribute")
		public List<ComparisonResultAttribute> getAttributes() {
			return attributes;
		}
		
		@XmlAttribute
		public String getOfferingUri() {
			return offeringUri;
		}

		@XmlAttribute
		public String getPricePlanUri() {
			return pricePlanUri;
		}

		@XmlAttribute
		public String getOfferingTitle() {
			return offeringTitle;
		}

		@XmlAttribute
		public String getPricePlanTitle() {
			return pricePlanTitle;
		}

		@XmlAttribute
		public String getStoreUrl() {
			return storeUrl;
		}

		public void addAttribute(ServiceAttribute attribute, HashMap<String, ServiceAttributeType> typeMap, Double score, Integer index) {
			this.attributes.add(new ComparisonResultAttribute(attribute, typeMap, score, index));
		}
		
		public void setTotalScore(Double totalScore) {
			this.totalScore = totalScore;
		}
	}
	
	public ComparisonResult() {
		targets = new ArrayList<ServiceContainer>();
	}
	
	public ComparisonResult(ServiceManifestation source, HashMap<String, ServiceAttributeType> typeMap) {
		this();
		this.source = new ServiceContainer(source, typeMap);
	}
	
	public void addTarget(ServiceManifestation target) {
		this.targets.add(new ServiceContainer(target));
	}
	
	public void addAttributeToLastTarget(ServiceAttribute attribute, HashMap<String, ServiceAttributeType> typeMap, Double score, Integer index) {
		this.targets.get(this.targets.size() - 1).addAttribute(attribute, typeMap, score, index);
	}
	
	public void addTotalScoreToLastTarget(Double totalScore) {
		this.targets.get(this.targets.size() - 1).setTotalScore(totalScore);
	}
	
	@XmlElement(name="source")
	public ServiceContainer getSource() {
		return source;
	}

	@XmlElementWrapper(name="targets")
	@XmlElement(name="target")
	public List<ServiceContainer> getTargets() {
		Collections.sort(targets, new Comparator<ServiceContainer>() {
			public int compare(ServiceContainer c1, ServiceContainer c2) {
				if (c1.getTotalScore() > c2.getTotalScore())
					return -1;
				if (c1.getTotalScore() < c2.getTotalScore())
					return 1;
				return 0;
			}
		});
		return targets;
	}
}
