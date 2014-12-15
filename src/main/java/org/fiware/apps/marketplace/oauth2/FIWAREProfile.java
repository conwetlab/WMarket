package org.fiware.apps.marketplace.oauth2;

import org.pac4j.oauth.profile.OAuth20Profile;

public class FIWAREProfile extends OAuth20Profile{

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getDisplayName() {
		return (String) this.getAttribute("displayName");
	}
	
	@Override
	public String getUsername() {
		return (String) this.getAttribute("nickName");
	}
	
	@Override
	public String getTypedId() {
		return super.getTypedId();
	}

}
