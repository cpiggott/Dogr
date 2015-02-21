package com.pathogenstudios.dogr.dogr;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Hayden on 2/21/2015.
 */
public class Dogr extends Application {
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Vt248hZtJxdO8Cn0k4rE09Epzy9h9rBUPGbc1CDt", "suqa1JlBsFi3FiEoA6cBu2Q5cqwJVFBKPdIrxVmr");
    }
}
