package com.beshop.develloper.beshop.data;

import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.GeoUtils;

/**
 * Created by Develloper on 27/05/2017.
 */

public class DataGeoLocation {

    String Key;
    GeoLocation geoLocation;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public DataGeoLocation(String Key, GeoLocation geoLocation) {
        this.Key = Key;
        this.geoLocation = geoLocation;
    }

    public Location getToLocation() {
        Location location = new Location(Key);
        location.setLatitude(geoLocation.latitude);
        location.setLongitude(geoLocation.longitude);
        return location;
    }

    //return meeters distance
    public double getDistance(GeoLocation geoTo) {
        Location a = getToLocation();
        Location b = new Location("to");
        b.setLatitude(geoTo.latitude);
        b.setLongitude(geoTo.longitude);

        return a.distanceTo(b);
    }
    //return meeters distance
    public double getDistance(double latitude, double longetude) {
        Location a = getToLocation();
        Location b = new Location("to");
        b.setLatitude(latitude);
        b.setLongitude(longetude);

        return a.distanceTo(b);
    }

}
