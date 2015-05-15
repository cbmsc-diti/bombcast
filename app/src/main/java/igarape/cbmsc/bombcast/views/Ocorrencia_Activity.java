package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.BackgroundVideoRecorder;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;


/**
 * Created by barcellos on 24/02/15.
 */
public class Ocorrencia_Activity extends Activity {

    protected boolean parar = false;
    protected String VtrMonitorada = Globals.getVtrSelecionada();
    protected String TelefoneCmt = Globals.getTelefoneCmt();
    protected String[] Endereco;
    protected String[] IO;
    protected String UrlJS;
    protected String ServidorSelecionado = Globals.getServidorSelecionado();
    public String retornoJS = "";
    TextView tv_endereco;
    protected PowerManager.WakeLock mWakeLock;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_ocorrencia);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        this.mWakeLock.acquire();

        Processo meu = new Processo(getBaseContext());
        meu.execute();

        final Button btn_j10 = (Button) findViewById(R.id.btn_j10);
        btn_j10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                parar = true;

                String LatOcorrencia = Globals.getLatitude();
                String LngOcorrencia = Globals.getLongitude();


                findViewById(R.id.btn_j10).setEnabled(false);
                findViewById(R.id.btn_j11).setEnabled(true);


                IO = Endereco[2].split("\\:");

                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j10=1&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado+"&lat_o="+LatOcorrencia+"&lng_o="+LngOcorrencia+"&u="+Globals.getUserName()+"&fn="+TelefoneCmt;


                try {

                  retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        final Button btn_j11 = (Button) findViewById(R.id.btn_j11);
        btn_j11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                findViewById(R.id.btn_j11).setEnabled(false);
                findViewById(R.id.btn_j10_i).setEnabled(true);




                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j11=1&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado;


                try {

                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        });
        final Button btn_j10_i = (Button) findViewById(R.id.btn_j10_i);
        btn_j10_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

                builder.setMessage("VERIFIQUE SE O HOSPITAL PODE RECEBER O PACIENTE ANTES DE RESPONDER. \n \n ESSE HOSPITAL RECUSOU ATENDIMENTO À VITIMA? \n (se não possuir uma vítima nessa viatura, ou não estar em um hospital, clique em NÃO.)")
                        .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){

                                findViewById(R.id.recBall).setVisibility(View.INVISIBLE);
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11_i).setEnabled(true);

                                Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                stopService(intent);

                                IO = Endereco[2].split("\\:");

                                String LatLocalIntermediario = Globals.getLatitude();
                                String LngLocalIntermediario = Globals.getLongitude();

                                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j10i=1&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado+"&lat_i="+LatLocalIntermediario+"&lng_i="+LngLocalIntermediario;


                                try {

                                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        })
                        .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                IO = Endereco[2].split("\\:");

                                String LatLocalRecusa = Globals.getLatitude();
                                String LngLocalRecusa = Globals.getLongitude();

                              /*  Geocoder geocoder = new Geocoder(Ocorrencia_Activity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(Globals.getLatAlteracao(), Globals.getLngAlteracao() , 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getAddressLine(1);

                                String EnderecoRecusa = address +"," + city;

                                EnderecoRecusa = Normalizer.normalize(EnderecoRecusa, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                */

                                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j10i=0&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado+"&lat_r="+LatLocalRecusa+"&lng_r="+LngLocalRecusa;

                                try {

                                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                AlertDialog alert = builder.create();

                alert.show();


            }
        });



        final Button btn_j11_i = (Button) findViewById(R.id.btn_j11_i);
        btn_j11_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

                builder.setMessage("A MACA RETRÁTIL DESSA VIATURA FICOU RETIDA NESSE HOSPITAL? (caso não possua maca, ou não esteja em um hospital clique em NÃO.)")
                       .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {


                           }
                       })
                        .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                IO = Endereco[2].split("\\:");

                                String LatLocalMaca = Globals.getLatitude();
                                String LngLocalMaca = Globals.getLongitude();
