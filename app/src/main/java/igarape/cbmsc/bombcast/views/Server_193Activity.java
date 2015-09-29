package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.entity.Servidores_193;
import igarape.cbmsc.bombcast.utils.Globals;

public class Server_193Activity extends Activity {

    private Servidores_193 servidor_sel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_193);

        startGPS();

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


                Globals.setServidorSelecionado(servidor_sel.getIp());
                Intent intent = new Intent(Server_193Activity.this, Select_Vtr_Activity.class);
                startActivity(intent);
                Server_193Activity.this.finish();
            }
        });

        final Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Server_193Activity.this, LoginActivity.class);
                startActivity(intent2);
                Server_193Activity.this.finish();
            }
        });
    }

    public void startGPS(){
        final LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.gps_desativado));
            builder.setMessage(getString(R.string.gps_desativado_text));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                updateView(locat);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);

    }
    public void updateView(Location locat){
        Double latitude = locat.getLatitude();
        Double longitude = locat.getLongitude();

        Globals.setLatAlteracao(latitude);
        Globals.setLngAlteracao(longitude);

        Globals.setLatitude(latitude.toString());
        Globals.setLongitude(longitude.toString());
    }
}