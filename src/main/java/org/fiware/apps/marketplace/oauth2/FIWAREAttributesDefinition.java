package org.fiware.apps.marketplace.oauth2;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

public class FIWAREAttributesDefinition extends OAuthAttributesDefinition {

	public static final String USER_NAME = "nickName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL = "email";

    public FIWAREAttributesDefinition() {
        addAttribute(DISPLAY_NAME, Converters.stringConverter);
        addAttribute(EMAIL, Converters.stringConverter);
        addAttribute(USER_NAME, Converters.stringConverter);
    }
}