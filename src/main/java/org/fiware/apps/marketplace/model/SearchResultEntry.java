package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchresult")
public class SearchResultEntry {
	
	private List<SearchResultEntryMatch> matches = new ArrayList<SearchResultEntryMatch>();	

	private Service service;
	private Store store;

	public SearchResultEntry (){
	}
	
	public SearchResultEntry (Service s){
		store = s.getStore();
		service = s;
	}
	
	@XmlElement
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@XmlElementWrapper
	@XmlElement(name = "match") 
	public List<SearchResultEntryMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<SearchResultEntryMatch> matches) {
		this.matches = matches;
	}
	
	@XmlElement
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	
	public void addMatch(SearchResultEntryMatch match){		
		matches.add(match);
	}
	
}
