package igarape.cbmsc.bombcast.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by barcellos on 17/11/15.
 */
public class StopService extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Intent intent3 = new Intent(getApplicationContext(), PlayerService.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent3);
        }catch( Exception e){
            e.printStackTrace();}
        try{
            Intent intent4 = new Intent(getApplicationContext(), RadioOnlineService.class);
            intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent4);
        }catch( Exception e){
            e.printStackTrace();}

        finish();
    }
}
