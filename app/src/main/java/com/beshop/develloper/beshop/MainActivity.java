package com.beshop.develloper.beshop;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.beshop.develloper.beshop.connections.FBBeacon;
import com.beshop.develloper.beshop.connections.FBCompany;
import com.beshop.develloper.beshop.connections.FBInformation;
import com.beshop.develloper.beshop.data.DataBeacon;
import com.beshop.develloper.beshop.data.DataCompany;
import com.beshop.develloper.beshop.data.DataInformation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Beshop beshop;
    boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("Geofire"));

        geoFire.setLocation("Beacon01", new GeoLocation(-25.477857, -49.281711));

        /*SAMPLE DATA SETS

        FBBeacon firebeacon = new FBBeacon("Beacons");
        FBCompany firecompany = new FBCompany("Companies");
        FBInformation fireInformation = new FBInformation("Informations");
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("Geofire"));

        DataBeacon dataBeacon = FBBeacon.createBeacon("Beacon01", "0b01", "Company01");
        DataCompany dataCompany = FBCompany.CreateCompany("Company01");
        dataCompany.Beacons.put("Beacon01","Beacon01");

        DataInformation dataInformation = FBInformation.CreateInformation("Promo Teste","Company01","Beacon01","This is a shor description to the information");

        geoFire.setLocation("Beacon01",new GeoLocation(-25.477857, -49.281711));
        firebeacon.setBeacon(dataBeacon);
        firecompany.setCompany(dataCompany);
        fireinformation.setInformation(dataInformation);
        */

        beshop = new Beshop(this);
        beshop.StopLocationService();

        if (!beshop.isLocationFinePermission(this))
            beshop.RequestLocationFinePermission((MainActivity) this);
        else
            beshop.StartLocationAlarm(0, 1000);

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case Beshop.REQUEST_PERMISSIONS_LOCATION_FINE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //beshop.StartLocationService();
                    beshop.StartLocationAlarm(3, 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
}
