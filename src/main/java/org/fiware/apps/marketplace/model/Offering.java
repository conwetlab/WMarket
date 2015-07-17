package org.fiware.apps.marketplace.model;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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


import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.fiware.apps.marketplace.utils.xmladapters.DescriptionXMLAdapter;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

@Entity
@Table(name = "offerings", uniqueConstraints = { @UniqueConstraint(columnNames = { "described_in", "uri" }) })
@XmlRootElement(name = "offering")
@IgnoreMediaTypes("application/*+json")
public class Offering extends ReviewableEntity {

	private String name;
	private String displayName;
	private String uri;
	private String description;
	private String version;
	private Description describedIn;
	private String imageUrl;
	private int views;
		
	// Price Plans & Services
	private Set<PricePlan> pricePlans;
	private Set<Service> services;
	
	// Offering categories depends on the attached services
	private Set<Category> categories;
	
	private List<User> usersBookmarkedMe;
	private List<ViewedOffering> usersViewedMe;

	@XmlID
	@XmlAttribute 
	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	@Column(name = "display_name")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@XmlAttribute
	@Column(name = "uri")
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@XmlElement
	@Column(name = "description", length = 1000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement
	@Column(name = "version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	@XmlElement
	@XmlJavaTypeAdapter(DescriptionXMLAdapter.class)
	@ManyToOne(optional = false)
	@JoinColumn(name = "described_in", nullable = false)
	public Description getDescribedIn() {
		return describedIn;
	}

	public void setDescribedIn(Description describedIn) {
		this.describedIn = describedIn;
	}

	@XmlElement
	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@XmlElement
	@Column(name = "views", columnDefinition = "int default 0")
	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	@XmlElement(name = "pricePlan")
    @JsonProperty("pricePlans")
	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	public Set<PricePlan> getPricePlans() {
		return pricePlans;
	}

	public void setPricePlans(Set<PricePlan> pricePlans) {
		this.pricePlans = pricePlans;
	}
	
	@XmlElement(name = "service")
	@JsonProperty("services")
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)	// Not many services are supposed to be attached
	@JoinTable(name = "offerings_services", 
			joinColumns = {@JoinColumn(name = "offering_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name = "service_id", referencedColumnName = "id")})
	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
	}
	
	@XmlElement(name = "category")
	@JsonProperty("categories")
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "offerings_categories",
			joinColumns = {@JoinColumn(name = "offering_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name = "category_id", referencedColumnName = "id")})
	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	
	// So cascade will work
	@XmlTransient
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "bookmarks", 
    		joinColumns = {@JoinColumn(name = "offering_id", referencedColumnName = "id")},
    		inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
	public List<User> getUsersBookmarkedMe() {
		return usersBookmarkedMe;
	}

	public void setUsersBookmarkedMe(List<User> usersBookmarkedMe) {
		this.usersBookmarkedMe = usersBookmarkedMe;
	}
	
	@XmlTransient
	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<ViewedOffering> getUsersViewedMe() {
		return usersViewedMe;
	}

	public void setUsersViewedMe(List<ViewedOffering> usersViewedMe) {
		this.usersViewedMe = usersViewedMe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((describedIn == null) ? 0 : describedIn.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		} 
		
		if (obj instanceof Offering) {
			Offering other = (Offering) obj;
			
			// Avoid null pointer exceptions...
			if (this.uri == null || this.describedIn == null) {
				return false;
			}
			
			if (this.uri.equals(other.uri) && this.describedIn.equals(other.describedIn)) {
				return true;
			}
		}
						
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("%s (Description: %s, Store: %s)", name, describedIn.getName(), 
				describedIn.getStore().getName());
	}

}
