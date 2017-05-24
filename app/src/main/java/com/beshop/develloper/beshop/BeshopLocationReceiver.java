package com.beshop.develloper.beshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Develloper on 23/05/2017.
 */

public class BeshopLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, BeshopLocationService.class);
        context.startService(myIntent);

    }
}
