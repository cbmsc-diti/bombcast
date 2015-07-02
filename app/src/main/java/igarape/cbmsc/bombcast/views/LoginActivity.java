package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

public class LoginActivity extends Activity {

    public static String TAG = LoginActivity.class.getName();
    EditText txtId;
    EditText txtPwd;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final Button btn_ajuda = (Button) findViewById(R.id.btn_ajuda);
        btn_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setTitle("Precisa de ajuda?")
                        .setMessage("Ligue para o plantão da DiTI (048)3271-1171 ou abra um SAU no site do CBMSC.")
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                }
            });

                txtId = (EditText) findViewById(R.id.txtLoginUser);
                //txtId.setText(Globals.getUserLogin(this));
                txtPwd = (EditText) findViewById(R.id.txtLoginPassword);

                /**
                 * Appears a hack
                 * On login_activity I added
                 * android:focusable="true"
                 * android:focusableInTouchMode="true"
                 */
                txtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            txtPwd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   // InputMethodManager keyboard = (InputMethodManager)
                                      //    getSystemService(Context.INPUT_METHOD_SERVICE);
                                   // keyboard.showSoftInput(txtId, 0);
                                }
                            }, 200);
                        }
                    }
                });
        final ImageButton icon_face = (ImageButton) findViewById(R.id.icon_face);
        icon_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.setUrlSocial("https://pt-br.facebook.com/CBMSC");
                Intent intent = new Intent(LoginActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton icon_twitter = (ImageButton) findViewById(R.id.icon_twitter);
        icon_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.setUrlSocial("https://twitter.com/CBMSC193");
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
                Globals.setUrlSocial("http://www.cbm.sc.gov.br/hotsite/");
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

                Processo meu = new Processo(getBaseContext());
                meu.execute();
            }

            public class Processo extends AsyncTask<String, String, String> {


                private String retornoHttp = "";
                public Context context;
                public Processo(Context context) {
                    this.context = context;
                }
                protected List<NameValuePair> params = new ArrayList<>();


                @Override
                protected void onPreExecute() {
                    //ANTES DE EXECUTAR (JANELA)
                    pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.login_in), getString(R.string.please_hold), true);



                }

                @Override
                protected String doInBackground(String... paramss) {
                    try {
                        String URL = Globals.SERVER_CBM + "ldap.conf.bombcast.php";
                        params.add(new BasicNameValuePair("u", txtId.getText().toString()));
                        params.add(new BasicNameValuePair("p", txtPwd.getText().toString()));


                        retornoHttp = ConexaoHttpClient.executaHttpPost(URL,params);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return retornoHttp;
                }

                @Override
                protected void onPostExecute(String result) {

                    if (retornoHttp.equalsIgnoreCase("1")) {

                        Globals.setUserName(txtId.getText().toString());
                        Intent intent = new Intent(LoginActivity.this, Server_193Activity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();

                    }else{
                        if (retornoHttp.equalsIgnoreCase("0")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Tente novamente.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 100);
                            toast.show();

                        txtPwd.setText("");
                        pDialog.dismiss();


                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Problema ao conectar com o E193.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 100);
                            toast.show();
                            pDialog.dismiss();
                        }
                    }
                }

                @Override
                protected void onProgressUpdate(String... values) {

                }
            }
    }
