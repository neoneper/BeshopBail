package com.beshop.develloper.beshop;

import android.*;
import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Develloper on 22/05/2017.
 */

public class Beshop {

    public static final int REQUEST_PERMISSIONS_LOCATION_FINE = 1;
    public static final int REQUEST_PERMISSIONS_LOCATION_CROSS = 2;

    public static Beshop instance;

    private Context context;
    private Intent intentLocationService;
    private Intent intentLocationAlarm;

    public Beshop(Context context) {
        this.context = context;
        Beshop.instance = this;
    }


    public static boolean isLocationServiceRunning(Context context) {
        return isServiceRunning(BeshopLocationService.class, context);

    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {

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

        if (Beshop.isLocationServiceRunning(this.context) == false) {
            //Iniciando o GoogleService em Background
            if (intentLocationService == null)
                intentLocationService = new Intent(context, BeshopLocationService.class);
            context.startService(intentLocationService);
            // context.bindService(intentLocationService, mServerConn, context.BIND_AUTO_CREATE);
            Log.e("BeshopLocationService", "Is started");
        } else {
            Log.e("BeshopLocationService", "Already is started");
        }
    }

    public void StopLocationService() {
        if (Beshop.isLocationServiceRunning(this.context)) {
            this.context.stopService(new Intent(this.context, BeshopLocationService.class));
            Log.e("BeshopLocationService", "Is stoped");
            // context.unbindService(mServerConn);
        }
    }

    public static void NotfyLocal(final Context context, String title, String msg, boolean vibrate, boolean alert) {
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

    public static void NotfyLocal(final Context context, Class<?> pendingClass, String title, String msg, boolean vibrate, boolean alert) {
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

    public static boolean IsLocationEnnabled(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnable;
    }

    public boolean IsLocationEnnabled() {
        return Beshop.IsLocationEnnabled(this.context);
    }

    public static boolean IsNetworkLocationEnnabled(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnable;
    }

    public boolean IsNetworkLocationEnnabled() {
        return Beshop.IsNetworkLocationEnnabled(this.context);
    }

    public static void ShowDialogGPSEnnable(final Context context, final DialogOptionsCallback callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS Inativo");  // GPS not found
        builder.setMessage("Ative seu GPS"); // Want to enable?
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                if (callback != null)
                    callback.OnConfirm();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null)
                    callback.OnCancel();
            }
        });

        builder.create().show();
    }

    public void ShowDialogGPSEnnable(final DialogOptionsCallback callback) {
        Beshop.ShowDialogGPSEnnable(this.context, callback);
    }

    public static boolean IsLocationAlarm(Context context) {
        return (PendingIntent.getBroadcast(context, 0, new Intent("ALARM_LOCATION_UPDATE"), PendingIntent.FLAG_NO_CREATE) != null);
    }


    public void StartLocationAlarm(int startSecond, long loopTimeMiles) {
        if (Beshop.IsLocationAlarm(this.context)) {
            Log.e("ALARM_LOCATION_UPDATE", "Alread started");
            return;
        }

        if (intentLocationAlarm == null)
            intentLocationAlarm = new Intent("ALARM_LOCATION_UPDATE");

        PendingIntent p = PendingIntent.getBroadcast(context, 0, intentLocationAlarm, 0);

        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
       // calendar.add(Calendar.SECOND, startSecond);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), loopTimeMiles, p);
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000,p);
    }

    public void StopLocationAlarm() {
        if (Beshop.IsLocationAlarm(this.context))
            return;
        if (intentLocationAlarm == null)
            return;
        PendingIntent p = PendingIntent.getBroadcast(context, 0, intentLocationAlarm, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(p);

    }

    public static boolean isLocationFinePermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isLocationCrossPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean RequestLocationFinePermission(MainActivity activity) {
        boolean result = false;
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_LOCATION_FINE);
        } else
            result = true;

        return result;
    }

    public boolean RequestLocationCorsePermission(MainActivity activity) {
        boolean result = false;
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_LOCATION_CROSS);
        } else
            result = true;

        return result;
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
