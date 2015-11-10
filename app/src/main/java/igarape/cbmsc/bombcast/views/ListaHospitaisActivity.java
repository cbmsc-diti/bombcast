package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

/**
 * Created by barcellos on 20/08/15.
 */
public class ListaHospitaisActivity extends Activity {

    List<String> listadeHospitais = new ArrayList<>();
    ListView lv_hospitais;
    List<NameValuePair> params = new ArrayList<>();
    String UrlJS = Globals.SERVER_CBM +"j_ocorrencia.bombcast2.php";
    String retornoJS;
    String mensagem;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_hospitais);
        listadeHospitais =Globals.getListaHospitais();
        lv_hospitais = (ListView) findViewById(R.id.lv_listahospitais);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListaHospitaisActivity.this, R.layout.listview_detalhes, listadeHospitais);
        ArrayAdapter<String> ListArrayAdapter = arrayAdapter;
        lv_hospitais.setAdapter(ListArrayAdapter);

        lv_hospitais.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long id) {


                Object obj = a.getItemAtPosition(pos);

                mensagem = "Deslocando para: " + obj;
                String hosp = obj.toString();

                Globals.setDeslocando_para(hosp);
                params.add(new BasicNameValuePair("jota", "hosp"));
                params.add(new BasicNameValuePair("hsp", hosp));
                params.add(new BasicNameValuePair("vtr_oc", Globals.getviaturaOcorrencia()));
                params.add(new BasicNameValuePair("h", Globals.getServidorSelecionado()));
                params.add(new BasicNameValuePair("u", Globals.getUserName()));
                params.add(new BasicNameValuePair("io", Globals.getId_Ocorrencia()));


                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... unused) {
                        try {

                            retornoJS = ConexaoHttpClient.executaHttpPost(UrlJS, params);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);

                        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG)
                                .show();


                    }
                }.execute();

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(ListaHospitaisActivity.this);
        builder.setTitle("Deslocando para:")
                .setMessage("SELECIONE UM DESTINO E CLIQUE NO BOTÃO 'VOLTAR' DO TELEFONE.")
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}