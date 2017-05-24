package com.beshop.develloper.beshop;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Develloper on 22/05/2017.
 */

public class Beshop {

    public static Beshop instance;

    private Context context;
    private Intent intentLocationService;

    public Beshop(Context context) {
        this.context = context;
        Beshop.instance = this;
    }

    public boolean isLocationServiceRunning() {
        return isServiceRunning(BeshopLocationService.class);
    }

    public boolean isServiceRunning(Class<?> serviceClass) {

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getShortClassName().contains(serviceClass.getSimpleName()) == true) {
                return true;
            }
        }
        return false;
    }

    public void StartLocationService() {

        if (isLocationServiceRunning() == false) {
            //Iniciando o GoogleService em Background
            intentLocationService = new Intent(context, BeshopLocationService.class);
            context.startService(intentLocationService);
            // context.bindService(intentLocationService, mServerConn, context.BIND_AUTO_CREATE);
            Log.e("BeshopLocationService","Is started");
        }
        else
        {
            Log.e("BeshopLocationService","Already is started");
        }
    }

    public void StopLocationService() {
        if (isLocationServiceRunning()) {
            context.stopService(new Intent(context, BeshopLocationService.class));
            Log.e("BeshopLocationService","Is stoped");
            // context.unbindService(mServerConn);
        }
    }

    public static void NotfyLocal(Context context, String title, String msg, boolean vibrate, boolean alert) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(msg);
        mBuilder.setWhen(System.currentTimeMillis());
        if (vibrate)
            mBuilder.setVibrate(new long[]{300, 300});
        if (alert)
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Notification notification = mBuilder.build();

        NotificationManagerCompat.from(context).notify(0, notification);
    }

    public static void NotfyLocal(Context context, Class<?> pendingClass, String title, String msg, boolean vibrate, boolean alert) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(msg);
        mBuilder.setWhen(System.currentTimeMillis());
        if (vibrate)
            mBuilder.setVibrate(new long[]{300, 300});
        if (alert)
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, pendingClass), 0);
        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();

        NotificationManagerCompat.from(context).notify(0, notification);
    }

    public static Date getDate(long date) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String dateAsString = sdf.format(date);
            Date d = sdf.parse(dateAsString);
            return d;
        } catch (Exception ex) {
            Log.e("getDate error", ex.getMessage());
            return null;
        }
    }

}
