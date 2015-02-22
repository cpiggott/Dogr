package com.pathogenstudios.fitbark;

import java.util.regex.Pattern;

public class FitBarkOAuthConfig {
    public static final String APP_SERVER_DOMAIN = "app.fitbark.com";
    public static final String APP_SERVER = "http://" + APP_SERVER_DOMAIN + "/";
    public static final String API_URL_PREFIX = APP_SERVER + "api/";
    public static final String OAUTH_URL_PREFIX = APP_SERVER + "oauth/";

    public static final String AUTHORIZE_TOKEN_URL_REGEX = "^https?://" + APP_SERVER_DOMAIN.replace(".", "\\.") + "/oauth/authorize/([a-z0-9]+)$";

    private String oAuthAppId;
    private String oAuthSecret;
    private String oAuthCallbackUrl;
    private String oAuthLoginUrl;

    public FitBarkOAuthConfig(String appId, String secret, String callbackUrl) {
        this.oAuthAppId = appId;
        this.oAuthSecret = secret;
        this.oAuthCallbackUrl = callbackUrl;

        oAuthLoginUrl = OAUTH_URL_PREFIX + "authorize?client_id=" + ApiUtils.UrlEncode(oAuthAppId) + "&redirect_uri=" + ApiUtils.UrlEncode(oAuthCallbackUrl) + "&response_type=code";
    }

    public String getOAuthAppId() { return oAuthAppId; }
    public String getOAuthSecret() { return oAuthSecret; }
    public String getOAuthCallbackUrl() { return oAuthCallbackUrl; }
    public String getLoginUrl() { return oAuthLoginUrl; }
}
