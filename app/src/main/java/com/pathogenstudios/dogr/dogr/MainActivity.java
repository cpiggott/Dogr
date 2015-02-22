package com.pathogenstudios.dogr.dogr;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        saveLocation();
    }

    @Override
    protected void onStart(){
        super.onStart();
        saveLocation();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 3) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            UpdateUserFragment.newInstance(position + 1)).commit();
        } else if (position == 4) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container,
                                MatchesFragment.newInstance(position + 1)).commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = "Update User";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_logout) {
            logoutCurrentUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutCurrentUser() {
        currentUser = ParseUser.getCurrentUser();
        Intent intent = new Intent(this, LoginActivity.class);
        if (currentUser != null) {
            currentUser.logOut();
            ParseObject.unpinAllInBackground();
            Toast.makeText(this, "User " + currentUser.getUsername() + " logged out", Toast.LENGTH_LONG).show();
        }
        startActivity(intent);
        finish();
    }

    //Obtains the current location from the phone
    private Location obtainLocation(Boolean showToast) {

        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if (bestLocation == null) {
            sendToast("Couldn't get your location, defaulting to Kansas City.");
            bestLocation = new Location("null");
            bestLocation.setLatitude(39.1);
            bestLocation.setLongitude(-94.58);
        }

        if (showToast)
            sendToast("Location is " + bestLocation.getLatitude() + ", "
                    + bestLocation.getLongitude() + ".");
        return bestLocation;
    }

    private void sendToast(String message) {
        CharSequence text = (CharSequence) message;
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    //Saves the location of the current user to Parse.
    private void saveLocation(){
        currentUser = ParseUser.getCurrentUser();
        Location currentLocation = obtainLocation(false);
        ParseGeoPoint parseLocation = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        currentUser.put("lastKnownLocation", parseLocation);
        currentUser.saveInBackground();
    }

}
