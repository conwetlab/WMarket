package org.fiware.apps.marketplace.utils.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fiware.apps.marketplace.model.Description;

public class DescriptionXMLAdapter extends XmlAdapter<String, Description>{

	@Override
	public String marshal(Description description) throws Exception {
		return description.getName();
	}

	@Override
	public Description unmarshal(String value) throws Exception {
		// Not Needed
		return null;
	}

}
