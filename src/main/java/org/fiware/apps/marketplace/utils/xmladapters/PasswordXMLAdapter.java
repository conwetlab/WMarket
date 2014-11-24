package org.fiware.apps.marketplace.utils.xmladapters;

import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordXMLAdapter extends HiddenFieldsXMLAdapter{
	
	// Encoder must be the same in all the platform: use the bean
	private static final PasswordEncoder ENCODER = (PasswordEncoder) 
			ApplicationContextProvider.getApplicationContext().getBean("encoder");
	
	@Override
	public String unmarshal(String password) throws Exception {
		return ENCODER.encode(password);
	}

}
