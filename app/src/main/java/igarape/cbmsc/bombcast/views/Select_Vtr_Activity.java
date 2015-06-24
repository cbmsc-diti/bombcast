package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.hive.UploadVideoActivity;
import igarape.cbmsc.bombcast.utils.ConexaoHttpClient;
import igarape.cbmsc.bombcast.utils.Globals;

public class Select_Vtr_Activity extends Activity {

    private List<String> vtrs = new ArrayList<>();
    private String vtr_sel;
    private String servidor193 = Globals.getServidorSelecionado();
    private String usuario = Globals.getUserName();
    EditText et_telefone;
    TextView nm_cmt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vtr);

        et_telefone = (EditText) findViewById(R.id.et_cel_cmt_area);
        nm_cmt = (TextView) findViewById(R.id.tv_nm_cmt);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy);

        String status;
        try {
            status = ConexaoHttpClient.executaHttpGet(Globals.SERVER_CBM + "sel_vtr.bombcast.php?u="+usuario+"&h="+servidor193);
            if(!status.isEmpty()){
                vtrs = Arrays.asList(status.split("\\."));
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.texto_vtr_nao_cadastrada))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent2 = new Intent(Select_Vtr_Activity.this, LoginActivity.class);
                                startActivity(intent2);
                                Select_Vtr_Activity.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Identifica o Spinner no layout
        Spinner sp_vtrs = (Spinner) findViewById(R.id.sp_vtrs);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, vtrs);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        sp_vtrs.setAdapter(spinnerArrayAdapter);
        //Método do Spinner para capturar o item selecionado
        sp_vtrs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                vtr_sel = parent.getItemAtPosition(posicao).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        try {
            status = ConexaoHttpClient.executaHttpGet(Globals.SERVER_CBM + "sel_vtr.bombcast.php?u="+usuario+"&h="+servidor193+"&vf=1");
            if(!status.isEmpty()){
                List<String> cmta = Arrays.asList(status.split("\\."));
                String telefone = cmta.get(2);
                String idCmt = cmta.get(0) +" "+ cmta.get(1);
                ((EditText) findViewById(R.id.et_cel_cmt_area)).setText(telefone);
                ((TextView) findViewById(R.id.tv_nm_cmt)).setText(idCmt);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.msg_tel_nao_encontrado))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.setTelefoneCmt(et_telefone.getText().toString());
                Globals.setVtrSelecionada(vtr_sel);

                Intent intent = new Intent(Select_Vtr_Activity.this, Ocorrencia_Activity.class);
                startActivity(intent);
            }
        });

        final Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent3 = new Intent(Select_Vtr_Activity.this, Server_193Activity.class);
                startActivity(intent3);
                Select_Vtr_Activity.this.finish();

            }
        });

        final Button btn_edit_telefone = (Button) findViewById(R.id.btn_edit_telefone);
        btn_edit_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.et_cel_cmt_area).setEnabled(true);
                ((TextView) findViewById(R.id.tv_nm_cmt)).setText(getString(R.string.telefone_editado));
            }
        });
        final Button btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(Select_Vtr_Activity.this, UploadVideoActivity.class);
                startActivity(intent4);
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}