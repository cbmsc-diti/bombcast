package igarape.cbmsc.bombcast.views;

import android.app.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.FileUtils;
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

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(FileUtils.getPath(Globals.getUserName())+Globals.getId_Ocorrencia()+ "_detalhes.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        Bitmap bitmap = takeScreenshot();
        saveBitmap(bitmap);

        finish();
    }
}


