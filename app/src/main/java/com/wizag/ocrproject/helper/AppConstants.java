package com.wizag.ocrproject.helper;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

final class AppConstants {
    private AppConstants() {
    }

    private static final String PACKAGE_NAME = "com.wizag.ocrproject";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {
        // Unga House.
        BAY_AREA_LANDMARKS.put("UNGA", new LatLng(-1.2630, 36.8048));

        // Sarit Centre.
        BAY_AREA_LANDMARKS.put("SARIT", new LatLng(-1.2612,36.8020));

        //Sankara
        BAY_AREA_LANDMARKS.put("SANKARA", new LatLng(-1.2627,36.8023));

        //The Mall
        BAY_AREA_LANDMARKS.put("THEMALL", new LatLng(-1.2643,36.8027));
    }
}
