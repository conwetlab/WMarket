package org.fiware.apps.marketplace.utils.xmladapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fiware.apps.marketplace.model.User;

public class UserXMLAdapter extends XmlAdapter<String, User>{

	@Override
	public User unmarshal(String value) throws Exception {
		// Not needed... 
		return null;
	}

	@Override
	public String marshal(User user) throws Exception {
		return user.getUserName();
	}



}
