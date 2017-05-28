package com.beshop.develloper.beshop.connections;

import android.util.Log;

import com.beshop.develloper.beshop.data.DataInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Develloper on 22/05/2017.
 */

public class FBInformation {
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
    private static class InformationValueEventListener implements ValueEventListener {

        private final FBInformationCallback callback;

        InformationValueEventListener(FBInformationCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            if (dataSnapshot.getValue() == null) {
                this.callback.OnInformationResult(dataSnapshot.getKey(), null);
            } else {

                DataInformation information = FBInformation.getDataValue(dataSnapshot);

                if (information != null) {
                    this.callback.OnInformationResult(dataSnapshot.getKey(), information);
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

    static DataInformation getDataValue(DataSnapshot dataSnapshot) {
        try {

            return dataSnapshot.getValue(DataInformation.class);

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

    public FBInformation(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public FBInformation(String databaseRefence) {

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


    public void setInformation(DataInformation information) {
        setInformation(information, null);
    }

    public void setInformation(final DataInformation information, final CompletionListener completionListener) {

        if (information == null) {
            throw new NullPointerException();
        }

        final DatabaseReference keyRef = this.getDatabaseRefForKey(information.Beacon);

        // company.CreatedDate = new Date().getTime();

        if (completionListener != null) {

            keyRef.setValue(information, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    completionListener.onComplete(keyRef.getKey(), databaseError);
                }
            });
        } else {
            keyRef.setValue(information);
        }
    }

    public void removeInformation(String key) {
        this.removeInformation(key, null);
    }

    /**
     * Removes the beacon for a key from this Firebase.
     *
     * @param key                The key to remove from this Firebase
     * @param completionListener A completion listener that is called once the beacons is successfully removed
     *                           from the server or an error occurred
     */
    public void removeInformation(final String key, final CompletionListener completionListener) {
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
    public void getInformation(String key, FBInformationCallback callback) {
        DatabaseReference keyRef = this.getDatabaseRefForKey(key);
        InformationValueEventListener valueListener = new InformationValueEventListener(callback);
        keyRef.addListenerForSingleValueEvent(valueListener);
    }

    public static DataInformation CreateInformation(String title, String company, String beacon, String description)
    {
        DataInformation data = new DataInformation();
        data.Name = title;
        data.Description = description;
        data.Company=company;
        data.Beacon=beacon;
        data.Type="Promo";
        return data;
    }
}
