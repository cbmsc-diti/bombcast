package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.BackgroundVideoRecorder;
import igarape.cbmsc.bombcast.service.LocationService;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

public class Ocorrencia_Activity extends Activity {

    public SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
    List<NameValuePair> params = new ArrayList<>();
    protected PowerManager.WakeLock mWakeLock;
    protected boolean parar = false;
    protected boolean j11 = true;
    protected String VtrMonitorada = Globals.getVtrSelecionada();
    protected String TelefoneCmt = Globals.getTelefoneCmt();
    protected String ServidorSelecionado = Globals.getServidorSelecionado();
    protected String count = "not";
    protected String UrlJS;
    protected String[] IO;
    protected String[] detalhes_ocorrencia;
    public TextView tv_tipo_oc;
    public TextView tv_endereco;
    public String LatOcorrencia;
    public String LngOcorrencia;
    public String retornoJS = "";
    public String vf;
    private Intent intent;
    private String[] endereco_final;
    protected KeyguardManager.KeyguardLock lock;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ocorrencia);
        super.onCreate(savedInstanceState);


        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        this.mWakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast2007.php";

        params.add(new BasicNameValuePair("nr_vtr", VtrMonitorada));
        params.add(new BasicNameValuePair("h", ServidorSelecionado));
        params.add(new BasicNameValuePair("fn",TelefoneCmt));
        params.add(new BasicNameValuePair("u", Globals.getUserName()));
        params.add(new BasicNameValuePair("infos","1"));
        params.add(new BasicNameValuePair("status","ON"));
        params.add(new BasicNameValuePair("log","1"));
        params.add(new BasicNameValuePair("lat_vtr",Globals.getLatitude()));
        params.add(new BasicNameValuePair("lng_vtr",Globals.getLongitude()));

       /* HistoryUtils.registerHistory(getApplicationContext(), State.LOGGED, State.MONITOR, Globals.getUserName());

        startAlarmReceiver();
*/
        Intent intent2 = new Intent(Ocorrencia_Activity.this, LocationService.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent2);

        Processo meu = new Processo(getBaseContext());
        meu.execute();

        final Button btn_j9 = (Button) findViewById(R.id.btn_j9);
        btn_j9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parar = true;

                params.add(new BasicNameValuePair("jota", "j9"));
                params.add(new BasicNameValuePair("hr_j9", s.format(new Date())));

                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.btn_j9).setEnabled(false);
                findViewById(R.id.btn_j10).setEnabled(true);
                findViewById(R.id.btn_j11).setEnabled(true);
                //###########################################################

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {
                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                            vf = "0";

                        } catch (Exception e) {
                            e.printStackTrace();
                            vf = "1";

                        }
                        return vf;
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        super.cancel(true);
                    }
                }.execute();
            }
        });

        final Button btn_j10 = (Button) findViewById(R.id.btn_j10);
        btn_j10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parar = true;

                LatOcorrencia = Globals.getLatitude();
                LngOcorrencia = Globals.getLongitude();

                params.set(11,new BasicNameValuePair("jota","j10"));
                params.add(new BasicNameValuePair("lat_o",LatOcorrencia));
                params.add(new BasicNameValuePair("lng_o", LngOcorrencia));
                params.add(new BasicNameValuePair("hr_j10", s.format(new Date())));

                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.btn_j10).setEnabled(false);
                findViewById(R.id.btn_j09_i).setEnabled(true);
                //###########################################################

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {
                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                            vf = "0";
                        } catch (Exception e) {
                            e.printStackTrace();
                            vf = "1";
                        }
                        return vf;
                    }
                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        super.cancel(true);
                    }
                }.execute();
            }
        });

        final Button btn_j9_i = (Button) findViewById(R.id.btn_j09_i);
        btn_j9_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                params.set(11, new BasicNameValuePair("jota", "j9_i"));
                params.add(new BasicNameValuePair("hr_j9_i", s.format(new Date())));



                Intent intent = new Intent(Ocorrencia_Activity.this, ListaHospitaisActivity.class);
                startActivity(intent);

                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.btn_j09_i).setEnabled(false);
                findViewById(R.id.btn_j10_i).setEnabled(true);
                //###########################################################

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {
                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                            vf = "0";
                        } catch (Exception e) {
                            e.printStackTrace();
                            vf = "1";
                        }

                        return vf;
                    }
                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        super.cancel(true);
                    }
                }.execute();
            }

        });

        final Button btn_j10_i = (Button) findViewById(R.id.btn_j10_i);
        btn_j10_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
                j11 = false;

                builder.setMessage(getString(R.string.texto_recusa_vitima))
                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                String LatLocalIntermediario = Globals.getLatitude();
                                String LngLocalIntermediario = Globals.getLongitude();

                                params.set(11, new BasicNameValuePair("jota", "j10_i_n"));
                                params.add(new BasicNameValuePair("lat_i",LatLocalIntermediario));
                                params.add(new BasicNameValuePair("lng_i",LngLocalIntermediario));
                                params.add(new BasicNameValuePair("hr_j10_i_n", s.format(new Date())));

                                //ALTERNA BOTOES NA TELA ####################################
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11).setEnabled(true);
                                //###########################################################

                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... unused) {
                                        try {
                                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                                            vf = "0";

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            vf = "1";
                                        }
                                        return vf;
                                    }
                                    @Override
                                    protected void onPostExecute(String aVoid) {
                                        super.onPostExecute(aVoid);
                                        super.cancel(true);
                                    }
                                }.execute();
                            }
                        })
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String LatLocalRecusa = Globals.getLatitude();
                                String LngLocalRecusa = Globals.getLongitude();

                                params.set(11, new BasicNameValuePair("jota", "j10_i_s"));
                                params.add(new BasicNameValuePair("lat_r",LatLocalRecusa));
                                params.add(new BasicNameValuePair("lng_r",LngLocalRecusa));
                                params.add(new BasicNameValuePair("hr_j10_i_s", s.format(new Date())));


                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... unused) {
                                        try {
                                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                                            vf = "0";
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            vf = "1";
                                        }
                                        return vf;
                                    }
                                    @Override
                                    protected void onPostExecute(String aVoid) {
                                        super.onPostExecute(aVoid);
                                        super.cancel(true);
                                    }
                                }.execute();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        final Button btn_j11 = (Button) findViewById(R.id.btn_j11);
        btn_j11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!j11) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

                    builder.setMessage(getString(R.string.texto_maca_retida))
                            .setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String LatLocalMaca = Globals.getLatitude();
                                    String LngLocalMaca = Globals.getLongitude();

                                    params.set(11, new BasicNameValuePair("jota", "j11_s"));
                                    params.add(new BasicNameValuePair("lat_m", LatLocalMaca));
                                    params.add(new BasicNameValuePair("lng_m", LngLocalMaca));
                                    params.add(new BasicNameValuePair("hr_j11_s", s.format(new Date())));


                                    new AsyncTask<Void, Void, String>() {

                                        ProgressDialog pDialog;
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            //ANTES DE EXECUTAR (JANELA)
                                            pDialog  = ProgressDialog.show(Ocorrencia_Activity.this, "J11", "Atualizando ocorrência, aguarde...", true);
                                        }
                                        @Override
                                        protected String doInBackground(Void... unused) {
                                            try {
                                                retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                                                vf = "0";

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                vf = "1";
                                            }
                                            return vf;
                                        }

                                        @Override
                                        protected void onPostExecute(String aVoid) {
                                            super.onPostExecute(aVoid);
                                            pDialog.dismiss();
                                            super.cancel(true);
                                        }
                                    }.execute();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                params.set(11, new BasicNameValuePair("jota", "j11_n"));
                params.add(new BasicNameValuePair("hr_j11_n", s.format(new Date())));





                new AsyncTask<Void, Void, String>() {

                    ProgressDialog pDialog;
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //ANTES DE EXECUTAR (JANELA)
                        pDialog  = ProgressDialog.show(Ocorrencia_Activity.this, "J11", "Atualizando ocorrência, aguarde...", true);
                    }
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {
                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                            vf = "0";

                        } catch (Exception e) {
                            e.printStackTrace();
                            vf = "1";
                        }
                        return vf;
                    }
                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        pDialog.dismiss();


                        //ALTERNA BOTOES NA TELA ####################################
                        findViewById(R.id.btn_j10).setEnabled(false);
                        findViewById(R.id.btn_j09_i).setEnabled(false);
                        findViewById(R.id.btn_j10_i).setEnabled(false);
                        findViewById(R.id.btn_j11).setEnabled(false);
                        findViewById(R.id.btn_j12).setEnabled(true);
                        //###########################################################

                        super.cancel(true);
                    }
                }.execute();
            }

        });

        final Button btn_j12 = (Button) findViewById(R.id.btn_j12);
        btn_j12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                params.set(11, new BasicNameValuePair("jota", "j12"));
                params.add(new BasicNameValuePair("hr_j12", s.format(new Date())));

                try {
                    Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stopService(intent);
                }catch( Exception e){
                    e.printStackTrace();
                }

                new AsyncTask<Void, Void, String>() {

                    ProgressDialog pDialog;
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //ANTES DE EXECUTAR (JANELA)
                        pDialog  = ProgressDialog.show(Ocorrencia_Activity.this, "J12", "Encerrando ocorrência, aguarde...", true);
                    }
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {
                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                            vf = "0";
                        } catch (Exception e) {
                            e.printStackTrace();
                            vf = "1";
                        }
                        return vf;
                    }
                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        if (vf.equals("1")){

                            //TIRA JANELA DE CARREGAMENTO DA TELA
                            pDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);

                            builder.setTitle(getString(R.string.problema))
                                    .setMessage("Clique em J12 somente conectado na WIFI da OBM!")
                                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        if (vf.equals("0")){

                            pDialog.dismiss();

                            //ALTERNA BOTOES NA TELA ####################################
                            findViewById(R.id.btn_play).setEnabled(false);
                            findViewById(R.id.btn_play).setVisibility(View.INVISIBLE);
                            findViewById(R.id.btn_stop).setEnabled(false);
                            findViewById(R.id.btn_stop).setVisibility(View.INVISIBLE);
                            findViewById(R.id.recBall).setVisibility(View.INVISIBLE);
                            findViewById(R.id.btn_j9).setEnabled(false);
                            findViewById(R.id.btn_j10).setEnabled(false);
                            findViewById(R.id.btn_j09_i).setEnabled(false);
                            findViewById(R.id.btn_j10_i).setEnabled(false);
                            findViewById(R.id.btn_j11).setEnabled(false);
                            findViewById(R.id.btn_j12).setEnabled(false);
                            findViewById(R.id.btn_mapa_ocorrencia).setEnabled(false);
                            findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(false);
                            //###########################################################

                            //MUDA TEXTO DA TELA, INSERE O NOME DA VIATURA
                            tv_endereco.setText(getString(R.string.msg_sem_ocorrencia));
                            tv_tipo_oc.setText(VtrMonitorada);


                            //VERIFICADOR DO PROCESSO MUDA PARA -->FALSE E RETORNA O MONITORAMENTO DAS OCORRENCIAS
                            parar = false;
                            Processo meu = new Processo(getBaseContext());
                            meu.execute();
                            //##################################################################
                        }
                        super.cancel(true);
                    }
                }.execute();
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
                if(isOnline()) {
                    intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + endereco_final[1]));
                    startActivity(intent);
                }else{
                    try{
                        Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Mapa disponível somente com a internet funcionando...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final Button btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.recBall).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_stop).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_play).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_stop).setEnabled(true);
                findViewById(R.id.btn_play).setEnabled(false);
                //###########################################################
            }
        });

        final Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                stopService(intent);


                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.recBall).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_stop).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_stop).setEnabled(false);
                findViewById(R.id.btn_play).setEnabled(true);
                //###########################################################
            }
        });
    }

        public class Processo extends AsyncTask<String, String, String> {

            public String retornoHttp= "";
            public SimpleDateFormat s_hora = new SimpleDateFormat("HH:mm");
            public Context context;
            String verifica_hora =  s_hora.format(new Date());

            public Processo(Context context) {
                this.context = context;
            }
        @Override
        protected String doInBackground(String... paramss) {


            try {
                new Thread();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try{
                retornoHttp = ConexaoHttpClient.executaHttpPost(Globals.SERVER_CBM + "rec_coord.bombcast.php", params);
                params.add(new BasicNameValuePair("log","3"));
            } catch (Exception e) {
                e.printStackTrace();
                retornoHttp = "ASC";
            }
            return retornoHttp;
        }
        @Override
        protected void onPostExecute(String result) {
            if(verifica_hora.equals("08:00")){

                AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
                builder.setTitle("Novo login!")
                        .setMessage("Faça o login com um membro da guarnição atual!")
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Ocorrencia_Activity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();

                alert.show();

                Processo.this.cancel(true);
            }else {

                if ((!retornoHttp.equals("0")) && (!retornoHttp.equals("ASC")) && (!retornoHttp.isEmpty()) && (!parar)) {

                    Globals.setMonitor(retornoHttp);

                    detalhes_ocorrencia = retornoHttp.split("\\|");

                    tv_endereco = (TextView) findViewById(R.id.tv_endereco_ocorrencia);
                    tv_tipo_oc = (TextView) findViewById(R.id.tv_desc_endereco_ocorrencia);

                    if (!detalhes_ocorrencia[5].isEmpty()) {
                        tv_endereco.setText(detalhes_ocorrencia[5]);
                        tv_tipo_oc.setText(detalhes_ocorrencia[0]);

                        Globals.setEnderecoOcorrencia(detalhes_ocorrencia[5]);
                    }
                    endereco_final = detalhes_ocorrencia[5].split(":");


                    IO = detalhes_ocorrencia[2].split(":");
                    try {
                        Globals.setId_Ocorrencia(IO[1]);
                        params.add(new BasicNameValuePair("io", IO[1]));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    //ALTERNA BOTOES NA TELA ####################################
                    findViewById(R.id.btn_j9).setEnabled(true);
                    findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(true);
                    findViewById(R.id.btn_play).setEnabled(true);
                    findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                    //###########################################################

                    play(Ocorrencia_Activity.this, getAlarmSound());

                    //VERIFICADOR MUDA PARA -->TRUE, MONITORAMENTO PARA A EXECUÇÃO
                    parar = true;
                    //############################################################


                    AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
                    builder.setTitle(getString(R.string.parar_alarme))
                            .setNeutralButton("PARAR", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    player.stop();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                    //BOTÃO DO MAPA DA OCORRENCIA É LIBERADO################
                    findViewById(R.id.btn_mapa_ocorrencia).setEnabled(true);
                    //######################################################

                    //CONTADOR RECEBE OK
                    count = "ok";
                    //#################

                    cancel(true);

                } else if ((retornoHttp.equals("0")) && (!parar)) {

                    Processo meu = new Processo(getBaseContext());

                    try {
                        new Thread();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Monitorando " + VtrMonitorada, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    meu.execute();
                } else {
                    try {
                        Processo meu = new Processo(getBaseContext());
                        meu.execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            cancel(true);
        }
    }

    private MediaPlayer player;

    protected void play(Context context, Uri alert) {
        player = new MediaPlayer();
        try {
            player.setDataSource(context, alert);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (IOException e) {e.printStackTrace();}
    }

    private Uri getAlarmSound() {
        return Uri.parse("android.resource://igarape.cbmsc.bombcast/"+R.raw.alarme_001);
    }

   /* private void startAlarmReceiver() {
        /**
         * AlarmManager...wakes every 15 sec.

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Globals.GPS_REPEAT_TIME, pending);
    }*/

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Ocorrencia_Activity.this, AlertDialog.THEME_HOLO_DARK);

        Resources res = getResources();
        alertDialog.setTitle("MONITORAMENTO!");
        alertDialog.setMessage("Deseja realmente parar o monitoramento?");

        alertDialog.setPositiveButton(res.getText(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Ocorrencia_Activity.this.finish();
            }
        });
        alertDialog.setNegativeButton(res.getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        try{
            Intent intent = new Intent(Ocorrencia_Activity.this, BackgroundVideoRecorder.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent);
        }catch( Exception e){
            e.printStackTrace();}

        try{
            Intent intent2 = new Intent(Ocorrencia_Activity.this, LocationService.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopService(intent2);
        }catch( Exception e){
            e.printStackTrace();}


        findViewById(R.id.btn_play).setEnabled(false);
        findViewById(R.id.btn_play).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_stop).setEnabled(false);
        findViewById(R.id.btn_stop).setVisibility(View.INVISIBLE);

        this.mWakeLock.release();
        parar=true;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    params.add(new BasicNameValuePair("status","OFF"));
                    params.add(new BasicNameValuePair("log","2"));
                    ConexaoHttpClient.executaHttpPost(Globals.SERVER_CBM + "rec_coord.bombcast.php",params);
                    //vf = "0";
                } catch (Exception e) {
                    e.printStackTrace();
                    vf = "1";
                }
                return vf;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.cancel(true);
            }
        }.execute();
        lock.reenableKeyguard();
        super.onDestroy();
    }

}