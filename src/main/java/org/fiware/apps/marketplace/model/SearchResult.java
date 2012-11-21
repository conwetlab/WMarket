package org.fiware.apps.marketplace.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchresults")
public class SearchResult {	

	private List<SearchResultEntry> searchresultentries;
	
	public SearchResult() {
		searchresultentries = new ArrayList<SearchResultEntry>();
	}

	@XmlElement
	public List<SearchResultEntry> getSearchresult() {
		return searchresultentries;
	}

	public void setSearchresult(List<SearchResultEntry> searchresult) {
		this.searchresultentries = searchresult;
	}
	
	public void addSearchResult(Service s, SearchResultEntryMatch match){
		boolean found = false;
		for(SearchResultEntry sre : searchresultentries){
			if(sre.getService().getId()==(s.getId())){
				sre.addMatch(match);
				found = true;
			}
		}
		if(!found){
			SearchResultEntry sre = new SearchResultEntry(s);
			sre.addMatch(match);
			searchresultentries.add(sre);
		}
	}

}
