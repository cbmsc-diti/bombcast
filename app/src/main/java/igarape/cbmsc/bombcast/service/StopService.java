package igarape.cbmsc.bombcast.service;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by barcellos on 17/11/15.
 */
public class StopService extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent intent = new Intent(StopService.this, PlayerService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent(StopService.this, LocationService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent(StopService.this, BackgroundVideoRecorder.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }



        finish();
    }
}
