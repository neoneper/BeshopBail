package com.beshop.develloper.beshop.connections;

import com.beshop.develloper.beshop.data.DataCompany;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Develloper on 22/05/2017.
 */

public interface FBCompanyCallback {
    /**
     * This method is called with the current company of the key. company will be null if there is no location
     * stored in FibreBase for the key.
     * @param key The key whose company we are getting
     * @param company The company data of the key
     */
    public void OnCompanyResult(String key, DataCompany company);

    /**
     * Called if the callback could not be added due to failure on the server or security rules.
     * @param databaseError The error that occurred
     */
    public void onCancelled(DatabaseError databaseError);
}
