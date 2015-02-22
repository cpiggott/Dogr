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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.pathogenstudios.fitbark.Dog;
import com.pathogenstudios.fitbark.FitBarkApiException;
import com.pathogenstudios.fitbark.FitBarkLoginWebView;
import com.pathogenstudios.fitbark.FitBarkOAuthConfig;
import com.pathogenstudios.fitbark.FitBarkSession;
import com.pathogenstudios.fitbark.User;

import java.util.ArrayList;
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
                doLoginFinished(ret);
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
                doLoginFinished(ret);
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

    private void doLoginFinished(FitBarkSession session) {
        // Save their access token
        preferences.edit().putString(cachedAccessTokenKey, session.getAccessToken()).apply();

        // Move to the main activity
        hideProgressDialog();
        startActivity(new Intent(this.getApplicationContext(), MainActivity.class));
        finish();
    }

    public void onLoginButtonPressed(View v) {
        // For testing only, clear all cookies:
        CookieManager.getInstance().removeAllCookie();

        webView = new FitBarkLoginWebViewClientImpl();
        webView.setVisibility(View.VISIBLE);
        setContentView(webView);
    }

    class LoginTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        Boolean mLoginFailed;

        public LoginTask() {
            progress = new ProgressDialog(LoginActivity.this);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progress.setCancelable(true);
            progress.setMessage("Attempting Log In");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
        }

        protected Void doInBackground(Void... params) {
            mLoginFailed = false;
            /*ParseUser.logInInBackground(mEmailAccount, mPassword, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("PAYBACK", "Logged user " + mEmailAccount + " in successfully.");
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed. Please try again, or use forgot password if necessary", Toast.LENGTH_LONG).show();
                        mLoginFailed = true;
                    }
                }
            });
            ParseUser currentUser = null;
            while( currentUser == null && mLoginFailed == false ) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                currentUser = ParseUser.getCurrentUser();
            }*/

            return null;
        }

        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            /*if( mLoginFailed == false ) {
                Intent mainIntent = new Intent(mActivity.getApplicationContext(), MainActivity.class);
                Log.d("PAYBACK", "In com.hgkdev.haydenkinney.payback.Login_User PostExecute");
                progress.dismiss();
                mActivity.startActivity(mainIntent);
                mActivity.finish();
            } else*/ {
                progress.dismiss();
            }
        }
    }
}
