package pt.ua.cm.myshoppinglist.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUtils {
    private LocationRequest mLocationRequest;

    /**
     * How often location requests are made
     */
    public static final long LOCATION_REFRESH_TIME = 500;      // milisecs
    public static final long LOCATION_REFRESH_TIME_FAST = 200;  // milisecs


    /**
     * Checks if app has location permission
     * @return
     */
    public static boolean checkLocationPermissions(Activity activity, Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(activity);
            return false;
        }
    }

    /**
     * Requests location permission
     */
    public static void requestPermissions(Activity activity) {
        int REQUEST_FINE_LOCATION = 1001;
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    /**
     * Moves the camera to a position
     * @param latLng
     */
    public static void moveCamera(GoogleMap map, LatLng latLng) {
        if (map == null) {
            return;
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.zoomTo(18));
    }
}