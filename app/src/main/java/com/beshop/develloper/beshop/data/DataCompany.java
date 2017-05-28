package com.beshop.develloper.beshop.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Develloper on 22/05/2017.
 */

public class DataCompany {
    public HashMap<String, String> Beacons;
    public HashMap<String, String> Informations;
    public String Name;

    public DataCompany() {
        Beacons = new HashMap<String, String>();
        Informations = new HashMap<String, String>();
    }
}
