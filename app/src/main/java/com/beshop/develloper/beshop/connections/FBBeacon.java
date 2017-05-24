package com.beshop.develloper.beshop.connections;

import android.util.Log;

import com.beshop.develloper.beshop.data.DataBeacon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Date;

/**
 * Created by Develloper on 22/05/2017.
 */

public class FBBeacon {

    /**
     * A listener that can be used to be notified about a successful write or an error on writing.
     */
    public static interface CompletionListener {
        /**
         * Called once a location was successfully saved on the server or an error occurred. On success, the parameter
         * error will be null; in case of an error, the error will be passed to this method.
         *
         * @param key   The key whose location was saved
         * @param error The error or null if no error occurred
         */
        public void onComplete(String key, DatabaseError error);
    }

    /**
     * A small wrapper class to forward any events to the BeaconEventListener.
     */
    private static class BeaconValueEventListener implements ValueEventListener {

        private final FBBeaconCallback callback;

        BeaconValueEventListener(FBBeaconCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null) {
                this.callback.OnBeaconResult(dataSnapshot.getKey(), null);
            } else {

                DataBeacon beacon = FBBeacon.getDataValue(dataSnapshot);

                if (beacon != null) {
                    this.callback.OnBeaconResult(dataSnapshot.getKey(), beacon);
                } else {
                    String message = "Firebase beacon data has invalid format: " + dataSnapshot.getValue();
                    this.callback.onCancelled(DatabaseError.fromException(new Throwable(message)));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            this.callback.onCancelled(databaseError);
        }
    }

    static DataBeacon getDataValue(DataSnapshot dataSnapshot) {
        try {
            DataBeacon beacon = new DataBeacon();
            beacon = dataSnapshot.getValue(DataBeacon.class);
            return beacon;

        } catch (NullPointerException e) {
            Log.e("getBeaconValue Error:", e.getMessage());
            return null;
        } catch (ClassCastException e) {
            Log.e("getBeaconValue Error:", e.getMessage());
            return null;
        }
    }

    private final DatabaseReference databaseReference;
    //private final EventRaiser eventRaiser;

    public FBBeacon(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public FBBeacon(String databaseRefence) {

        this.databaseReference = FirebaseDatabase.getInstance().getReference(databaseRefence);
    }

    /**
     * @return The Firebase reference this Firebase instance uses
     */
    public DatabaseReference getDatabaseReference() {
        return this.databaseReference;
    }

    DatabaseReference getDatabaseRefForKey(String key) {
        return this.databaseReference.child(key);
    }

    public static DataBeacon createBeacon(String name, String UUID, String company) {
        DataBeacon beacon = new DataBeacon();
        beacon.Name = name;
        beacon.UUID = UUID;
        beacon.Company = company;
        beacon.CreatedDate = new Date().getTime();
        return beacon;
    }

    public void setBeacon(DataBeacon beacon) {
        setBeacon(beacon, null);
    }

    public void setBeacon(final DataBeacon beacon, final CompletionListener completionListener) {

        if (beacon == null) {
            throw new NullPointerException();
        }

        final DatabaseReference keyRef = this.getDatabaseRefForKey(beacon.Name);

        beacon.CreatedDate = new Date().getTime();

        if (completionListener != null) {

            keyRef.setValue(beacon, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    completionListener.onComplete(keyRef.getKey(), databaseError);
                }
            });
        } else {
            keyRef.setValue(beacon);
        }
    }

    public void removeBeacon(String key) {
        this.removeBeacon(key, null);
    }

    /**
     * Removes the beacon for a key from this Firebase.
     *
     * @param key                The key to remove from this Firebase
     * @param completionListener A completion listener that is called once the beacons is successfully removed
     *                           from the server or an error occurred
     */
    public void removeBeacon(final String key, final CompletionListener completionListener) {
        if (key == null) {
            throw new NullPointerException();
        }
        DatabaseReference keyRef = this.getDatabaseRefForKey(key);
        if (completionListener != null) {
            keyRef.setValue(null, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    completionListener.onComplete(key, databaseError);
                }
            });
        } else {
            keyRef.setValue(null);
        }
    }

    /**
     * Gets the current location for a key and calls the callback with the current value.
     *
     * @param key      The key whose location to get
     * @param callback The callback that is called once the location is retrieved
     */
    public void getBeacon(String key, FBBeaconCallback callback) {
        DatabaseReference keyRef = this.getDatabaseRefForKey(key);
        BeaconValueEventListener valueListener = new BeaconValueEventListener(callback);
        keyRef.addListenerForSingleValueEvent(valueListener);
    }

}
