package igarape.cbmsc.bombcast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import igarape.cbmsc.bombcast.BuildConfig;

import static java.net.URLEncoder.encode;

/**
 * Created by fcavalcanti on 28/10/2014.
 */
public class Globals {

    public static String TAG = Globals.class.getName();
    public static final String SENDER_ID = "559090531786";
    private static final String PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN";
    public static final String PREF_FILE_NAMES = "PREF_FILE_NAMES";
    public static final String DATA = "DATA";
    private static final String PREF_TIME_LOGIN = "PREF_TIME_LOGIN";
    private static final String PREF_USER_LOGIN = "PREF_USER_LOGIN";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String SERVER_URL_WEB = BuildConfig.serverUrl;
    public static final String DIRECTORY_SIZE = "DIRECTORY_SIZE";
    public static final String DIRECTORY_UPLOADED_SIZE = "DIRECTORY_UPLOADED_SIZE";
    public static final String SERVER_CBM = "https://aplicativosweb.cbm.sc.gov.br/ebm/" ;
    public static final String AUTH = "AUTH";
    private static String accessToken = null;
    private static String userLogin = null;
    private static String serverIpAddress = "";
    private static Integer streamingPort = 1935;
    private static String streamingUser = "";
    private static String streamingPassword = "";
    private static String streamingPath = "";
    private static String userName = null;
    private static Bitmap userImage = null;
    private static Long directorySize;
    private static Long directoryUploadedSize;
    private static String ServidorSelecionado = null;
    private static String VtrSelecionada = "";
    private static String Monitor = "";
    private static String Latitude = "0";
    private static String Longitude = "0";
    private static String EnderecoOcorrencia = "Santa Catarina";
    private static String Id_Ocorrencia="";

    public static String getDeslocando_para() {
        return deslocando_para;
    }

    public static void setDeslocando_para(String deslocando_para) {
        Globals.deslocando_para = deslocando_para;
    }

    private static String deslocando_para = null;
    private static Double LatAlteracao = 0.0;
    private static Double LngAlteracao = 0.0;
    public static final long GPS_REPEAT_TIME = 1000 * 15; // 15 seconds
    private static boolean toggling;


    public static Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public static void setLastKnownLocation(Location lastKnownLocation) {
        Globals.lastKnownLocation = lastKnownLocation;
    }

    private static Location lastKnownLocation = null;


    private static String UrlSocial = "";

    private static String TelefoneCmt = "";

    public static String getUrlSocial() {
        return UrlSocial;
    }

    public static void setUrlSocial(String urlSocial) {
        UrlSocial = urlSocial;
    }

    public static String getTelefoneCmt() {
        return TelefoneCmt;
    }

    public static void setTelefoneCmt(String telefoneCmt) {
        TelefoneCmt = telefoneCmt;
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

    public static String getEnderecoOcorrencia() {
        return EnderecoOcorrencia;
    }

    public static void setEnderecoOcorrencia(String enderecoOcorrencia) {
        EnderecoOcorrencia = enderecoOcorrencia;
    }

    public static String getLatitude() {
        return Latitude;
    }

    public static void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public static String getLongitude() {
        return Longitude;
    }

    public static void setLongitude(String longitude) {
        Longitude = longitude;
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

    public synchronized static String getAccessTokenStraight(Context context) {
        if (accessToken == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
            accessToken = sharedPrefs.getString(PREF_ACCESS_TOKEN, null);
        }
        return accessToken != null ? accessToken : null;
    }

    public synchronized static void setAccessToken(Context context, String token) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_ACCESS_TOKEN, token);
        editor.putLong(PREF_TIME_LOGIN, java.lang.System.currentTimeMillis());
        editor.commit();
        accessToken = token;
        if (accessToken == null) {
            setUserImage(null);
        }
    }

    public synchronized static String getUserLogin(Context context) {
        if (userLogin == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
            userLogin = sharedPrefs.getString(PREF_USER_LOGIN, null);
        }
        return userLogin;
    }

    public synchronized static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
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

    public synchronized static void setUserLogin(Context context, String login) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_USER_LOGIN, login);
        editor.commit();
    }

    public static Bitmap getUserImage() {
        return userImage;
    }

    public static void setUserImage(Bitmap userImage) {
        Globals.userImage = userImage;
    }

    public static void setStreamingPort(Integer streamingPort) {
        Globals.streamingPort = streamingPort;
    }

    public static void setStreamingUser(String streamingUser) {
        Globals.streamingUser = streamingUser;
    }

    public static void setServerIpAddress(String serverIpAddress) {
        Globals.serverIpAddress = serverIpAddress;
    }

    public static void setStreamingPassword(String streamingPassword) {
        Globals.streamingPassword = streamingPassword;
    }

    public static void setStreamingPath(String streamingPath) {
        Globals.streamingPath = streamingPath;
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

      try {

          Globals.VtrSelecionada = encode( VtrSelecionada,"ISO-8859-1");
      }catch (Exception e){
          e.printStackTrace();
      }


    }

    public static String getVtrSelecionada() {
        return Globals.VtrSelecionada;
    }


    public static void setAccessToken(String accessToken) {
        Globals.accessToken = accessToken;
    }

    public static void clear(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear().commit();

        accessToken = null;
        userLogin = null;
        serverIpAddress = "";
        streamingPort = 1935;
        streamingUser = "";
        streamingPassword = "";
        streamingPath = "";
        userName = null;
        userImage = null;
    }

    public static void setDirectorySize(Context context,Long directorySize) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(DIRECTORY_SIZE, directorySize);
        editor.commit();
        Globals.directorySize = directorySize;
        setDirectoryUploadedSize(context, Long.valueOf(0));
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
        editor.commit();
        Globals.directoryUploadedSize = directoryUploadedSize;
    }

    public static Long getDirectorySize(Context context) {
        if (directorySize == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
            directorySize = sharedPrefs.getLong(DIRECTORY_SIZE, 0);
        }
        return directorySize;
    }

    public static Long getDirectorySize() {
        return directorySize;
    }

    public static String getServerIpAddress() {
        return serverIpAddress;
    }

    public static Integer getStreamingPort() {
        return streamingPort;
    }

    public static String getStreamingUser() {
        return streamingUser;
    }

    public static String getStreamingPassword() {
        return streamingPassword;
    }

    public static String getStreamingPath() {
        return streamingPath;
    }

    public static void setToggling(boolean toggling) {
        Globals.toggling = toggling;
    }

    public static boolean isToggling() {
        return toggling;
    }
}
