package igarape.cbmsc.bombcast;

import android.app.Application;
import android.content.res.Configuration;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

import igarape.cbmsc.bombcast.utils.FileUtils;

public class FireCastApplication extends Application {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.init();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64149555-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        Mint.initAndStartSession(FireCastApplication.this, "c58077d2");

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}