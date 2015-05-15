package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.Globals;

/**
 * Created by barcellos on 03/03/15.
 */
public class ListaDetalhesActivity extends Activity {

    private List<String> listadeDetalhes = new ArrayList<>();
    ListView lv_detalhesOcorrencia;
    private String monitor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listadetalhes);
        monitor = Globals.getMonitor();
        listadeDetalhes = Arrays.asList(monitor.split("\\|"));
        lv_detalhesOcorrencia = (ListView) findViewById(R.id.lv_detalhesOcorrencia);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListaDetalhesActivity.this, R.layout.listview_detalhes, listadeDetalhes);


        ArrayAdapter<String> ListArrayAdapter = arrayAdapter;
        lv_detalhesOcorrencia.setAdapter(ListArrayAdapter);

    }
}