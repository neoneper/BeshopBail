package com.beshop.develloper.beshop;

import android.content.Context;

import com.beshop.develloper.beshop.data.DataGeoLocation;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Develloper on 27/05/2017.
 */

public class BeshopService {

    private double fixedLatitude = -25.478085;
    private double fidexLongetude = -49.281743;
    private double fixedRange = 12.0;//km

    public double getFixedLatitude() {
        return fixedLatitude;
    }

    public void setFixedLatitude(double fixedLatitude) {
        this.fixedLatitude = fixedLatitude;
    }

    public double getFidexLongetude() {
        return fidexLongetude;
    }

    public void setFidexLongetude(double fidexLongetude) {
        this.fidexLongetude = fidexLongetude;
    }

    public double getFixedRange() {
        return fixedRange;
    }

    public void setFixedRange(double fixedRange) {
        this.fixedRange = fixedRange;
    }

    public Context context;
    public FirebaseDatabase database;
    public DatabaseReference databaseReference;

    Map<String, DataGeoLocation> GeoLocations;

    public BeshopService(Context context) {
        GeoLocations = new HashMap<String, DataGeoLocation>();

        this.context = context;

        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        databaseReference = database.getReference().child("Geofire");
        databaseReference.keepSynced(true);
    }

    public void SetPersistentDatabase() {
        database.setPersistenceEnabled(true);
        databaseReference.keepSynced(true);
    }

    public void UnsetPersistentDatabase() {
        database.setPersistenceEnabled(false);
        databaseReference.keepSynced(false);
    }

    public void AddGeoLocation(String key, GeoLocation geolocation) {
        AddGeoLocation(key, geolocation, null);
    }

    public void AddGeoLocation(String key, GeoLocation geolocation, BeshopServiceCallback callback) {
        if (GeoLocations.containsKey(key))
            return;

        DataGeoLocation data = new DataGeoLocation(key, geolocation);
        GeoLocations.put(key, data);

        if(callback!=null)
            callback.OnGeoLocationAdd(data);
    }

    public void UpdateGeoLocation(String key, GeoLocation geolocation) {
        UpdateGeoLocation(key,geolocation,null);
    }
    public void UpdateGeoLocation(String key, GeoLocation geolocation, BeshopServiceCallback callback) {
        if (!GeoLocations.containsKey(key))
            return;

        GeoLocations.get(key).setGeoLocation(geolocation);
        if(callback!=null)
            callback.OnGeoLocationUpdate(GeoLocations.get(key));
    }

    public void RemoveGeoLocation(String key) {
       RemoveGeoLocation(key,null);
    }
    public void RemoveGeoLocation(String key, BeshopServiceCallback callback) {
        if (!GeoLocations.containsKey(key))
            return;

        DataGeoLocation data = GeoLocations.get(key);
        GeoLocations.remove(key);

        if(callback!=null)
            callback.OnGeoLocationRemoded(data);

    }

    public DataGeoLocation GetGeoLocation(String key) {
        if (!GeoLocations.containsKey(key))
            return null;

        return GeoLocations.get(key);

    }

    //Retorna localizações proximas ou iguais a metragem espesificada
    public List<DataGeoLocation> GetGeoLocationsNear(double lat, double lon, double meeters) {
        List<DataGeoLocation> location = new ArrayList<DataGeoLocation>();

        for (String Key : GeoLocations.keySet()) {
            DataGeoLocation l = GeoLocations.get(Key);
            if (l.getDistance(lat, lon) <= meeters) {
                location.add(l);
            }
        }

        return location;
    }

}
