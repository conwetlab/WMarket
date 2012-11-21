package org.fiware.apps.marketplace.model;

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
