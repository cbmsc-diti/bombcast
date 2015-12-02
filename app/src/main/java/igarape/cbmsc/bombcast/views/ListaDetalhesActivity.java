package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.FileUtils;
import igarape.cbmsc.bombcast.utils.Globals;

 //* Created by barcellos on 03/03/15.

public class ListaDetalhesActivity extends Activity {

    ListView lv_detalhesOcorrencia;

    protected void onCreate(Bundle savedInstanceState) {
        String monitor;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listadetalhes);
        monitor = Globals.getMonitor();
        List<String> listadeDetalhes = Arrays.asList(monitor.split("\\|"));
        lv_detalhesOcorrencia = (ListView) findViewById(R.id.lv_detalhesOcorrencia);

        ArrayAdapter<String> ListArrayAdapter = new ArrayAdapter<>(this, R.layout.listview_detalhes, listadeDetalhes);
        lv_detalhesOcorrencia.setAdapter(ListArrayAdapter);
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(FileUtils.getPath(Globals.getUserName())+Globals.getId_Ocorrencia()+ "_detalhes.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
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


