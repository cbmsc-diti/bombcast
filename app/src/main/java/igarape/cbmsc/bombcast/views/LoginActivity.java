package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.service.BackgroundVideoRecorder;
import igarape.cbmsc.bombcast.service.LocationService;
import igarape.cbmsc.bombcast.state.State;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;
import igarape.cbmsc.bombcast.utils.HistoryUtils;
import igarape.cbmsc.bombcast.utils.HttpResponseCallback;
import igarape.cbmsc.bombcast.utils.ManageSharedPreferences;

import static igarape.cbmsc.bombcast.utils.NetworkUtils.post;

public class LoginActivity extends Activity {

    public static String TAG = LoginActivity.class.getName();
    String URL = Globals.SERVER_CBM + "ldap.conf.bombcast.php";
    AutoCompleteTextView txtId;
    EditText txtPwd;
    ProgressDialog pDialog;
    public Set<String> logins;
    private String retornoHttp;
    protected List<NameValuePair> params = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final CheckBox cbShowPassword = (CheckBox) findViewById(R.id.show_password);
        final Button btn_ajuda = (Button) findViewById(R.id.btn_ajuda);
        final ImageButton icon_face = (ImageButton) findViewById(R.id.icon_face);
        final ImageButton icon_twitter = (ImageButton) findViewById(R.id.icon_twitter);
        final ImageButton icon_igarape = (ImageButton) findViewById(R.id.icon_igarape);
        final ImageButton icon_cbm = (ImageButton) findViewById(R.id.icon_cbm);


        btn_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setTitle(getString(R.string.title_help))
                        .setMessage(getString(R.string.help_text))
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

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
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
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
                startActivity(intent);            }
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
                startActivity(intent);            }
        });
        icon_cbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://portal.cbm.sc.gov.br/");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);            }
        });


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
                    }catch (Exception e){}
                    if (logins == null){
                        logins = new LinkedHashSet<String>();
                    }
                    logins.add(txtID);
                    Iterator it = logins.iterator();

                    ManageSharedPreferences.putInSharedPreferences(LoginActivity.this, Globals.PREF_FILE_NAMES, "login", logins);

                    Intent intent = new Intent(LoginActivity.this, Server_193Activity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
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