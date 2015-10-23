package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.FileUtils;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.service.UploadService;

import static igarape.cbmsc.bombcast.BuildConfig.requireWifiUpload;

public class Select_Vtr_Activity extends Activity {

    protected String Url;
    private List<String> vtrs = new ArrayList<>();
    private String servidor193 = Globals.getServidorSelecionado();
    private String usuario = Globals.getUserName();
    private String vtr_sel;
    public String status;
    ProgressDialog pDialog;
    EditText et_telefone;
    TextView nm_cmt;
    List<NameValuePair> params = new ArrayList<>();
    private final GenericExtFilter filter = new GenericExtFilter(".mp4");
    private ArrayList<File> videos;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vtr);
        findViewById(R.id.btn_next).setEnabled(false);
        et_telefone = (EditText) findViewById(R.id.et_cel_cmt_area);
        nm_cmt = (TextView) findViewById(R.id.tv_nm_cmt);
        Url = Globals.SERVER_CBM + "sel_vtr.bombcast.php";
        final Intent intent = new Intent(Select_Vtr_Activity.this, UploadService.class);



        new AsyncTask<Void, Void, List>() {

            @Override
            protected void onPreExecute() {
                try {
                    pDialog = ProgressDialog.show(Select_Vtr_Activity.this,"Verificando cadastro no E193", getString(R.string.please_hold), true);
                }catch (Exception e){

                }
                params.add(new BasicNameValuePair("u", usuario));
                params.add(new BasicNameValuePair("h", servidor193));
                params.add(new BasicNameValuePair("vf", "0"));
            }
            @Override
            protected List doInBackground(Void... unused) {
                try {
                    status = ConexaoHttpClient.executaHttpPost(Url,params);
                    vtrs = Arrays.asList(status.split("\\."));

                } catch (Exception e) {
                    e.printStackTrace();
                    vtrs.add(0,"");
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
            protected void onPostExecute(List aVoid) {
                super.onPostExecute(aVoid);
                params.add(new BasicNameValuePair("vf", "1"));

                if (vtrs.get(0).equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);
                    builder.setMessage(getString(R.string.texto_vtr_nao_cadastrada))
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent2 = new Intent(Select_Vtr_Activity.this, LoginActivity.class);
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

                    Spinner sp_vtrs = (Spinner) findViewById(R.id.sp_vtrs);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(Select_Vtr_Activity.this, R.layout.spinner_item, vtrs);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    sp_vtrs.setAdapter(spinnerArrayAdapter);
                    sp_vtrs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                            vtr_sel = parent.getItemAtPosition(posicao).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    try {
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    if(!status.isEmpty()){

                        //PROBLEMA COM O WHATSAPP
                       /* try { List<String> cmta = Arrays.asList(status.split("\\."));
                            String telefone = cmta.get(2);
                            String idCmt = cmta.get(0) +" "+ cmta.get(1);
                            ((EditText) findViewById(R.id.et_cel_cmt_area)).setText(telefone);
                            ((TextView) findViewById(R.id.tv_nm_cmt)).setText(idCmt);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
*/
                        ((TextView) findViewById(R.id.tv_nm_cmt)).setText("EM DESENVOLVIMENTO");

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);
                        builder.setMessage(getString(R.string.msg_tel_nao_encontrado))
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
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


                Globals.setTelefoneCmt(et_telefone.getText().toString());
                Globals.setVtrSelecionada(vtr_sel);

                Intent intent = new Intent(Select_Vtr_Activity.this, Ocorrencia_Activity.class);
                startActivity(intent);
            }
        });

        final Button btn_hidrantes = (Button) findViewById(R.id.btn_hidrantes);
        btn_hidrantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Select_Vtr_Activity.this);

                builder.setTitle("EM DESENVOLVIMENTO!")
                        .setMessage("OPÇAO DISPONIVEL EM:\n" +
                                "\n http://www.cbm.sc.gov.br/intranet/relatorios_gestores/relatorio_administrativo/index.php \n  Em seguida clique em 'MAPEAMENTO CBMSC'")
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
               // Intent intent3 = new Intent(Select_Vtr_Activity.this, MapaHidrantesActivity.class);
               // startActivity(intent3);
            }
        });

        final Button btn_edit_telefone = (Button) findViewById(R.id.btn_edit_telefone);
        btn_edit_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.et_cel_cmt_area).setEnabled(true);
                ((TextView) findViewById(R.id.tv_nm_cmt)).setText(getString(R.string.telefone_editado));
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

                        try{
                            Toast toast = Toast.makeText(Select_Vtr_Activity.this, "Os vídeos estão sendo enviados...", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
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
}