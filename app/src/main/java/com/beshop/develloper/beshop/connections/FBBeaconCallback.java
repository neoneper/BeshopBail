package com.beshop.develloper.beshop.connections;

import com.beshop.develloper.beshop.data.DataBeacon;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Develloper on 22/05/2017.
 */

public interface FBBeaconCallback {
    /**
     * This method is called with the current location of the key. beacon will be null if there is no location
     * stored in FibreBase for the key.
     * @param key The key whose beacon we are getting
     * @param beacon The beacon data of the key
     */
    public void OnBeaconResult(String key, DataBeacon beacon);

    /**
     * Called if the callback could not be added due to failure on the server or security rules.
     * @param databaseError The error that occurred
     */
    public void onCancelled(DatabaseError databaseError);
}
