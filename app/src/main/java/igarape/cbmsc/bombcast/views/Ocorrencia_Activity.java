package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.BackgroundVideoRecorder;
import igarape.cbmsc.bombcast.service.LocationService;
import igarape.cbmsc.bombcast.service.PlayerService;
import igarape.cbmsc.bombcast.service.StopService;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.utils.ServiceUtils;

public class Ocorrencia_Activity extends Activity {

    final String URL_JOTAS = Globals.PAGINA_JOTAS;
    final String URL_OCORRENCIAS = Globals.PAGINA_OCORRENCIAS;
    final SimpleDateFormat formatoData = ServiceUtils.formatoData();
    final MyTimerTask myTask = new MyTimerTask(); // aqui instacia sua tarefa
    final Timer myTimer = new Timer(); // aqui instancia o agendador
    final String ServidorSelecionado = Globals.getServidorSelecionado();
    String VtrsMonitoradas = Globals.getVtrSelecionada();
    KeyguardManager.KeyguardLock lock;
    PowerManager.WakeLock mWakeLock;
    String[] DETALHES_OCORRENCIA;
    String[] ID_OCORRENCIA = new String[2];
    String VTR_OCORRENCIA;
    String CONTROLADOR_ID_OCORRENCIA;
    String DADOS_HTTP_OCORRENCIA;
    String LatOcorrencia;
    String LngOcorrencia;
    String vf;
    String VERIFICADOR_JOTAS = "inicio";
    String[] VTR_FINAL;
    String RETORNO_JOTAS;
    Boolean j11 = true;
    List<NameValuePair> PARAMETROS_OCORRENCIA = new ArrayList<>();
    Integer CONTADOR = 1;
    TextView tv_tipo_oc;
    TextView tv_endereco;
    Intent intent;
    String[] ENDERECO_FINAL;




    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ocorrencia);
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        this.mWakeLock.acquire();
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();


        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("nr_vtr", VtrsMonitoradas));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("h", ServidorSelecionado));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("u", Globals.getUserName()));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("infos", "1"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("status", "ON"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("log", "1"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lat_vtr", Globals.getLatitude()));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lng_vtr", Globals.getLongitude()));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("eo", "nao"));


        //INICIA OS BOTOES######################################################
        final  Button btn_j9    =  (Button) findViewById(R.id.btn_j9);
        final  Button btn_j10   =  (Button) findViewById(R.id.btn_j10);
        final  Button btn_j9_i  =  (Button) findViewById(R.id.btn_j09_i);
        final  Button btn_j10_i =  (Button) findViewById(R.id.btn_j10_i);
        final  Button btn_j11   =  (Button) findViewById(R.id.btn_j11);
        final  Button btn_j12   =  (Button) findViewById(R.id.btn_j12);
        final  Button btn_detalhes_ocorrencia = (Button) findViewById(R.id.btn_detalhes_ocorrencia);
        final  Button btn_mapa_ocorrencia = (Button) findViewById(R.id.btn_mapa_ocorrencia);
        final  Button btn_play  =  (Button) findViewById(R.id.btn_play);
        final  Button btn_stop  =  (Button) findViewById(R.id.btn_stop);

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
        if (ServiceUtils.verificaHora()){

            AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
            builder.setTitle("Novo login!")
                    .setMessage("Bom dia! Seu login expirou, reconecte!")
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Ocorrencia_Activity.this, LoginActivity.class);
                            startActivity(intent);
                            encerraMonitoramento();
                        }
                    });
            try {
                AlertDialog alert = builder.create();
                alert.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... paramss) {
                try{
                    if(CONTADOR == 2) {
                        VERIFICADOR_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                    }
                    DADOS_HTTP_OCORRENCIA = ConexaoHttpClient.executaHttpPost(URL_OCORRENCIAS, PARAMETROS_OCORRENCIA);
                    PARAMETROS_OCORRENCIA.set(6, new BasicNameValuePair("log", "3"));
                } catch (Exception e) {
                    e.printStackTrace();
                    DADOS_HTTP_OCORRENCIA = "ASC";
                }
                return DADOS_HTTP_OCORRENCIA + VERIFICADOR_JOTAS;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);



                Toast toast = Toast.makeText(Ocorrencia_Activity.this, "Monitorando " + VtrsMonitoradas, Toast.LENGTH_SHORT);
                toast.show();

                if ((!DADOS_HTTP_OCORRENCIA.equals("0")) && (!DADOS_HTTP_OCORRENCIA.equals("ASC")) && (!DADOS_HTTP_OCORRENCIA.isEmpty())) {


                    if(CONTADOR == 1) {
                        //EXECUTA UMA VEZ ###############################################################
                        try {
                            Globals.setMonitor(DADOS_HTTP_OCORRENCIA);


                            DETALHES_OCORRENCIA = DADOS_HTTP_OCORRENCIA.split("\\|");
                            VTR_FINAL           = DETALHES_OCORRENCIA[0].split(":");
                            ID_OCORRENCIA       = DETALHES_OCORRENCIA[3].split(":");
                            ENDERECO_FINAL      = DETALHES_OCORRENCIA[6].split(":");
                            VTR_OCORRENCIA      = VTR_FINAL[1];

                            tv_tipo_oc  = (TextView) findViewById(R.id.tv_desc_endereco_ocorrencia);
                            tv_endereco = (TextView) findViewById(R.id.tv_endereco_ocorrencia);

                            tv_tipo_oc.setText(DETALHES_OCORRENCIA[1]);
                            tv_endereco.setText(DETALHES_OCORRENCIA[6]);


                            Globals.setViaturaOcorrencia(VTR_OCORRENCIA);
                            Globals.setId_Ocorrencia(ID_OCORRENCIA[1]);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        PARAMETROS_OCORRENCIA.set(8, new BasicNameValuePair("eo", "sim"));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("vtr_oc", VTR_OCORRENCIA));

                        findViewById(R.id.btn_detalhes_ocorrencia).setEnabled(true);
                        findViewById(R.id.btn_j9).setEnabled(true);
                        findViewById(R.id.btn_play).setEnabled(true);
                        findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_mapa_ocorrencia).setEnabled(true);
                        //#######################################################################
                    }
                    if ((ID_OCORRENCIA !=null)&&(ID_OCORRENCIA[1]!=null)&&(ID_OCORRENCIA[1].equals(CONTROLADOR_ID_OCORRENCIA))){

                        switch (VERIFICADOR_JOTAS) {

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
                                CONTROLADOR_ID_OCORRENCIA = "DEFAULT";
                                try {
                                    Toast toast2 = Toast.makeText(Ocorrencia_Activity.this, "Ocorrência encontrada, aguarde...", Toast.LENGTH_LONG);
                                    toast2.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }else{
                        if(CONTADOR != 2){
                            //TOCA ALARME
                            playAlarme();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Ocorrencia_Activity.this);
                            builder.setTitle(getString(R.string.parar_alarme))
                                    .setCancelable(false)
                                    .setNeutralButton("PARAR", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            stopAlarme();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        try {
                            assert ID_OCORRENCIA != null;
                            CONTROLADOR_ID_OCORRENCIA = ID_OCORRENCIA[1];
                            PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("io", ID_OCORRENCIA[1]));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        //CONTADOR RECEBE 2 PARA NÃO CAIR NO ALARME DENOVO
                        CONTADOR = 2;
                        //################################################
                    }

                } else if ((DADOS_HTTP_OCORRENCIA.equals("0"))) {
                    //SE EXISTIA OCORRENCIA E A MESMA FOI ENCERRADA O APP EXECUTA O J12 PARA REINICIAR OS BOTOES
                    if(CONTADOR == 2) {
                        try{
                            J12();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    //###########################################################
                    CONTADOR = 1;
                    //LIMPA O FORMULARIO DA OCORRENCIA
                    try{
                        PARAMETROS_OCORRENCIA.remove(10);
                        PARAMETROS_OCORRENCIA.remove(11);
                        PARAMETROS_OCORRENCIA.remove(12);
                        PARAMETROS_OCORRENCIA.remove(13);
                        PARAMETROS_OCORRENCIA.remove(14);
                        PARAMETROS_OCORRENCIA.remove(15);
                        PARAMETROS_OCORRENCIA.remove(16);
                        PARAMETROS_OCORRENCIA.remove(17);
                        PARAMETROS_OCORRENCIA.remove(18);
                        PARAMETROS_OCORRENCIA.remove(19);
                        PARAMETROS_OCORRENCIA.remove(20);
                        PARAMETROS_OCORRENCIA.remove(21);
                        PARAMETROS_OCORRENCIA.remove(22);
                        PARAMETROS_OCORRENCIA.remove(23);
                        PARAMETROS_OCORRENCIA.remove(24);
                        PARAMETROS_OCORRENCIA.remove(25);
                        PARAMETROS_OCORRENCIA.remove(26);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //###########################################################
                } else {
                   Toast toast2 = Toast.makeText(Ocorrencia_Activity.this, "ATENÇÃO!!! PERDA DE CONEXÃO", Toast.LENGTH_SHORT);
                    toast2.show();
                }
                super.cancel(true);
            }
        }.execute();
        }
    }

    //FUNÇOES DOS BOTOES ####################################
    protected void J9() {
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j9"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j9", formatoData.format(new Date())));

        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j10).setEnabled(true);
        findViewById(R.id.btn_j11).setEnabled(true);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return RETORNO_JOTAS;
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

        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j10"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lat_o", LatOcorrencia));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lng_o", LngOcorrencia));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j10", formatoData.format(new Date())));

        //ALTERNA BOTOES NA TELA ####################################
        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j10).setEnabled(false);
        findViewById(R.id.btn_j09_i).setEnabled(true);
        //###########################################################

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return RETORNO_JOTAS;
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
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j9_i"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j9_i", formatoData.format(new Date())));

        //ALTERNA BOTOES NA TELA ####################################
        findViewById(R.id.btn_j9).setEnabled(false);
        findViewById(R.id.btn_j09_i).setEnabled(false);
        findViewById(R.id.btn_j10_i).setEnabled(true);
        //###########################################################

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return RETORNO_JOTAS;
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

                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j10_i_n"));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lat_i", LatLocalIntermediario));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lng_i", LngLocalIntermediario));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j10_i_n", formatoData.format(new Date())));

                        //ALTERNA BOTOES NA TELA ####################################
                        findViewById(R.id.btn_j9).setEnabled(false);
                        findViewById(R.id.btn_j10_i).setEnabled(false);
                        findViewById(R.id.btn_j11).setEnabled(true);
                        //###########################################################

                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... unused) {
                                try {
                                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return RETORNO_JOTAS;
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

                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j10_i_s"));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lat_r", LatLocalRecusa));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lng_r", LngLocalRecusa));
                        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j10_i_s", formatoData.format(new Date())));

                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... unused) {
                                try {
                                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return RETORNO_JOTAS;
                            }

                            @Override
                            protected void onPostExecute(String aVoid) {
                                super.onPostExecute(aVoid);

                                Intent intent = new Intent(Ocorrencia_Activity.this, ListaHospitaisActivity.class);
                                startActivity(intent);

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

                            PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j11_s"));
                            PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lat_m", LatLocalMaca));
                            PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("lng_m", LngLocalMaca));
                            PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j11_s", formatoData.format(new Date())));

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
                                        RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return RETORNO_JOTAS;
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

        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j11_n"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j11_n", formatoData.format(new Date())));

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
                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return RETORNO_JOTAS;
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
        CONTADOR = 1;
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("jota", "j12"));
        PARAMETROS_OCORRENCIA.add(new BasicNameValuePair("hr_j12", formatoData.format(new Date())));

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
                    RETORNO_JOTAS = ConexaoHttpClient.executaHttpPost(URL_JOTAS, PARAMETROS_OCORRENCIA);
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

            intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + ENDERECO_FINAL[1]));
            startActivity(intent);
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
    protected void playAlarme() {


        Intent intent = new Intent(Ocorrencia_Activity.this, PlayerService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

    }
    protected void stopAlarme() {
        Intent intent = new Intent(Ocorrencia_Activity.this, PlayerService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stopService(intent);

    }
    private void encerraMonitoramento(){

        try {
            PARAMETROS_OCORRENCIA.set(5, new BasicNameValuePair("status", "OFF"));
            PARAMETROS_OCORRENCIA.set(6, new BasicNameValuePair("log", "2"));
        }catch (Exception e){
            e.printStackTrace();
        }
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
                    DADOS_HTTP_OCORRENCIA = ConexaoHttpClient.executaHttpPost(URL_OCORRENCIAS, PARAMETROS_OCORRENCIA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DADOS_HTTP_OCORRENCIA;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                super.cancel(true);

                myTimer.cancel();
                PARAMETROS_OCORRENCIA.clear();
                lock.reenableKeyguard();

                try {
                    Ocorrencia_Activity.this.mWakeLock.release();
                    pDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(Ocorrencia_Activity.this, StopService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Ocorrencia_Activity.this.finish();
            }
        }.execute();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        encerraMonitoramento();
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