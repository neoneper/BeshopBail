package com.beshop.develloper.beshop;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Develloper on 23/05/2017.
 */
public class BeshopLocationService extends Service
{

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

    private Context context;
    private Handler mHandler;
    //Usado para receber evento de tempo em tempo
    private Timer mTimer = null;
    //Intervalo de tempo para cada disparo
    long notify_interval = 20000;
    //Usado para iniciar, identificar e gerenciar o servido em background
    public static String str_receiver = "com.beshop.develloper.beshop.receiver";
    //Novo intent para trabalhar em background
    Intent intent;
    public int teste = 0;

    public BeshopLocationService() {
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        if (Beshop.instance != null)
            if (Beshop.instance.isLocationServiceRunning()) {
                return;
            }

        //Criando novo disparo de evento por tempo determinado
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToTest(), 0, notify_interval);
        Log.e("BeshopLocationService", "Is Timer started");
        //intent = new Intent(str_receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("BeshopLocationService", "Is Binded");
        return null;
    }


    /*Evento chamado pelo Timmer.Shedule*/
    private class TimerTaskToTest extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    teste++;
                    Log.i("background test", "Estou sendo executado");

                    Beshop.NotfyLocal(context, MainActivity.class, "Beshop", "To aqui", true, true);

                }
            });

        }
    }

}
