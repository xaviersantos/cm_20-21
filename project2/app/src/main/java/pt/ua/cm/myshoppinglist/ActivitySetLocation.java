package pt.ua.cm.myshoppinglist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.*;

public class ActivitySetLocation extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean isCamInitPosSetup = false;
    private boolean mMarkersChanged = false;
    private ArrayList<LatLng> mPoints;
    private String mListName;
    private String mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        Intent intent = getIntent();
        mPoints = (ArrayList<LatLng>) intent.getSerializableExtra(MARKERS);
        mListName = intent.getStringExtra("LIST_NAME");
        mListId = intent.getStringExtra("LIST_ID");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_set_location);

        mapFragment.getMapAsync(this);
    }


    /**
     * Called when GoogleMap is ready to be used
     * @param googleMap
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(checkLocationPermissions(this, this)) {
            mMap.setMyLocationEnabled(true); // Self location button
        }
        // Zoom to my location
        showMyLocation();
        // Setup listener to add / remove markers on click
        setTouchListeners();
        // Populate map with existing markers
        addMarkersToMap();
    }


    /**
     * Adds the already existing markers of this list to the map
     */
    private void addMarkersToMap() {
        if (mPoints.size() > 0) {
            for (LatLng point : mPoints) {
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude)).title(mListName);
                mMap.addMarker(marker);
            }
        }
    }


    /**
     * Set up touch listeners
     * Tap on map to add a marker
     * Tap on a marker to remove it
     */
    private void setTouchListeners() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude)).title(mListName);
                mPoints.add(point);
                mMap.addMarker(marker);
                mMarkersChanged = true;
                Log.d("MAP","added point:"+point.toString());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mPoints.remove(marker.getPosition());
                marker.remove();
                mMarkersChanged = true;
                return true;
            }
        });
    }

    /**
     * Gets own location
     */
    @SuppressLint("MissingPermission")
    private void showMyLocation() {
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // Get current position and update camera
        LocationServices.getFusedLocationProviderClient(this)
                .getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null && mMap != null && !isCamInitPosSetup) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    LatLng latLng = new LatLng(lat,lng);
                    moveCamera(mMap, latLng);
                    isCamInitPosSetup = true;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        //TODO: mudar isto para um botao de confirmar
        finishLocationEdit();
        super.onBackPressed();
    }

    /**
     * Confirms the changes to markers of this list and returns them
     */
    private void finishLocationEdit() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MARKERS_CHANGED, mMarkersChanged);
        if (mMarkersChanged) {
            returnIntent.putExtra(MARKERS, mPoints);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}