/*
                                Geocoder geocoder = new Geocoder(Ocorrencia_Activity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(Globals.getLatAlteracao(), Globals.getLngAlteracao(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getAddressLine(1);

                                String EnderecoMaca = address +"," + city;

                                EnderecoMaca = Normalizer.normalize(EnderecoMaca, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
*/

                                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j11i=0&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado+"&lat_m="+LatLocalMaca+"&lng_m="+LngLocalMaca;

                                try {

                                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();

                findViewById(R.id.btn_j11_i).setEnabled(false);

                findViewById(R.id.recBall).setVisibility(View.VISIBLE);


                IO = Endereco[2].split("\\:");

                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j11i=1&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado;


                try {

                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        final Button btn_j12 = (Button) findViewById(R.id.btn_j12);
        btn_j12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                stopService(intent);
                findViewById(R.id.recBall).setVisibility(View.INVISIBLE);

                IO = Endereco[2].split("\\:");

                UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast.php?j12=1&nr_vtr="+VtrMonitorada+"&io="+IO[1]+"&h="+ServidorSelecionado;


                try {

                    retornoJS = ConexaoHttpClient.executaHttpGet(UrlJS);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                parar = false;
                Processo meu = new Processo(getBaseContext());
                meu.execute();

                findViewById(R.id.btn_j10).setEnabled(false);
                findViewById(R.id.btn_j11).setEnabled(false);
                findViewById(R.id.btn_j10_i).setEnabled(false);
                findViewById(R.id.btn_j11_i).setEnabled(false);
                findViewById(R.id.btn_j12).setEnabled(false);
                findViewById(R.id.btn_mapa_ocorrencia).setEnabled(false);
                findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(false);

                tv_endereco.setText("Sem ocorrência para essa viatura no momento...");



            }
        });


        final Button btn_detalhes_ocorrencia = (Button) findViewById(R.id.btn_detalhes_ocorrencia);
        btn_detalhes_ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ocorrencia_Activity.this, ListaDetalhesActivity.class);
                startActivity(intent);

            }
        });


        final Button btn_mapa_ocorrencia = (Button) findViewById(R.id.btn_mapa_ocorrencia);
        btn_mapa_ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ocorrencia_Activity.this, MapaOcorrenciaActiviy.class);
                startActivity(intent);

            }
        });




    }



    public class Processo extends AsyncTask<String, String, String> {

        public String retornoHttp= "";


        private Context context;

        public Processo(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... paramss) {

        try {

                    new Thread().sleep(4000);
                    retornoHttp = ConexaoHttpClient.executaHttpGet(Globals.SERVER_CBM + "rec_coord.bombcast.php?infos=1&nr_vtr=" + VtrMonitorada + "&h=" + ServidorSelecionado);

                } catch (Exception e) {
                    e.printStackTrace();
                retornoHttp = "ASC";
                }
            return retornoHttp;

        }



        @Override
        protected void onPostExecute(String result) {

           if((!retornoHttp.equals("0")) && (!retornoHttp.equals("ASC")) && (!retornoHttp.isEmpty()) && (parar==false)  ) {

               Globals.setMonitor(retornoHttp);

               Endereco = retornoHttp.split("\\|");
               tv_endereco = (TextView)findViewById(R.id.tv_endereco_ocorrencia);
               if(!Endereco[5].isEmpty()) {
                  tv_endereco.setText(Endereco[5]);
                   Globals.setEnderecoOcorrencia(Endereco[5]);
               }

               IO = Endereco[2].split("\\:");


               Globals.setId_Ocorrencia(IO[1]);



               Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startService(intent);
               findViewById(R.id.recBall).setVisibility(View.VISIBLE);

               findViewById(R.id.btn_j10).setEnabled(true);
               findViewById(R.id.btn_j12).setEnabled(true);

               findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(true);

               play(Ocorrencia_Activity.this, getAlarmSound());

               parar=true;

               AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

               builder.setTitle("PARAR ALARME.")
                           .setNeutralButton("PARAR", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id){

                                   player.stop();

                               }
                           });

                   AlertDialog alert = builder.create();

                   alert.show();

               findViewById(R.id.btn_mapa_ocorrencia).setEnabled(true);

           }else if((retornoHttp.equals("0"))&& (parar==false)){


               Processo meu = new Processo(getBaseContext());

               try {
                   new Thread().sleep(1000);


                   Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Monitorando "+VtrMonitorada, Toast.LENGTH_SHORT);
                   toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
                   toast.show();


               } catch (InterruptedException e) {
                   e.printStackTrace();
               }


               meu.execute();



           }else{
               final Timer t = new Timer();
               final AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

               play(Ocorrencia_Activity.this, getAlarmSound2());

               builder.setTitle("CONEXÃO PERDIDA.")
                       .setNeutralButton("RETOMAR CONEXÃO", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id){

                               t.cancel();
                               player.stop();


                               Processo meu = new Processo(getBaseContext());
                               meu.execute();

                           }
                       });

               final AlertDialog alert = builder.create();

              try {
                  alert.show();

                  try{

                      t.schedule(new TimerTask() {
                          public void run() {
                              alert.dismiss();
                              Processo meu = new Processo(getBaseContext());
                              meu.execute();

                              t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report

                          }
                      }, 35000);
                    }catch(Exception e){
                      e.printStackTrace();
                  }


              } catch (Exception e) {
                e.printStackTrace();
                player.stop();
              }



           }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

    private MediaPlayer player;

    protected void play(Context context, Uri alert) {

        player = new MediaPlayer();

        try {

            player.setDataSource(context, alert);

            final AudioManager audio = (AudioManager) context

                    .getSystemService(Context.AUDIO_SERVICE);


                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                player.setVolume(20, 20);
                player.prepare();

                audio.isSpeakerphoneOn();

                player.start();

        } catch (IOException e) {


        }

    }
    private Uri getAlarmSound() {

        Uri alertSound = Uri.parse("android.resource://igarape.cbmsc.bombcast/"+R.raw.alarme_001);

        if (alertSound == null) {

            alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);

            if (alertSound == null) {

                alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            }

        }

        return alertSound;

    }

    private Uri getAlarmSound2() {

        Uri alertSound = Uri.parse("android.resource://igarape.cbmsc.bombcast/"+R.raw.alarme_002);

        if (alertSound == null) {

            alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);

            if (alertSound == null) {

                alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            }

        }

        return alertSound;

    }

    @Override
    protected void onDestroy() {

        Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stopService(intent);

        this.mWakeLock.release();
        parar=true;
        super.onDestroy();
    }

}