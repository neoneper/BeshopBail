package com.beshop.develloper.beshop;

import com.beshop.develloper.beshop.data.DataGeoLocation;

/**
 * Created by Develloper on 28/05/2017.
 */

public interface BeshopServiceCallback {

    public void OnGeoLocationAdd(DataGeoLocation data);
    public void OnGeoLocationRemoded(DataGeoLocation data);
    public void OnGeoLocationUpdate(DataGeoLocation data);
}
