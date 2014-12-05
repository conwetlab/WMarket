package org.fiware.apps.marketplace.oauth2;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

public class FIWAREApi extends DefaultApi20 {

	private static final String AUTHORIZATION_URL = "https://account.lab.fi-ware.org/authorize" + 
			"?client_id=%s&redirect_uri=%s&scope=%s&response_type=code";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://account.lab.fi-ware.org/token";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return String.format(AUTHORIZATION_URL, config.getApiKey(), 
				OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}
}
