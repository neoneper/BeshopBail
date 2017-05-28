package com.beshop.develloper.beshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQueryEventListener;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Develloper on 26/05/2017.
 */

public class BeshopLocationReiverAlarm extends BroadcastReceiver {

    LocationManager locationManager = null;
    Location location = null;
    double fixedLatitude = 37.7832;
    double fidexLongetude = -122.4056;
    double fixedRange = 6.0;//km
    double teste = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Beshop.instance==null)
        {
            if (!Beshop.isServiceRunning(BeshopLocationService.class, context)) {
                Log.e("LocationAlarm","alarm start location service");
                Intent myIntent = new Intent(context, BeshopLocationService.class);
                context.startService(myIntent);
            }
        }
        else
        {
            Log.e("LocationAlarm","alarm wait for beshop closed");
        }

    }

}
