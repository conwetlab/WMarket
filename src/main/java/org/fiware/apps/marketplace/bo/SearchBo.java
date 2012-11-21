package org.fiware.apps.marketplace.bo;

import org.fiware.apps.marketplace.model.SearchResult;


public interface SearchBo {
	public SearchResult searchByKeyword(String searchstring);

}
