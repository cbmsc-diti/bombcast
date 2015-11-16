package igarape.cbmsc.bombcast.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.view.SurfaceHolder;

import java.io.IOException;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.views.Ocorrencia_Activity;

/**
 * Created by barcellos on 16/11/15.
 */
public class PlayerService  extends Service {
    private MediaPlayer player = null;;
    private boolean isPlaying;
    protected SurfaceHolder surfaceHolder;
    private int mId = 1;
    Context context;
    Uri caminho;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {

        // Start foreground service to avoid unexpected kill
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Alarme!")
                .setContentText("Alarme disparando")
                .setSmallIcon(R.drawable.ic_launcher)
                .build();
        startForeground(mId, notification);

        context = this.getApplicationContext();
        player = new MediaPlayer();

        caminho =  Uri.parse("android.resource://igarape.cbmsc.bombcast/" + R.raw.alarme_001);
        try {
            player.setDataSource(context, caminho);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setLooping(true);

        new MediaPrepareTask().execute(null, null, null);

    }
    @Override
    public void onDestroy() {
        try {
            releasePlayer();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        protected Boolean doInBackground(Void... voids) {
            if (player != null) {
                player.start();
                isPlaying = true;
            } else {
                player.release();
                isPlaying = false;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }

}