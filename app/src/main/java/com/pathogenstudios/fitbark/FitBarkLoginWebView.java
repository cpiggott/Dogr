package com.pathogenstudios.fitbark;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FitBarkLoginWebView extends WebView {
    private static final String TAG = "FitBarkLoginWebView";

    private class FitBarkLoginWebViewClient extends WebViewClient
    {
        private Pattern authorizeUrlRegex = Pattern.compile(FitBarkOAuthConfig.AUTHORIZE_TOKEN_URL_REGEX);

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "Going to URL: '" + url + "'");

            Matcher authorizeUrlMatch = authorizeUrlRegex.matcher(url);
            if (authorizeUrlMatch.find()) {
                onLoginSuccessful(authorizeUrlMatch.group(1));
            }

            view.loadUrl(url);
            return true;
        }
    }

    public FitBarkLoginWebView(Context context, FitBarkOAuthConfig oAuthConfig) {
        super(context);
        this.getSettings().setJavaScriptEnabled(true);
        this.setWebViewClient(new FitBarkLoginWebViewClient());
        this.loadUrl(oAuthConfig.getLoginUrl());
    }

    protected abstract void onLoginSuccessful(String authorizationToken);
}
