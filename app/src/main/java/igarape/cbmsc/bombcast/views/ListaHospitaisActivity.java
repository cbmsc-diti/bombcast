package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.Globals;

/**
 * Created by barcellos on 20/08/15.
 */
public class ListaHospitaisActivity extends Activity {

    private List<String> listadeHospitais = new ArrayList<>();
    ListView lv_hospitais;
    private String hospitais;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_hospitais);
        hospitais = "HU|HR|HSJ|OUTROS";
        listadeHospitais = Arrays.asList(hospitais.split("\\|"));
        lv_hospitais = (ListView) findViewById(R.id.lv_listahospitais);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListaHospitaisActivity.this, R.layout.listview_detalhes, listadeHospitais);


        ArrayAdapter<String> ListArrayAdapter = arrayAdapter;
        lv_hospitais.setAdapter(ListArrayAdapter);

        lv_hospitais.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long id) {


                Object obj = a.getItemAtPosition(pos);

                String mensagem = "Deslocando para: " + obj;
                String hosp = obj.toString();

                Globals.setDeslocando_para(hosp);

                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG)
                        .show();

                ListaHospitaisActivity.this.finish();
            }

        });
    }
}
