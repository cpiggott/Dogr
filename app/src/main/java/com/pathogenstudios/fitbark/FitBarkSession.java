package com.pathogenstudios.fitbark;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

// Currently missing API endpoints:
// user_relations
// activity_series
// activity_totals
// time_breakdown
// similar_activity_avg
// daily_goal (read and write)
public class FitBarkSession {
    private static final String TAG = "FitBarkSession";

    private String oAuthAccessToken;

    private static final String badDataExceptionMessage = "The FitBark servers responded with malformed data.";

    public FitBarkSession(FitBarkOAuthConfig oAuthConfig, String authorizationToken) {
        // Get the access token from the authorizationToken:
        oAuthAccessToken = getAccessTokenFromAuthorizationToken(oAuthConfig, authorizationToken);
    }

    private static String getAccessTokenFromAuthorizationToken(FitBarkOAuthConfig oAuthConfig, String authorizationToken) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(FitBarkOAuthConfig.OAUTH_URL_PREFIX + "token");
        final String authErrorMessage = "Error while authenticating with FitBark.";

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("client_id", oAuthConfig.getOAuthAppId()));
        postParameters.add(new BasicNameValuePair("client_secret", oAuthConfig.getOAuthSecret()));
        postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParameters.add(new BasicNameValuePair("redirect_uri", oAuthConfig.getOAuthCallbackUrl()));
        postParameters.add(new BasicNameValuePair("code", authorizationToken));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
        } catch (UnsupportedEncodingException ex) {
            throw new FitBarkApiException(authErrorMessage, ex);
        }

        String rawJsonString;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            rawJsonString = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Access token request response: " + response.getStatusLine() + ", " + rawJsonString);
        } catch (IOException ex) {
            throw new FitBarkApiException(authErrorMessage, ex);
        }

        String accessToken;
        try {
            JSONObject json = new JSONObject(rawJsonString);
            accessToken = json.getString("access_token");
            Log.i(TAG, "Access token: " + accessToken);
        } catch (JSONException ex) {
            throw new FitBarkApiException(authErrorMessage, ex);
        }
        return accessToken;
    }

    private JSONObject queryApi(String endPoint) {
        HttpClient http = new DefaultHttpClient();
        HttpGet request = new HttpGet(FitBarkOAuthConfig.API_URL_PREFIX + endPoint);
        request.setHeader("Authorization", "Bearer " + oAuthAccessToken);

        String jsonString;
        StatusLine httpStatus;
        try {
            HttpResponse response = http.execute(request);
            jsonString = EntityUtils.toString(response.getEntity());
            httpStatus = response.getStatusLine();
            Log.i(TAG, "API call response for " + endPoint + ": " + httpStatus + ", '" + jsonString + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new FitBarkApiException("A connection occurred while accessing the FitBark servers.", ex);
        }

        try {
            return new JSONObject(jsonString);
        } catch (JSONException ex) {
            String exceptionMessage = badDataExceptionMessage;
            if (httpStatus.getStatusCode() != 200) {
                exceptionMessage += " (HTTP Status = " + httpStatus.getStatusCode() + ")";
            }
            throw new FitBarkApiException(exceptionMessage, ex);
        }
    }

    public User getUserInfo() {
        try {
            return new User(queryApi("user").getJSONObject("user"));
        } catch (JSONException ex) {
            throw new FitBarkApiException(badDataExceptionMessage, ex);
        }
    }

    public Dog getDog(int id) {
        try {
            return new Dog(queryApi("dog/" + id).getJSONObject("dog"));
        } catch (JSONException ex) {
            throw new FitBarkApiException(badDataExceptionMessage, ex);
        }
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> ret = new ArrayList<Dog>();

        //TODO: We currently discard the relationship between the user and each dog. We might want ot provide a way to get this information.
        try {
            JSONArray relations = queryApi("dog_relations").getJSONArray("dog_relations");
            for (int i = 0; i < relations.length(); i++) {
                ret.add(new Dog(relations.getJSONObject(i).getJSONObject("dog")));
            }
        } catch (JSONException ex) {
            throw new FitBarkApiException(badDataExceptionMessage, ex);
        }

        return ret;
    }
}
