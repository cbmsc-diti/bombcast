package igarape.cbmsc.bombcast.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.view.WindowManager;

import java.io.IOException;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.views.LoginActivity;

/**
 * Created by barcellos on 24/11/15.
 */
public class RadioOnlineService extends Service{

    protected MediaPlayer player = null;;
    protected boolean isPlaying;
    protected int mId = 6;
    protected Context context;
    protected String caminho;
    protected PendingIntent pendingIntent;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {

        context = this.getApplicationContext();
        player = new MediaPlayer();
        Intent intentNotification = new Intent(context,StopService.class);
        pendingIntent = PendingIntent.getActivity(getBaseContext(), mId,intentNotification , PendingIntent.FLAG_UPDATE_CURRENT);
        // Start foreground service to avoid unexpected kill
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Rádio Online")
                .setContentIntent(pendingIntent)
                .setContentText("Ouvindo a comunicação de " + Globals.getNomeServidorRadioSelecionado())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(true)
                        .build();


        startForeground(mId, notification);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        caminho = Globals.getServidorRadioSelecionado();



        new MediaPrepareTask().execute(null, null, null);

    }
    @Override
    public void onDestroy() {
       releasePlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void releasePlayer() {
        try {
            if (player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
        } catch (IllegalStateException i) {
        }
    }


    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {
       @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                player.setDataSource(caminho);
                player.prepare();
                if (player != null) {
                    player.start();
                    isPlaying = true;
                } else {
                    releasePlayer();
                    return false;
                }

            }catch (Exception e){
                e.printStackTrace();
                releasePlayer();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }
}
