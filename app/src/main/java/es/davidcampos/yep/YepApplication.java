package es.davidcampos.yep;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by David on 24/07/2015.
 */
public class YepApplication extends Application {
    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "s6QKuoGfI0L2v3PnzMXEm6DS5QzAEfJ00XRgMtnA", "sY6SWL1PZLkVdUeKBXWfWerd98wdyUDDOZXb1W1Y");

    }
}
