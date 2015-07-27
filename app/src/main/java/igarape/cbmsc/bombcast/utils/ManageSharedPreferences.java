package igarape.cbmsc.bombcast.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import java.util.Set;

public class ManageSharedPreferences {

    private static SharedPreferences preferencesInstance;
    private static SharedPreferences.Editor editorInstance;

    // singleton
    private static SharedPreferences getSharedPreferences(Context context,
                                                          String fileName) {
        if (preferencesInstance == null) {
            preferencesInstance = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE);
        }
        return preferencesInstance;
    }

    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, Integer value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, Float value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, Long value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    //para realizar putSet<String> Ã© necessÃ¡rio APT LVL 11
    //TODO verificar compatibilidade
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putInSharedPreferences(Context context, String fileName,
                                              String key, Set<String> values) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putStringSet(key, values);
        editor.commit();
    }

    public static Integer getIntFromSharedPreference(Context context,
                                                     String fileName, String key) {
        Integer retorno = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getInt(key, 0);
        return retorno;
    }

    public static Float getFloatFromSharedPreference(Context context,
                                                     String fileName, String key) {
        Float retorno = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getFloat(key, 0);
        return retorno;
    }
    public static boolean getBooleanFromSharedPreference(Context context,
                                                         String fileName, String key) {
        boolean retorno = false;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getBoolean(key, false);
        return retorno;
    }
    public static Long getLongFromSharedPreference(Context context,
                                                   String fileName, String key) {
        Long retorno = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getLong(key, 0);
        return retorno;
    }
    public static String getStringFromSharedPreference(Context context,
                                                       String fileName, String key) {
        String retorno = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getString(key, null);
        return retorno;
    }

    //para realizar putSet<String> Ã© necessÃ¡rio APT LVL 11
    //TODO verificar compatibilidade
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getSetStringFromSharedPreference(Context context,
                                                               String fileName, String key) {
        Set<String> retorno = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        retorno = preferences.getStringSet(key, null);
        return retorno;
    }

    public static void removeFromSharedPreference(Context context,
                                                  String fileName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

}