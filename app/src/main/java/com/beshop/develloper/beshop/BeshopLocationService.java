package com.beshop.develloper.beshop;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.beshop.develloper.beshop.connections.FBBeacon;
import com.beshop.develloper.beshop.data.DataGeoLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Develloper on 23/05/2017.
 */
public class BeshopLocationService extends Service implements LocationListener, GeoQueryEventListener, BeshopServiceCallback {

    class BeaconEventListner implements ValueEventListener {
        public BeshopService beshopService;
        DatabaseReference databaseReference;


        public BeaconEventListner(BeshopService beshopService) {
            this.beshopService = beshopService;
            databaseReference = beshopService.databaseReference.child("Beacons");
        }

        public void AttachAndSync(String key) {
            databaseReference.child(key).addListenerForSingleValueEvent(this);
            databaseReference.child(key).keepSynced(true);
        }

        public void DettachAndUnsync(String key) {
            databaseReference.child(key).removeEventListener(this);
            databaseReference.child(key).keepSynced(false);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("OnDataBeacon",dataSnapshot.getKey());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    class CompanyEventListner implements ValueEventListener {
        public BeshopService beshopService;
        DatabaseReference databaseReference;

        public CompanyEventListner(BeshopService beshopService) {
            this.beshopService = beshopService;
            databaseReference = beshopService.databaseReference.child("Companies");
        }

        public void AttachAndSync(String key) {
            databaseReference.child(key).addListenerForSingleValueEvent(this);
            databaseReference.child(key).keepSynced(true);
        }

        public void DettachAndUnsync(String key) {
            databaseReference.child(key).removeEventListener(this);
            databaseReference.child(key).keepSynced(false);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("OnDataCompany",dataSnapshot.getKey());

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    class InformationEventListner implements ValueEventListener {
        public BeshopService beshopService;
        DatabaseReference databaseReference;

        public InformationEventListner(BeshopService beshopService) {
            this.beshopService = beshopService;
            databaseReference = beshopService.databaseReference.child("Informations");
        }

        public void AttachAndSync(String key) {
            databaseReference.child(key).addListenerForSingleValueEvent(this);
            databaseReference.child(key).keepSynced(true);
        }

        public void DettachAndUnsync(String key) {
            databaseReference.child(key).removeEventListener(this);
            databaseReference.child(key).keepSynced(false);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e("OnDataInformation",dataSnapshot.getKey());

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private Context context;
    private Handler mHandler;
    //Usado para receber evento de tempo em tempo
    private Timer mTimer = null;
    //Intervalo de tempo para cada disparo
    long notify_interval = 1000 * (60 * 2); //2 Min

    //Gerenciador de gps para capturar o local do usuario
    LocationManager locationManager;
    //Local atual atualizado do usuario
    Location currentLocation;
    //Local anterior ao atual
    Location oldLocation;
    Location lastNotfyLocation;

    boolean isGPSEnable;
    boolean isNetworkEnable;
    boolean isUpdateLocation = false;//Fica true quando o gps estiver procurando a localização atual. false quando encontrado
    boolean isDatabaseReady = false;//Fica true quando o database for sincronziado completamente
    boolean isTimmerStart = false;//Fica quando true quando o temporizador for iniciado

    BeshopService beshopService;
    GeoFire geoFire;
    GeoQuery geoQuery;

    BeaconEventListner beaconEventListner;
    CompanyEventListner companyEventListner;
    InformationEventListner informationEventListner;

    public BeshopLocationService() {
        mHandler = new Handler();

    }

    private void UpdateCurrentLocation() {

        if (isUpdateLocation)
            return;
        if (Beshop.instance != null)
            return;
        if (isDatabaseReady == false)
            return;

        //  locationManager.removeUpdates(this);

        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.e("Location", "gps and network invalid");

            return;
        }

        //Se não houver permisao para localizacao gps por meio dos metodos finos ou groço, paro por aqui. Tratar futuramente
        if (isNetworkEnable) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("LocationMananger", "Acesso não permitido AcessFine ou AcessCrossair");
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            oldLocation = currentLocation;
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (isGPSEnable) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            oldLocation = currentLocation;
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        isUpdateLocation = true;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        context = this;


        //Create mananger service util
        beshopService = new BeshopService(this);

        //Get others database to sync after geolocations synced
        beaconEventListner = new BeaconEventListner(beshopService);
        companyEventListner = new CompanyEventListner(beshopService);
        informationEventListner = new InformationEventListner(beshopService);

        //Create geolocation mananger util to firebase database
        geoFire = new GeoFire(beshopService.databaseReference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(beshopService.getFixedLatitude(), beshopService.getFidexLongetude()), beshopService.getFixedRange());
        geoQuery.addGeoQueryEventListener(this);

        //Create GPS currentLocation mananger
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);


        Log.e("BeshopLocationService", "Is Timer started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("BeshopLocationService", "Is Binded");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        geoQuery.removeGeoQueryEventListener(this);
        geoQuery.removeAllListeners();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("latitude", location.getLatitude() + "");
        Log.e("longitude", location.getLongitude() + "");
        boolean isNoty = false;

        if (oldLocation == null)
            oldLocation = currentLocation;
        if (currentLocation != location)
            currentLocation = location;

        //Procurando por Beacons proximos
        List<DataGeoLocation> geoLocations = beshopService.GetGeoLocationsNear(location.getLatitude(), location.getLongitude(), 500);
        //Verifica a distancia da ultima notificação para não floodar o client
        if (geoLocations.size() > 0) {
            if (lastNotfyLocation != null) {
                if (lastNotfyLocation.distanceTo(currentLocation) > 500) {
                    isNoty = true;
                }
            } else {
                isNoty = true;
            }
        }

        //Avalia a permição para notificação
        if (isNoty) {
            String msg = "Encontramos (" + geoLocations.size() + ") BeShoppers proximos à você! Ative seu Radar!";
            Beshop.NotfyLocal(context, MainActivity.class, "Beshop Radar", msg, true, true);
            lastNotfyLocation = currentLocation;
        }

        locationManager.removeUpdates(this);
        isUpdateLocation = false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("ProviderChanged", provider);

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("ProviderEnnable", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("ProviderDisable", provider);
    }

    //GeoQueryListener

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.e("Beacon", key + " Entered");
        beshopService.AddGeoLocation(key, location, this);
    }

    @Override
    public void onKeyExited(String key) {
        Log.e("Beacon", key + " Exited");
        beshopService.RemoveGeoLocation(key, this);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        beshopService.UpdateGeoLocation(key, location, this);
    }

    @Override
    public void onGeoQueryReady()
    {
        isDatabaseReady = true;

        if (isTimmerStart == false) {
            //Create and start time looping to update current currentLocation and find nears beacons
            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetLocation(), 0, notify_interval);
            isTimmerStart = true;
        }
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.e("GeoFire Error", error.getMessage());
        stopService(new Intent(context, BeshopLocationService.class));
        stopSelf();
        mTimer.cancel();
    }

    //BeshopService data Callbacks
    @Override
    public void OnGeoLocationAdd(DataGeoLocation data) {
        Log.e("OnGeoLocationAdd", data.getKey());
        beaconEventListner.AttachAndSync(data.getKey());
        companyEventListner.AttachAndSync(data.getKey());
        informationEventListner.AttachAndSync(data.getKey());
    }

    @Override
    public void OnGeoLocationRemoded(DataGeoLocation data) {
        Log.e("OnGeoLocationRemoded", data.getKey());

        beaconEventListner.DettachAndUnsync(data.getKey());
        companyEventListner.DettachAndUnsync(data.getKey());
        informationEventListner.DettachAndUnsync(data.getKey());
    }

    @Override
    public void OnGeoLocationUpdate(DataGeoLocation data) {

    }

    /*Evento chamado pelo Timmer.Shedule*/
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    UpdateCurrentLocation();
                    //   Beshop.NotfyLocal(context, MainActivity.class, "Beshop", "LA:" + currentLocation.getLatitude() + " x LO:" + currentLocation.getLongitude(), true, true);
                }
            });
        }
    }


}
