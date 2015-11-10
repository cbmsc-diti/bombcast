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
import java.util.Timer;
import java.util.TimerTask;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.BackgroundVideoRecorder;
import igarape.cbmsc.bombcast.service.LocationService;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

public class Ocorrencia_Activity extends Activity {

    final String UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast2.php";
    final String UrlOcorrencia = Globals.SERVER_CBM + "rec_coord.bombcast2.php";
    final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
    final MyTimerTask myTask = new MyTimerTask(); // aqui instacia sua tarefa
    final Timer myTimer = new Timer(); // aqui instancia o agendador
    final String ServidorSelecionado = Globals.getServidorSelecionado();
    KeyguardManager.KeyguardLock lock;
    PowerManager.WakeLock mWakeLock;
    String[] detalhes_ocorrencia;
    String[] IO = new String[2];
    String VtrsMonitoradas = Globals.getVtrSelecionada();
    String VtrOc;
    String controlador;
    String retornoHttp;
    String LatOcorrencia;
    String LngOcorrencia;
    String vf;
    String confereJS= "inicio";
    String[] vtr_final;
    String retornoJS;
    Boolean j11 = true;
    List<NameValuePair> params = new ArrayList<>();
    Integer cont = 1;
    TextView tv_tipo_oc;
    TextView tv_endereco;
    Intent intent;
    String[] endereco_final;
    MediaPlayer player;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ocorrencia);
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        this.mWakeLock.acquire();
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();


        params.add(new BasicNameValuePair("nr_vtr", VtrsMonitoradas));
        params.add(new BasicNameValuePair("h", ServidorSelecionado));
        params.add(new BasicNameValuePair("u", Globals.getUserName()));
        params.add(new BasicNameValuePair("infos","1"));
        params.add(new BasicNameValuePair("status","ON"));
        params.add(new BasicNameValuePair("log","1"));
        params.add(new BasicNameValuePair("lat_vtr", Globals.getLatitude()));
        params.add(new BasicNameValuePair("lng_vtr", Globals.getLongitude()));
        params.add(new BasicNameValuePair("eo","nao"));


        //INICIA OS BOTOES######################################################
        final  Button btn_j9 = (Button) findViewById(R.id.btn_j9);
        final  Button btn_j10 = (Button) findViewById(R.id.btn_j10);
        final  Button btn_j9_i = (Button) findViewById(R.id.btn_j09_i);
        final  Button btn_j10_i = (Button) findViewById(R.id.btn_j10_i);
        final  Button btn_j11 = (Button) findViewById(R.id.btn_j11);
        final  Button btn_j12 = (Button) findViewById(R.id.btn_j12);
        final  Button btn_detalhes_ocorrencia = (Button) findViewById(R.id.btn_detalhes_ocorrencia);
        final  Button btn_mapa_ocorrencia = (Button) findViewById(R.id.btn_mapa_ocorrencia);
        final  Button btn_play = (Button) findViewById(R.id.btn_play);
        final  Button btn_stop = (Button) findViewById(R.id.btn_stop);

        btn_j9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J9();
            }
        });
        btn_j10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J10();
            }
        });
        btn_j9_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J9Intermediario();
            }

        });
        btn_j10_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J10Intermediario();
            }
        });
        btn_j11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J11();
            }
        });
        btn_j12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                J12();
            }
        });
        btn_detalhes_ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {detalhesOcorrencia();
            }
        });
        btn_mapa_ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapaOcorrencia();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoPlay();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoStop();
            }
        });
        //####################################################################################

        //TENTA INICIAR O ENVIO DAS COORDENANDAS
        try {
            Intent intent2 = new Intent(Ocorrencia_Activity.this, LocationService.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent2);
        }catch (Exception e){
            e.printStackTrace();
        }

        // aqui agenda sua tarefa para rodar daqui 1000 milisegundos e ficar repetindo a cada 5000 milisegundos
        myTimer.schedule(myTask, 1000, 5000);
    }

    private void monitoramento(){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... paramss) {
                try{
                    if(cont == 2) {
                        confereJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                        }
                        retornoHttp = ConexaoHttpClient.executaHttpPost(UrlOcorrencia, params);
                    params.set(6,new BasicNameValuePair("log","3"));
                } catch (Exception e) {
                    e.printStackTrace();
                    retornoHttp = "ASC";
                }
                return retornoHttp;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                verificaHora();

                try {
                    Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Monitorando " + VtrsMonitoradas, Toast.LENGTH_SHORT);
                    toast.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ((!retornoHttp.equals("0")) && (!retornoHttp.equals("ASC")) && (!retornoHttp.isEmpty())) {
                    params.set(8,new BasicNameValuePair("eo","sim"));
                    params.add(new BasicNameValuePair("vtr_oc", VtrOc));

                    if(cont == 1) {
                        //UMA VEZ ###############################################################
                        Globals.setMonitor(retornoHttp);
                        detalhes_ocorrencia = retornoHttp.split("\\|");
                        tv_endereco = (TextView) findViewById(R.id.tv_endereco_ocorrencia);
                        tv_tipo_oc = (TextView) findViewById(R.id.tv_desc_endereco_ocorrencia);
                        try {
                            tv_endereco.setText(detalhes_ocorrencia[6]);
                            tv_tipo_oc.setText(detalhes_ocorrencia[1]);
                            Globals.setEnderecoOcorrencia(detalhes_ocorrencia[6]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        vtr_final = detalhes_ocorrencia[0].split(":");
                        VtrOc = vtr_final[1];
                        Globals.setViaturaOcorrencia(VtrOc);
                        endereco_final = detalhes_ocorrencia[6].split(":");
                        try {
                            IO = detalhes_ocorrencia[3].split(":");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(true);
                        findViewById(R.id.btn_j9).setEnabled(true);
                        findViewById(R.id.btn_play).setEnabled(true);
                        findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_mapa_ocorrencia).setEnabled(true);
                        //#######################################################################
                    }
                    if ((IO!=null)&&(IO[1]!=null)&&(IO[1].equals(controlador))){

                        switch (confereJS) {

                            case "Aguarda-J9":
                                break;
                            case "J9":
                                findViewById(R.id.btn_j9).setEnabled(false);
                                findViewById(R.id.btn_j10).setEnabled(true);
                                findViewById(R.id.btn_j09_i).setEnabled(false);
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11).setEnabled(true);
                                findViewById(R.id.btn_j12).setEnabled(false);
                                break;
                            case "J10":
                                findViewById(R.id.btn_j9).setEnabled(false);
                                findViewById(R.id.btn_j10).setEnabled(false);
                                findViewById(R.id.btn_j09_i).setEnabled(true);
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11).setEnabled(true);
                                findViewById(R.id.btn_j12).setEnabled(false);
                                break;
                            case "J9I":
                                findViewById(R.id.btn_j9).setEnabled(false);
                                findViewById(R.id.btn_j10).setEnabled(false);
                                findViewById(R.id.btn_j09_i).setEnabled(false);
                                findViewById(R.id.btn_j10_i).setEnabled(true);
                                findViewById(R.id.btn_j11).setEnabled(false);
                                findViewById(R.id.btn_j12).setEnabled(false);
                                break;
                            case "J10I":
                                findViewById(R.id.btn_j9).setEnabled(false);
                                findViewById(R.id.btn_j10).setEnabled(false);
                                findViewById(R.id.btn_j09_i).setEnabled(false);
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11).setEnabled(true);
                                findViewById(R.id.btn_j12).setEnabled(false);
                                break;
                            case "J11":
                                findViewById(R.id.btn_j9).setEnabled(false);
                                findViewById(R.id.btn_j10).setEnabled(false);
                                findViewById(R.id.btn_j09_i).setEnabled(false);
                                findViewById(R.id.btn_j10_i).setEnabled(false);
                                findViewById(R.id.btn_j11).setEnabled(false);
                                findViewById(R.id.btn_j12).setEnabled(true);
                                break;
                            case "J12":
                                J12();
                                break;
                            case "inicio":
                                break;
                            default:
                                controlador = "DEFAULT";
                                try {
                                    Toast toast2 = Toast.makeText(Ocorrencia_Activity.this, "Ocorrência encontrada, aguarde...", Toast.LENGTH_LONG);
                                    toast2.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }else{
                        try {
                            Globals.setId_Ocorrencia(IO[1]);
                            controlador = IO[1];
                            params.set(10,new BasicNameValuePair("io", IO[1]));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(cont != 2){
                            //TOCA ALARME
                            play(Ocorrencia_Activity.this, getAlarmSound());

                            AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
                            builder.setTitle(getString(R.string.parar_alarme))
                                    .setCancelable(false)
                                    .setNeutralButton("PARAR", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            player.stop();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        //CONTADOR RECEBE 2 PARA NÃO CAIR NO ALARME DENOVO
                        cont = 2;
                        //################################################
                    }

                } else if ((retornoHttp.equals("0"))) {
                    //SE EXISTIA OCORRENCIA E A MESMA FOI ENCERRADA O APP EXECUTA O J12 PARA REINICIAR OS BOTOES
                    if(cont == 2) {
                       try{
                           J12();
                       }catch(Exception e){
                           e.printStackTrace();
                       }
                    }
                    //###########################################################
                    cont = 1;
                    //LIMPA O FORMULARIO DA OCORRENCIA
                    try{
                            params.remove(10);
                            params.remove(11);
                            params.remove(12);
                            params.remove(13);
                            params.remove(14);
                            params.remove(15);
                            params.remove(16);
                            params.remove(17);
                            params.remove(18);
                            params.remove(19);
                            params.remove(20);
                            params.remove(21);
                            params.remove(22);
                            params.remove(23);
                            params.remove(24);
                            params.remove(25);
                            params.remove(26);
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
                    //###########################################################

                    } else {
                        Toast toast = Toast.makeText(Ocorrencia_Activity.this, "ATENÇÃO!!! PERDA DE CONEXÃO", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        }.execute();
    }

    //FUNÇOES DOS BOTOES ####################################
    protected void J9() {
        params.add(new BasicNameValuePair("jota", "j9"));
        params.add(new BasicNameValuePair("hr_j9", s.format(new Date())));

        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j10).setEnabled(true);
        findViewById(R.id.btn_j11).setEnabled(true);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return retornoJS;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                super.cancel(true);
            }
        }.execute();
    }
    protected void J10() {
        j11 = true;
        LatOcorrencia = Globals.getLatitude();
        LngOcorrencia = Globals.getLongitude();

        params.add(new BasicNameValuePair("jota", "j10"));
        params.add(new BasicNameValuePair("lat_o", LatOcorrencia));
        params.add(new BasicNameValuePair("lng_o", LngOcorrencia));
        params.add(new BasicNameValuePair("hr_j10", s.format(new Date())));

        //ALTERNA BOTOES NA TELA ####################################
        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j10).setEnabled(false);
        findViewById(R.id.btn_j09_i).setEnabled(true);
        //###########################################################

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return retornoJS;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                super.cancel(true);
            }
        }.execute();
    }
    protected void J9Intermediario() {
        j11 = false;
        params.add(new BasicNameValuePair("jota", "j9_i"));
        params.add(new BasicNameValuePair("hr_j9_i", s.format(new Date())));

        //ALTERNA BOTOES NA TELA ####################################
        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j09_i).setEnabled(false);
        findViewById(R.id.btn_j10_i).setEnabled(true);
        //###########################################################

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS,params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return retornoJS;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                super.cancel(true);
            }
        }.execute();

        Intent intent = new Intent(Ocorrencia_Activity.this, ListaHospitaisActivity.class);
        startActivity(intent);

    }
    protected void J10Intermediario(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
        j11 = false;

        builder.setMessage(getString(R.string.texto_recusa_vitima))
                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String LatLocalIntermediario = Globals.getLatitude();
                        String LngLocalIntermediario = Globals.getLongitude();

                        params.add(new BasicNameValuePair("jota", "j10_i_n"));
                        params.add(new BasicNameValuePair("lat_i", LatLocalIntermediario));
                        params.add(new BasicNameValuePair("lng_i", LngLocalIntermediario));
                        params.add(new BasicNameValuePair("hr_j10_i_n", s.format(new Date())));

                        //ALTERNA BOTOES NA TELA ####################################
                        findViewById(R.id.btn_j9).setEnabled(false);
                        findViewById(R.id.btn_j10_i).setEnabled(false);
                        findViewById(R.id.btn_j11).setEnabled(true);
                        //###########################################################

                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... unused) {
                                try {
                                    retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    }
                                return retornoJS;
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

                        params.add(new BasicNameValuePair("jota", "j10_i_s"));
                        params.add(new BasicNameValuePair("lat_r", LatLocalRecusa));
                        params.add(new BasicNameValuePair("lng_r", LngLocalRecusa));
                        params.add(new BasicNameValuePair("hr_j10_i_s", s.format(new Date())));

                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... unused) {
                                try {
                                    retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return retornoJS;
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
    protected void J11(){
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

                            params.add(new BasicNameValuePair("jota", "j11_s"));
                            params.add(new BasicNameValuePair("lat_m", LatLocalMaca));
                            params.add(new BasicNameValuePair("lng_m", LngLocalMaca));
                            params.add(new BasicNameValuePair("hr_j11_s", s.format(new Date())));

                            new AsyncTask<Void, Void, String>() {

                                ProgressDialog pDialog;

                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    //ANTES DE EXECUTAR (JANELA)
                                    pDialog = ProgressDialog.show(Ocorrencia_Activity.this, "J11", "Atualizando ocorrência, aguarde...", true);
                                }

                                @Override
                                protected String doInBackground(Void... unused) {
                                    try {
                                        retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return retornoJS;
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

        params.add(new BasicNameValuePair("jota", "j11_n"));
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return retornoJS;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                pDialog.dismiss();


                //ALTERNA BOTOES NA TELA ####################################
                findViewById(R.id.btn_j9).setEnabled(false);
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
    protected void J12(){
        params.add(new BasicNameValuePair("jota", "j12"));
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
                   try {
                       tv_endereco.setText(getString(R.string.msg_sem_ocorrencia));
                       tv_tipo_oc.setText(VtrsMonitoradas);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }
                super.cancel(true);
            }
        }.execute();
    }
    protected void mapaOcorrencia(){
        if(isOnline()) {
            intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + endereco_final[1]));
            startActivity(intent);
        }else{
            try{
                Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Mapa disponível somente com a internet funcionando...", Toast.LENGTH_SHORT);
                toast.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected void detalhesOcorrencia(){
        Intent intent = new Intent(Ocorrencia_Activity.this, ListaDetalhesActivity.class);
        startActivity(intent);
    }
    protected void botaoPlay(){
        try {
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
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    protected void botaoStop(){
        try {
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //#######################################################
    protected void verificaHora(){
        SimpleDateFormat s_hora = new SimpleDateFormat("HH:mm");
        String verifica_hora =  s_hora.format(new Date());
        if(verifica_hora.equals("08:00")){

            AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
            builder.setTitle("Novo login!")
                    .setMessage("Bom dia! Seu login expirou, reconecte!")
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Ocorrencia_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            onDestroy();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
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
    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void encerraMonitoramento(){

        new AsyncTask<Void, Void, String>() {

            ProgressDialog pDialog;
            protected void onPreExecute() {
                super.onPreExecute();
                //ANTES DE EXECUTAR (JANELA)
                pDialog  = ProgressDialog.show(Ocorrencia_Activity.this, "Aguarde", "Encerrando monitoramento, aguarde...", true);
            }
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    params.set(5,new BasicNameValuePair("status","OFF"));
                    params.set(6,new BasicNameValuePair("log","2"));
                    ConexaoHttpClient.executaHttpPost(UrlOcorrencia,params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.cancel(true);
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
                myTimer.cancel();
                params.clear();
                lock.reenableKeyguard();
                Ocorrencia_Activity.this.mWakeLock.release();
                pDialog.dismiss();
                Ocorrencia_Activity.this.finish();
            }
        }.execute();

    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Ocorrencia_Activity.this, AlertDialog.THEME_HOLO_DARK);

        Resources res = getResources();
        alertDialog.setTitle("MONITORAMENTO!");
        alertDialog.setMessage("Deseja realmente parar o monitoramento?");

        alertDialog.setPositiveButton(res.getText(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                encerraMonitoramento();
            }
        });
        alertDialog.setNegativeButton(res.getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    class MyTimerTask extends TimerTask {
        public void run() {
            monitoramento();
        }
    }
}