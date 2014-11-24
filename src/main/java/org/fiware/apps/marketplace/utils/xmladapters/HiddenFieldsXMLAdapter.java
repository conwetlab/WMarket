package org.fiware.apps.marketplace.utils.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class HiddenFieldsXMLAdapter extends XmlAdapter<String, String>{
	
	@Override
	public String marshal(String val) throws Exception {
		return null;
	}
	
	@Override
	public String unmarshal(String val) throws Exception {
		return val;
	}

}
