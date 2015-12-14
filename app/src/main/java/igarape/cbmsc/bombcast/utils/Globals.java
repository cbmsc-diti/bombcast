package igarape.cbmsc.bombcast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import igarape.cbmsc.bombcast.BuildConfig;


public class Globals {

    public static String TAG = Globals.class.getName();

    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN";
    private static final String PREF_TIME_LOGIN = "PREF_TIME_LOGIN";

    public static final String SENDER_ID = "559090531786";
    public static final String PREF_FILE_NAMES = "PREF_FILE_NAMES";
    public static final String DATA = "DATA";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String SERVER_URL_WEB = BuildConfig.serverUrl;
    public static final String SERVER_CBM = BuildConfig.serverCBM;
    public static final String DIRECTORY_SIZE = "DIRECTORY_SIZE";
    public static final String DIRECTORY_UPLOADED_SIZE = "DIRECTORY_UPLOADED_SIZE";
    public static final String AUTH = "AUTH";

    public static final String PAGINA_JOTAS       = SERVER_CBM +"j_ocorrencia.bombcast2.php";
    public static final String PAGINA_OCORRENCIAS = SERVER_CBM + "rec_coord.bombcast2.php";
    public static final String PAGINA_VIATURAS    = SERVER_CBM + "sel_vtr.bombcast2.php";
    public static final String PAGINA_LOCATIONS   = SERVER_CBM + "locations/rec_locations.php";
    public static final String PAGINA_CONEXAO     = SERVER_CBM + "ldap.conf.bombcast.php";
    public static final String PAGINA_CIDADES     = SERVER_CBM +"firecast_cidades.php";
    public static final String PAGINA_COBOM       = "http://aplicativosweb.cbm.sc.gov.br/cobom/e193/#";
    public static final String PAGINA_HIDRANTES   = "http://www.cbm.sc.gov.br/intranet/relatorios_gestores/relatorio_administrativo/mapeamento/m/?host=";

    private static String accessToken = null;
    private static String userName = null;
    private static Long directoryUploadedSize;
    private static String ServidorSelecionado = null;
    private static String VtrSelecionada = "";
    private static String Monitor = "";
    private static String Latitude = null;
    private static String Longitude = null ;
    private static String Id_Ocorrencia="";
    private static String viaturaOcorrencia;
    private static String servidorRadioSelecionado;
    private static Double LatAlteracao = 0.0;
    private static String nomeServidorRadioSelecionado;
    private static String userPwd="";
    private static Double LngAlteracao = 0.0;
    private static String UrlSocial = "";


    public static String getPaginaLocations() {
        return PAGINA_LOCATIONS;
    }
    public static String getNomeServidorRadioSelecionado() {
        return nomeServidorRadioSelecionado;
    }
    public static String getPaginaViaturas() {
        return PAGINA_VIATURAS;
    }
    public static String getServidorRadioSelecionado() {
        return servidorRadioSelecionado;
    }
    public static String getUserPwd() {
        return userPwd;
    }
    public static String getPaginaJotas() {
        return PAGINA_JOTAS;
    }
    public static void setUserPwd(String userPwd) {
        Globals.userPwd = userPwd;
    }
    public static List<String> getListaHospitais() {
        return listaHospitais;
    }
    public static void setListaHospitais(List<String> listaHospitais) {
        Globals.listaHospitais = listaHospitais;
    }
    private static List<String> listaHospitais = new ArrayList<>();
    public static String getUrlSocial() {
        return UrlSocial;
    }
    public static void setUrlSocial(String urlSocial) {
        UrlSocial = urlSocial;
    }
    public static Double getLngAlteracao() {
        return LngAlteracao;
    }
    public static void setLngAlteracao(Double lngAlteracao) {
        LngAlteracao = lngAlteracao;
    }
    public static Double getLatAlteracao() {
        return LatAlteracao;
    }
    public static void setLatAlteracao(Double latAlteracao) {
        LatAlteracao = latAlteracao;
    }
    public static String getId_Ocorrencia() {
        return Id_Ocorrencia;
    }
    public static void setId_Ocorrencia(String id_Ocorrencia) {
        Id_Ocorrencia = id_Ocorrencia;
    }
    public static String getLatitude() {
        return Latitude;
    }
    public static void setLatitude() {
        Latitude = getLatAlteracao().toString();
    }
    public static String getLongitude() {
        return Longitude;
    }
    public static void setLongitude() {
        Longitude = getLngAlteracao().toString();
    }
    public static String getMonitor() {
        return Monitor;
    }
    public static void setMonitor(String monitor) {
        Monitor = monitor;
    }
    public synchronized static String getAccessToken(Context context) {
        if (accessToken == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
            accessToken = sharedPrefs.getString(PREF_ACCESS_TOKEN, null);
        }
        return accessToken != null ? "Bearer " + accessToken : null;
    }
    public synchronized static void setAccessToken(Context context, String token) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_ACCESS_TOKEN, token);
        editor.putLong(PREF_TIME_LOGIN, java.lang.System.currentTimeMillis());
        editor.apply();
        accessToken = token;
    }
    public synchronized static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
    public synchronized static String getRegistrationId(Context context) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String registrationId = sharedPrefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = sharedPrefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    public static void setUserName(String userName) {
        Globals.userName = userName;
    }
    public static String getUserName() {
        return Globals.userName;
    }
    public static void setServidorSelecionado(String ServidorSelecionado) {
        Globals.ServidorSelecionado = ServidorSelecionado;
    }
    public static String getServidorSelecionado() {
        return Globals.ServidorSelecionado;
    }
    public static void setVtrSelecionada(String VtrSelecionada) {
          Globals.VtrSelecionada = VtrSelecionada;
    }
    public static String getVtrSelecionada() {
        return Globals.VtrSelecionada;
    }
    public static void clear(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear().apply();

        accessToken = null;
        userName = null;
    }
    public static void setDirectorySize(Context context,Long directorySize) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(DIRECTORY_SIZE, directorySize);
        editor.apply();
        setDirectoryUploadedSize(context, (long) 0);
    }
    public static Long getDirectoryUploadedSize(Context context) {

        if (directoryUploadedSize == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
            directoryUploadedSize = sharedPrefs.getLong(DIRECTORY_UPLOADED_SIZE, 0);
        }
        return directoryUploadedSize;
    }
    public static void setDirectoryUploadedSize(Context context,Long directoryUploadedSize) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(DIRECTORY_UPLOADED_SIZE, directoryUploadedSize);
        editor.apply();
        Globals.directoryUploadedSize = directoryUploadedSize;
    }
    public static void setViaturaOcorrencia(String viaturaOcorrencia) {
        Globals.viaturaOcorrencia = viaturaOcorrencia;
    }
    public static String getviaturaOcorrencia() {
        return viaturaOcorrencia;
    }
    public static void setServidorRadioSelecionado(String servidorRadioSelecionado) {
        Globals.servidorRadioSelecionado = servidorRadioSelecionado;
    }
    public static void setNomeServidorRadioSelecionado(String nomeServidorRadioSelecionado) {
        Globals.nomeServidorRadioSelecionado = nomeServidorRadioSelecionado;
    }
}
