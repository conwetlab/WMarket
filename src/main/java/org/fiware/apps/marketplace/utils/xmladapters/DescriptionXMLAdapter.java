package org.fiware.apps.marketplace.utils.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.MinifiedDescription;

public class DescriptionXMLAdapter extends XmlAdapter<MinifiedDescription, Description>{

	@Override
	public MinifiedDescription marshal(Description description) throws Exception {
		return new MinifiedDescription(description);
	}

	@Override
	public Description unmarshal(MinifiedDescription minDescription) throws Exception {
		return new Description(minDescription);
	}

}
