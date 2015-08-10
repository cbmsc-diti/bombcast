package igarape.cbmsc.bombcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import igarape.cbmsc.bombcast.utils.BatteryUtils;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.utils.LocationUtils;

/**
 * Created by FCavalcanti on 7/3/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = AlarmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive...");
        if(BatteryUtils.getSingletonInstance().shouldUpload()){
            final Location lastKnownLocation = Globals.getLastKnownLocation();
            if(null != lastKnownLocation){
                LocationUtils.sendLocation(context, Globals.getUserLogin(context), lastKnownLocation);
            }else{
                Log.d(TAG, "no location found.");
            }
        }else{
            Log.d(TAG, "low battery.");
        }
    }
}
