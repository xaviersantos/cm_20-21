package pt.ua.cm.myshoppinglist.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import pt.ua.cm.myshoppinglist.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    long LOCATION_REFRESH_TIME = 1000;      // milisecs
    long LOCATION_REFRESH_TIME_FAST = 500;
    float LOCATION_REFRESH_DISTANCE = 10;  // meters

    private MapViewModel mMapViewModel;

    private boolean isCamInitPosSetup = false;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        // Init map
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        return root;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true); // Self location button
        }
        startLocationUpdates();
    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_REFRESH_TIME);
        mLocationRequest.setFastestInterval(LOCATION_REFRESH_TIME_FAST);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // Get current position and update camera
        LocationServices.getFusedLocationProviderClient(getActivity())
                .getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("MAP","inside onSuccess location");
                if (location != null && mMap != null && !isCamInitPosSetup) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    LatLng latLng = new LatLng(lat,lng);
                    moveCamera(latLng);
                    isCamInitPosSetup = true;
                }
            }
        });
    }


    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        int REQUEST_FINE_LOCATION = 1001;
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    private void moveCamera(LatLng latLng) {
        if (mMap == null) {
            return;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
    }
}