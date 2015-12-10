package igarape.cbmsc.bombcast.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Calendar;

/**
 * Created by bruno on 11/14/14.
 */
public class ServiceUtils {

    public static boolean isMyServiceRunning(final Class clazz, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //#######################################################
    public static boolean verificaHora(){
        Calendar calendar = Calendar.getInstance();

        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        int minAtual = calendar.get(Calendar.MINUTE);

        int horaAntes = 7;
        int minAntes = 59;

        int horaDepois = 8;
        int minDepois = 01;


        if ((horaAtual > horaAntes) && (minAntes < minAtual)||(horaAtual == horaDepois) && (minAtual < minDepois) ){
            return true;
        } return false;
    }

}
