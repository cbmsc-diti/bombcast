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
    AutoCompleteTextView txtId;
    EditText txtPwd;
    ProgressDialog pDialog;
    public Set<String> logins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button btn_ajuda = (Button) findViewById(R.id.btn_ajuda);
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


        /**
         * Appears a hack
         * On login_activity I added
         * android:focusable="true"
         * android:focusableInTouchMode="true"
         */

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
                    }, 500);
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

}

                txtPwd = (EditText) findViewById(R.id.txtLoginPassword);



        final CheckBox cbShowPassword = (CheckBox) findViewById(R.id.show_password);
        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txtPwd.setTransformationMethod(null);
                else
                    txtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        final ImageButton icon_face = (ImageButton) findViewById(R.id.icon_face);
        icon_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.setUrlSocial("http://pt-br.facebook.com/CBMSC");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton icon_twitter = (ImageButton) findViewById(R.id.icon_twitter);
        icon_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://twitter.com/CBMSC193");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton icon_igarape = (ImageButton) findViewById(R.id.icon_igarape);
        icon_igarape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://www.igarape.org.br/");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton icon_cbm = (ImageButton) findViewById(R.id.icon_cbm);
        icon_cbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("http://portal.cbm.sc.gov.br/");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
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


         /*     //ACESSO AO MONITORAMENTO DO COPCAST-- AJEITAR NO BANCO DE DADOS ANTES
                pDialog = ProgressDialog.show(this, getString(R.string.login_in), getString(R.string.please_hold), true);


                final String regId = Globals.getRegistrationId(getApplicationContext());
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", txtId.getText().toString()));
                params.add(new BasicNameValuePair("password", txtPwd.getText().toString()));
                params.add(new BasicNameValuePair("scope", "client"));
                params.add(new BasicNameValuePair("gcm_registration", regId));

                post(this, Globals.SERVER_URL_WEB + "/token", params, new HttpResponseCallback() {
                    @Override
                    public void success(JSONObject response) {
                        Log.d(TAG, "@JSONRESPONSE=[" + response + "]");
                        String token = null;
                        try {
                            token = (String) response.get("token");
                            //Globals.setUserName(getApplicationContext(), (String) response.get("userName"));
                        } catch (JSONException e) {
                            Log.e(TAG, "error on login", e);
                        }
                        if (pDialog != null) {
                            pDialog.dismiss();
                            pDialog = null;
                        }
                        Globals.setAccessToken(getBaseContext(), token);
                        Globals.setUserLogin(getBaseContext(), txtId.getText().toString());

                        HistoryUtils.registerHistory(getApplicationContext(), State.NOT_LOGGED, State.LOGGED, Globals.getUserLogin(LoginActivity.this));
                    }

                    @Override
                    public void unauthorized() {
                        showToast(R.string.unauthorized_login);
                    }

                    private void showToast(final int message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (pDialog != null) {
                                    pDialog.dismiss();
                                    pDialog = null;
                                }
                                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 100);
                                toast.show();

                            }
                        });
                    }

                    @Override
                    public void failure(int statusCode) {
                        showToast(R.string.server_error);
                    }

                    @Override
                    public void noConnection() {
                        showToast(R.string.network_required);
                    }

                    @Override
                    public void badConnection() {
                        showToast(R.string.connection_error);
                    }

                    @Override
                    public void badRequest() {
                        showToast(R.string.bad_request_error);
                    }

                    @Override
                    public void badResponse() {
                        showToast(R.string.bad_request_error);
                    }
                });
//                #######################################################
*/

            //MINHA VALIDAÇÂO NO LDAP
            Processo meu = new Processo(getBaseContext());
            meu.execute();

                //LOGIN SEM VALIDAÇÂO
               /* Globals.setUserName(txtId.getText().toString());
                Globals.setUserPwd(txtPwd.getText().toString());


                ManageSharedPreferences.putInSharedPreferences(LoginActivity.this,Globals.PREF_FILE_NAMES,"login",txtId.getText().toString());

                Intent intent = new Intent(LoginActivity.this, Server_193Activity.class);
                startActivity(intent);
                LoginActivity.this.finish();
*/

        }

                public class Processo extends AsyncTask<String, String, String> {


                private String retornoHttp = "";
                public Context context;
                public Processo(Context context) {
                    this.context = context;
                }
                protected List<NameValuePair> params = new ArrayList<>();
                public String txtID= txtId.getText().toString();
                public String txtPwD= txtPwd.getText().toString();

                @Override
                protected void onPreExecute() {
                    //ANTES DE EXECUTAR (JANELA)
                    pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_in), getString(R.string.please_hold), true);
                }

                @Override
                protected String doInBackground(String... paramss) {
                    try {
                        String URL = Globals.SERVER_CBM + "ldap.conf.bombcast.php";
                        params.add(new BasicNameValuePair("u",txtID ));
                        params.add(new BasicNameValuePair("p",txtPwD));

                        retornoHttp = ConexaoHttpClient.executaHttpPost(URL,params);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return retornoHttp;//0 ou 1
                }

                @Override
                protected void onPostExecute(String result) {

                    if (retornoHttp.equalsIgnoreCase("1")) {

                       try{
                            pDialog.dismiss();
                            }catch(Exception e){
                            e.printStackTrace();
                       }
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
/*
                        while (it.hasNext()) {
                            String aux = (String)it.next();
                        }
*/
                        ManageSharedPreferences.putInSharedPreferences(LoginActivity.this, Globals.PREF_FILE_NAMES, "login", logins);

                        Intent intent = new Intent(LoginActivity.this, Server_193Activity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        cancel(true);


                    }else{
                        if (retornoHttp.equalsIgnoreCase("0")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Tente novamente.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 100);
                            toast.show();

                        txtPwd.setText("");

                            try{
                                pDialog.dismiss();
                            }catch(Exception e){
                                e.printStackTrace();
                            }


                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Problema ao conectar com o E193.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 100);
                            toast.show();
                            try{
                                pDialog.dismiss();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                    }
                    cancel(true);
                }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}
