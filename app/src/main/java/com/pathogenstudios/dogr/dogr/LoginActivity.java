package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.pathogenstudios.fitbark.Dog;
import com.pathogenstudios.fitbark.FitBarkApiException;
import com.pathogenstudios.fitbark.FitBarkLoginWebView;
import com.pathogenstudios.fitbark.FitBarkOAuthConfig;
import com.pathogenstudios.fitbark.FitBarkSession;
import com.pathogenstudios.fitbark.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private SharedPreferences preferences;
    private static final String cachedAccessTokenKey = "cachedFitBarkAccessToken";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        oAuthConfig = new FitBarkOAuthConfig(
                getString(R.string.fitBarkOAuthAppId),
                getString(R.string.fitBarkOAuthSecret),
                getString(R.string.fitBarkOAuthCallbackUrl)
        );

        // Try to log in using an existing access token:
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String cachedAccessToken = null;
        try {
            cachedAccessToken = preferences.getString(cachedAccessTokenKey, null);
        } catch (ClassCastException ex) {
        }

        if (cachedAccessToken != null) {
            showProgressDialog("Logging into FitBark...");
            new ResumeFitBarkSessionTask().execute(cachedAccessToken);
        }

        //ParseUser currentUser = ParseUser.getCurrentUser();
        //if (currentUser != null) {
        //    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        //    startActivity(mainIntent);
        //    finish();
        //}

        ImageView daLogo = (ImageView) this.findViewById(R.id.daLogo);
        daLogo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
    }

    //-------------------------------------
    // FitBark Login Stuff
    //-------------------------------------
    private FitBarkOAuthConfig oAuthConfig;

    private class FitBarkLoginWebViewClientImpl extends FitBarkLoginWebView {
        public FitBarkLoginWebViewClientImpl() {
            super(LoginActivity.this, oAuthConfig);
        }

        @Override
        public void onLoginSuccessful(String authorizationToken) {
            setVisibility(View.INVISIBLE);
            new FinishLoginTask().execute(authorizationToken);
        }
    }

    private class FinishLoginTask extends AsyncTask<String, Void, FitBarkSession> {
        protected FitBarkSession doInBackground(String... tokens) {
            if (tokens.length != 1) {
                throw new IllegalArgumentException("FinishLoginTask should only be called with a single token.");
            }

            FitBarkSession ret = new FitBarkSession(oAuthConfig, tokens[0]);
            User user = ret.getUserInfo();
            ArrayList<Dog> dogs = ret.getDogs();

            Log.i(TAG, "User: " + user.getName());
            Log.i(TAG, "Has " + dogs.size() + " dogs.");
            for (Dog dog : dogs) {
                Log.i(TAG, "    " + dog.getName());
            }

            if (ret != null) {
                doLoginFinished(ret, null);
            }

            return ret;
        }
    }

    private class ResumeFitBarkSessionTask extends AsyncTask<String, Void, FitBarkSession> {
        protected FitBarkSession doInBackground(String... tokens) {
            if (tokens.length != 1) {
                throw new IllegalArgumentException("ResumeFitBarkSessionTask should only be called with a single token.");
            }

            FitBarkSession ret = new FitBarkSession(oAuthConfig, tokens[0], true);
            User user;

            try {
                user = ret.getUserInfo();
            } catch (FitBarkApiException ex) {
                ret = null;
                user = null;
                showToast("Login failed. Please re-enter your FitBark credentials.");
            }

            if (ret != null) {
                doLoginFinished(ret, user);
            }

            return ret;
        }
    }

    private FitBarkLoginWebViewClientImpl webView;

    //-------------------------------------
    // Login Lifecycle Management
    //-------------------------------------
    private void showProgressDialog(String message) {
        hideProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void doLoginFinished(FitBarkSession session, User fitBarkUser) {
        // Save their access token
        preferences.edit().putString(cachedAccessTokenKey, session.getAccessToken()).apply();

        // Login to Parse
        if (fitBarkUser == null) {
            fitBarkUser = session.getUserInfo();
        }

        //TODO: Yeah, this is not secure at all.
        ParseUser parseUser = null;
        try {
            parseUser = ParseUser.logIn("user" + fitBarkUser.getId(), "mega_security" + fitBarkUser.getId());
        } catch (ParseException ex) {
            // Ignore since this exception can happen on login failure.
        }

        final String firstNameKey = "firstName";
        final String lastNameKey = "lastName";
        final String userBioKey = "userBio";

        // Register the user with Parse if they don't exist yet
        if (parseUser == null) {
            parseUser = new ParseUser();
            parseUser.setUsername("user" + fitBarkUser.getId());
            parseUser.setPassword("mega_security" + fitBarkUser.getId());

            parseUser.setEmail(fitBarkUser.getUsername());
            parseUser.put(firstNameKey, fitBarkUser.getFirstName());
            parseUser.put(lastNameKey, fitBarkUser.getLastName());
            parseUser.put(userBioKey, "New to Dogr!");
            try {
                parseUser.signUp();
            } catch (ParseException ex) {
                throw new FitBarkApiException("Couldn't log in.", ex);
            }
        }

        // Sync basic profile info:
        parseUser.setEmail(fitBarkUser.getUsername());
        parseUser.put(firstNameKey, fitBarkUser.getFirstName());
        parseUser.put(lastNameKey, fitBarkUser.getLastName());

        // Sync dogs:
        ArrayList<Dog> fitBarkDogs = session.getDogs();
        for (Dog fitBarkDog : fitBarkDogs) {
            parseUser.getRelation("dogs").add(SyncDog(fitBarkDog, parseUser));
        }

        // Save any changes made to the user:
        try {
            parseUser.save();
        } catch (ParseException ex) {
            throw new FitBarkApiException("Couldn't sync basic user info.", ex);
        }

        // Move to the main activity
        hideProgressDialog();
        startActivity(new Intent(this.getApplicationContext(), MainActivity.class));
        finish();
    }

    private ParseObject SyncDog(Dog fitBarkDog, ParseUser owner) {
        final String dogClassName = "Dog";

        final String fitBarkIdKey = "fitBarkId";

        ParseQuery<ParseObject> query = ParseQuery.getQuery(dogClassName);
        query.whereEqualTo(fitBarkIdKey, fitBarkDog.getId());
        query.setLimit(1);

        ParseObject result = null;
        try {
            result = query.getFirst();
        } catch (ParseException ex) {
            // Ignore this since it might just mean that there was not dog.
        }

        // Create a new dog if it is a new one
        if (result == null) {
            result = new ParseObject(dogClassName);
            result.put(fitBarkIdKey, fitBarkDog.getId());
        }

        // Sync or set data:
        String breed1 = fitBarkDog.getBreed1();
        String breed2 = fitBarkDog.getBreed2();
        if (breed1 != null) {
            result.put("breed1", breed1);
        } else {
            result.put("breed1", JSONObject.NULL);
        }
        if (breed2 != null) {
            result.put("breed2", breed2);
        } else {
            result.put("breed2", JSONObject.NULL);
        }
        result.put("dailyGoal", Integer.toString(fitBarkDog.getDailyGoal()));
        result.put("gender", fitBarkDog.getGenderString());
        result.put("name", fitBarkDog.getName());
        result.put("neutered", Boolean.toString(fitBarkDog.getIsNutered()));
        result.put("owner", owner);
        result.put("weight", Integer.toString(fitBarkDog.getWeight()));
        result.put("weightUnit", fitBarkDog.getWeightUnits());

        try {
            result.save();
        } catch (ParseException ex) {
            throw new FitBarkApiException("Couldn't sync FitBark dog data.", ex);
        }

        return result;
    }

    public void onLoginButtonPressed(View v) {
        // For testing only, clear all cookies:
        //CookieManager.getInstance().removeAllCookie();

        webView = new FitBarkLoginWebViewClientImpl();
        webView.setVisibility(View.VISIBLE);
        setContentView(webView);
    }
}
