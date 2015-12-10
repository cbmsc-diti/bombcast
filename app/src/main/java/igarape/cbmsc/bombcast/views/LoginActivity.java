package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.entity.ServidoresRadioOnline;
import igarape.cbmsc.bombcast.service.RadioOnlineService;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.utils.ManageSharedPreferences;

public class LoginActivity extends Activity {

    String URL = Globals.getPaginaConexao();
    AutoCompleteTextView txtId;
    EditText txtPwd;
    WebView myWebView;
    ProgressDialog pDialog;
    public Set<String> logins;
    private String retornoHttp;
    private ServidoresRadioOnline servidoresRadioOnline;
    protected List<NameValuePair> params = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String UrlCidades = Globals.getPaginaCidades();

        final CheckBox cbShowPassword = (CheckBox) findViewById(R.id.show_password);
        final ImageButton icon_face = (ImageButton) findViewById(R.id.icon_face);
        final ImageButton icon_twitter = (ImageButton) findViewById(R.id.icon_twitter);
        final ImageButton icon_igarape = (ImageButton) findViewById(R.id.icon_igarape);
        final ImageButton icon_cbm = (ImageButton) findViewById(R.id.icon_cbm);
        final ToggleButton btn_radioonline = (ToggleButton) findViewById(R.id.btn_radioonline);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String strIP = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        WifiManager.WifiLock lock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
        lock.acquire();

        String ipSeparado[] = strIP.split("\\.");
        if (ipSeparado[0].equals("10")){

            Spinner sp_servidores = (Spinner) findViewById(R.id.sp_servidores_radioonline);
            ArrayAdapter<ServidoresRadioOnline> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ServidoresRadioOnline.listaServidores());
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
            sp_servidores.setAdapter(spinnerArrayAdapter);
            sp_servidores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                    btn_radioonline.setChecked(false);
                    servidoresRadioOnline = (ServidoresRadioOnline) parent.getItemAtPosition(posicao);
                    Globals.setServidorRadioSelecionado(servidoresRadioOnline.getUrl());
                    Globals.setNomeServidorRadioSelecionado(servidoresRadioOnline.getNome());

                    try{
                        Intent intent = new Intent(LoginActivity.this, RadioOnlineService.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        stopService(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }else{
            Spinner sp_servidores = (Spinner) findViewById(R.id.sp_servidores_radioonline);
            ArrayAdapter<ServidoresRadioOnline> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ServidoresRadioOnline.listaServidoresExterno());
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
            sp_servidores.setAdapter(spinnerArrayAdapter);
            sp_servidores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                    btn_radioonline.setChecked(false);
                    servidoresRadioOnline = (ServidoresRadioOnline) parent.getItemAtPosition(posicao);
                    Globals.setServidorRadioSelecionado(servidoresRadioOnline.getUrl());
                    Globals.setNomeServidorRadioSelecionado(servidoresRadioOnline.getNome());
                    try{
                        Intent intent = new Intent(LoginActivity.this, RadioOnlineService.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        stopService(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        txtId = (AutoCompleteTextView) findViewById(R.id.txtLoginUser);
        txtId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtId.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(txtId, 0);
                        }
                    }, 200);
                }
            }
        });

        try{
            logins = ManageSharedPreferences.getSetStringFromSharedPreference(LoginActivity.this, Globals.PREF_FILE_NAMES, "login");

            Iterator it = logins.iterator();
            int cont = 0;
            String[] arr = new String[logins.size()];
            while (it.hasNext()) {
                String aux = (String)it.next();
                arr[cont] = aux;
                cont++;
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
            txtId.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        txtPwd = (EditText) findViewById(R.id.txtLoginPassword);

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txtPwd.setTransformationMethod(null);
                else
                    txtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        icon_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://pt-br.facebook.com/CBMSC");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });
        icon_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://twitter.com/CBMSC193");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);            }
        });
        icon_igarape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://www.igarape.org.br/");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });
        icon_cbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://portal.cbm.sc.gov.br/");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        btn_radioonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btn_radioonline.isChecked()){
                    try{
                    Intent intent = new Intent(LoginActivity.this, RadioOnlineService.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startService(intent);
                        }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    try{
                        Intent intent = new Intent(LoginActivity.this, RadioOnlineService.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        stopService(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        myWebView = (WebView) findViewById(R.id.wv_cidades);
        myWebView.loadUrl(UrlCidades);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void makeLoginRequest(View view) {

        //MINHA VALIDAÇÂO NO LDAP

        new AsyncTask<String, String, String>() {

            public String txtID= txtId.getText().toString();
            public String txtPwD= txtPwd.getText().toString();
            @Override
            protected void onPreExecute() {
                pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_in), getString(R.string.please_hold), true);
            }
            @Override
            protected String doInBackground(String... paramss) {
                try {

                    params.add(new BasicNameValuePair("u",txtID ));
                    params.add(new BasicNameValuePair("p",txtPwD));

                    retornoHttp = ConexaoHttpClient.executaHttpPost(URL,params);

                } catch (Exception e) {
                    e.printStackTrace();
                    retornoHttp = "2";
                }
                return retornoHttp;//0 ou 1
            }
            @Override
            protected void onPostExecute(String result) {
                try{
                    pDialog.dismiss();
                }catch(Exception e){
                    e.printStackTrace();
                }

                if (retornoHttp.equalsIgnoreCase("1")) {

                    Globals.setUserName(txtID);
                    Globals.setUserPwd(txtPwD);

                    try {
                        logins = ManageSharedPreferences.getSetStringFromSharedPreference(LoginActivity.this, Globals.PREF_FILE_NAMES, "login");
                    }catch (Exception ignored){}
                    if (logins == null){
                        logins = new LinkedHashSet<>();
                    }
                    logins.add(txtID);
                    logins.iterator();

                    ManageSharedPreferences.putInSharedPreferences(LoginActivity.this, Globals.PREF_FILE_NAMES, "login", logins);

                    Intent intent2 = new Intent(LoginActivity.this, Server_193Activity.class);
                    startActivity(intent2);
                    cancel(true);
                }else{
                    if (retornoHttp.equalsIgnoreCase("0")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Usuário ou senha incorretos.", Toast.LENGTH_LONG);
                        toast.show();

                        txtPwd.setText("");
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Problema ao conectar com o E193.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                cancel(true);
            }
        }.execute();
    }
}