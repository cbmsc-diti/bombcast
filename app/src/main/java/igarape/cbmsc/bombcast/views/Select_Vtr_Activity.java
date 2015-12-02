package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.UploadService;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.FileUtils;
import igarape.cbmsc.bombcast.utils.Globals;

import static igarape.cbmsc.bombcast.BuildConfig.requireWifiUpload;

public class Select_Vtr_Activity extends Activity {
    protected PowerManager.WakeLock mWakeLock;
    protected KeyguardManager.KeyguardLock lock;
    private final GenericExtFilter filter = new GenericExtFilter(".mp4");
    private String vtrs;
    private String servidor193 = Globals.getServidorSelecionado();
    private String usuario = Globals.getUserName();
    private String senha = Globals.getUserPwd();
    protected String Url= Globals.getPaginaViaturas();
    public String status;
    ProgressDialog pDialog;
    List<NameValuePair> params = new ArrayList<>();
    WebView myWebView;
    String UrlOrdens ="http://"+servidor193+"/web193/modulos/ordens/upload/cadastro.php";
    String UrlHidrantes = "http://www.cbm.sc.gov.br/intranet/relatorios_gestores/relatorio_administrativo/mapeamento/m/?host="+servidor193;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vtr);
        findViewById(R.id.btn_next).setEnabled(false);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final Intent intent = new Intent(Select_Vtr_Activity.this, UploadService.class);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        this.mWakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String strIP = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        String ipSeparado[] = strIP.split("\\.");
        if (!ipSeparado[0].equals("10")){

            findViewById(R.id.btn_hidrantes).setEnabled(false);
            findViewById(R.id.btn_ordens).setEnabled(false);
            findViewById(R.id.btn_ordens).setVisibility(View.INVISIBLE);

            AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);
            builder.setMessage("Conecte-se na rede do quartel se quiser visualizar as ordens de serviço e/ou mapa de hidrantes.")
                    .setCancelable(true)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else{

            try{
                myWebView = (WebView) findViewById(R.id.wv_mapa);
                myWebView.setWebViewClient(new MyWebViewClient());
                myWebView.getSettings().getAllowContentAccess();
                myWebView.getSettings().getAllowFileAccess();
                myWebView.getSettings().getLoadsImagesAutomatically();
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.getSettings().setUseWideViewPort(true);
                myWebView.getSettings().setBuiltInZoomControls(true);
                myWebView.setHttpAuthUsernamePassword("cbm.sc.gov.br", null, usuario, senha);
                myWebView.setDownloadListener(new DownloadListener() {
                    public void onDownloadStart(String url, String userAgent,
                                                String contentDisposition, String mimetype,
                                                long contentLength) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);
                builder.setMessage("Não foi possível carregar a página! ")
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                try {
                    pDialog = ProgressDialog.show(Select_Vtr_Activity.this,"Verificando cadastro no E193", getString(R.string.please_hold), true);
                }catch (Exception e){
                    e.printStackTrace();
                }
                params.add(new BasicNameValuePair("u", usuario));
                params.add(new BasicNameValuePair("h", servidor193));
                params.add(new BasicNameValuePair("vf", "0"));
            }
            @Override
            protected String doInBackground(Void... unused) {
                try {
                    vtrs = ConexaoHttpClient.executaHttpPost(Url, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    vtrs="";
                }

                try {
                    params.add(new BasicNameValuePair("vf", "2"));
                    status = ConexaoHttpClient.executaHttpPost(Url, params);
                    List<String> hosp = Arrays.asList(status.split("\\."));
                    Globals.setListaHospitais(hosp);


                } catch (Exception e) {
                    e.printStackTrace();
                    String erro = "Não foi possivel carregar os destinos.";
                    List<String> hosp = Arrays.asList(erro);
                    Globals.setListaHospitais(hosp);
                }
                return vtrs;
            }
            @Override
            protected void onPostExecute(String aVoid) {
                pDialog.dismiss();
                super.onPostExecute(aVoid);
                params.add(new BasicNameValuePair("vf", "1"));

                if (vtrs.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);
                    builder.setMessage(getString(R.string.texto_vtr_nao_cadastrada))
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent2 = new Intent(Select_Vtr_Activity.this, Server_193Activity.class);
                                    startActivity(intent2);
                                    Select_Vtr_Activity.this.finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    try {
                        status = ConexaoHttpClient.executaHttpPost(Url,params);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                findViewById(R.id.btn_next).setEnabled(true);
                super.cancel(true);
            }
        }.execute();

        final Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.setVtrSelecionada(vtrs);
                Select_Vtr_Activity.this.onPause();
                Intent intent = new Intent(Select_Vtr_Activity.this, Ocorrencia_Activity.class);
                startActivity(intent);
            }
        });

        final Button btn_hidrantes = (Button) findViewById(R.id.btn_hidrantes);
        btn_hidrantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);

                if(servidor193.equals("fpolis.cbm.sc.gov.br"))
                {
                    myWebView.loadUrl(UrlHidrantes);
                    findViewById(R.id.btn_hidrantes).setEnabled(false);
                    findViewById(R.id.btn_hidrantes).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btn_ordens).setEnabled(true);
                    findViewById(R.id.btn_ordens).setVisibility(View.VISIBLE);

                }else{

                    builder.setTitle("EM DESENVOLVIMENTO!")
                            .setMessage("OPÇAO TEMPORARIAMENTE DISPONÍVEL SOMENTE NO 1º e 10º BBM.")
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        final Button btn_ordens = (Button) findViewById(R.id.btn_ordens);
        btn_ordens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);

                if(servidor193.equals("fpolis.cbm.sc.gov.br"))
                {
                    myWebView.loadUrl(UrlOrdens);
                    findViewById(R.id.btn_ordens).setEnabled(false);
                    findViewById(R.id.btn_ordens).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btn_hidrantes).setEnabled(true);
                    findViewById(R.id.btn_hidrantes).setVisibility(View.VISIBLE);
                }else{

                    builder.setTitle("EM DESENVOLVIMENTO!")
                            .setMessage("OPÇAO TEMPORARIAMENTE DISPONÍVEL SOMENTE NO 1º e 10º BBM.")
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        final Button btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!canUpload(getBaseContext(), intent)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);

                    builder.setTitle("ERRO!")
                            .setMessage("Envio de vídeos somente conectado na WIFI.")
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    String path = FileUtils.getPath(Globals.getUserName());
                    File dir = new File(path);
                    File[] files = dir.listFiles(filter);
                    if (files != null && files.length > 0) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startService(intent);
                        try {
                            Toast toast = Toast.makeText(Select_Vtr_Activity.this, "Os vídeos estão sendo enviados...", Toast.LENGTH_LONG);
                            toast.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);

                        builder.setTitle("VÍDEOS NO SERVIDOR!")
                                .setMessage("Não foram encontrados vídeos nesse dispositivo")
                                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                }
            }

        });
        try {
            myWebView.loadUrl(UrlOrdens);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean canUpload(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnectedOrConnecting() || intent == null) {
            return false;
        }

        boolean isWiFi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        return (isWiFi || !requireWifiUpload);
    }
    class GenericExtFilter implements FilenameFilter {

        private String ext;
        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        lock.reenableKeyguard();
        this.mWakeLock.release();
        Intent intent = new Intent(Select_Vtr_Activity.this, Server_193Activity.class);
        startActivity(intent);
        Select_Vtr_Activity.this.finish();
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                                              HttpAuthHandler handler, String host, String realm) {
            handler.proceed(usuario, senha);
        }
    }
}
