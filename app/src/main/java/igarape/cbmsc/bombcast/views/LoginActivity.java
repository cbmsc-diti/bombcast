package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

import static java.net.URLEncoder.encode;

public class LoginActivity extends Activity {

    public static String TAG = LoginActivity.class.getName();
    EditText txtId;
    EditText txtPwd;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtId = (EditText) findViewById(R.id.txtLoginUser);
        //txtId.setText(Globals.getUserLogin(this));
        txtPwd = (EditText) findViewById(R.id.txtLoginPassword);

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
                    }, 200);
                }
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

        Processo meu = new Processo(getBaseContext());
        meu.execute();
    }

    public class Processo extends AsyncTask<String, String, String> {

        private String retornoHttp= "";


        public Context context;

        public Processo(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //ANTES DE EXECUTAR (JANELA)
            pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_in), getString(R.string.please_hold), true);

        }

        @Override
        protected String doInBackground(String... paramss) {
            try {
                String URL = Globals.SERVER_CBM + "ldap.conf.bombcast.php?u=" + txtId.getText().toString() + "&p=" +encode(txtPwd.getText().toString(),"ISO-8859-1");

                retornoHttp = ConexaoHttpClient.executaHttpGet(URL);


            } catch (Exception e) {
                e.printStackTrace();
            } return retornoHttp;
        }

        @Override
        protected void onPostExecute(String result) {

            if (retornoHttp.equalsIgnoreCase("1") ) {

                Globals.setUserName(txtId.getText().toString());
                Intent intent = new Intent(LoginActivity.this, Server_193Activity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Erro ao acessar o aplicativo.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.show();


                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

}
