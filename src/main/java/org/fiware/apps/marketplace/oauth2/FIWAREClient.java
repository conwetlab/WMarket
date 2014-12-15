package org.fiware.apps.marketplace.oauth2;

import java.util.Date;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.SignatureType;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;


public class FIWAREClient extends BaseOAuth20Client<FIWAREProfile>{
	
	// To store users information
	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();	
	private UserBo userBo = (UserBo) context.getBean("userBo");

	private String scopeValue = "";

	@Override
	protected void internalInit() {
		super.internalInit();

		this.scopeValue = "";
		this.service = new ProxyOAuth20ServiceImpl(new FIWAREApi(), 
				new OAuthConfig(this.key, this.secret,
						this.callbackUrl,
						SignatureType.Header,
						this.scopeValue, null),
						this.connectTimeout, this.readTimeout, this.proxyHost,
						this.proxyPort, false, true);
	}

	@Override
	protected boolean requiresStateParameter() {
		return false;
	}

	@Override
	protected FIWAREProfile extractUserProfile(String body) {
		FIWAREProfile profile = new FIWAREProfile();
		
		final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "nickName"));
            for (final String attribute : new FIWAREAttributesDefinition().getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        
        // FIXME: By default, we are adding the default Role...
        profile.addRole("ROLE_USER");
        
        // User information should be stored in the local users table //
        User user;
        String username = (String) profile.getUsername();
        String email = (String) profile.getEmail();
        String displayName = (String) profile.getDisplayName();

        try {
        	// Modify the existing user
			user = userBo.findByName(username);
		} catch (UserNotFoundException e) {
			// Create a new user
			user = new User();
			user.setRegistrationDate(new Date());
		}
                
        // Set field values
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword("");	// Password cannot be NULL
        user.setDisplayName(displayName);
        
        // Save the new user
        userBo.save(user);
                
        return profile;
	}

	@Override
	protected String getProfileUrl(Token arg0) {
		return "https://account.lab.fi-ware.org/user";
	}

	@Override
	protected boolean hasBeenCancelled(WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        
        // user has denied permissions
        if ("access_denied".equals(error)) {
            return true;
        } else {
            return false;
        }
	}

	@Override
	protected BaseClient<OAuthCredentials, FIWAREProfile> newClient() {
		FIWAREClient newClient = new FIWAREClient();
		return newClient;
	}
}
