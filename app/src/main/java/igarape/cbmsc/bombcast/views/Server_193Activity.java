package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.entity.Servidores_193;
import igarape.cbmsc.bombcast.utils.Globals;

public class Server_193Activity extends Activity {

    private Servidores_193 servidor_sel;
    WebView myWebView;
    String UrlCobom = Globals.getPaginaCobom();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_193);
        myWebView = (WebView) findViewById(R.id.wv_mapa);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(UrlCobom);

        Spinner sp_servidores = (Spinner) findViewById(R.id.sp_servidores);
        ArrayAdapter<Servidores_193> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, Servidores_193.listaServidores());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        sp_servidores.setAdapter(spinnerArrayAdapter);
        sp_servidores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                servidor_sel = (Servidores_193) parent.getItemAtPosition(posicao);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myWebView.destroy();
                onPause();
                Globals.setServidorSelecionado(servidor_sel.getIp());
                Intent intent = new Intent(Server_193Activity.this, Select_Vtr_Activity.class);
                startActivity(intent);
                Server_193Activity.this.finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        myWebView.loadUrl(UrlCobom);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Server_193Activity.this, LoginActivity.class);
        startActivity(intent);
        Server_193Activity.this.finish();
    }
}
