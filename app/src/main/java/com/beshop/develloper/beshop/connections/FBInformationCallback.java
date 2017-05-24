package com.beshop.develloper.beshop.connections;

import com.beshop.develloper.beshop.data.DataInformation;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Develloper on 22/05/2017.
 */

public interface FBInformationCallback {
    /**
     * This method is called with the current company of the key. company will be null if there is no location
     * stored in FibreBase for the key.
     * @param key The key whose company we are getting
     * @param information The company data of the key
     */
    public void OnInformationResult(String key, DataInformation information);

    /**
     * Called if the callback could not be added due to failure on the server or security rules.
     * @param databaseError The error that occurred
     */
    public void onCancelled(DatabaseError databaseError);
}
