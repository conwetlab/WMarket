package org.fiware.apps.marketplace.utils.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fiware.apps.marketplace.model.Store;

public class StoreXMLAdapter extends XmlAdapter<String, Store>{

	@Override
	public Store unmarshal(String value) throws Exception {
		// Not needed... 
		return null;
	}

	@Override
	public String marshal(Store store) throws Exception {
		return store.getName();
	}

}
