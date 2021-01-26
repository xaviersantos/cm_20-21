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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.*;

public class ActivitySetLocation extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private boolean isCamInitPosSetup = false;
    private HashMap<String, LatLng> mPreviousPoints;
    private HashMap<String, LatLng> mNewPoints;     // <id : point>
    private ArrayList<String> mRemovedPoints;       // marker id
    private String mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        Intent intent = getIntent();
		mListId = intent.getStringExtra(LIST_ID);
        mPreviousPoints = (HashMap<String,LatLng>) intent.getSerializableExtra(MARKERS);
        mNewPoints = new HashMap<>();
        mRemovedPoints = new ArrayList<>();

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
        for(HashMap.Entry<String, LatLng> entry : mPreviousPoints.entrySet()) {
            String uuid = entry.getKey();
            LatLng point = entry.getValue();
            MarkerOptions markerOpt = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(mListId);
            Marker marker = mMap.addMarker(markerOpt);
            marker.setTag(uuid);
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
                String uuid = UUID.randomUUID().toString();
                MarkerOptions markerOpt = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(mListId);
                Marker marker = mMap.addMarker(markerOpt);
                marker.setTag(uuid);
                mNewPoints.put(uuid, point);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                mPoints.remove(marker.getPosition());
                String uuid = (String) marker.getTag();
                if (mPreviousPoints.containsKey(uuid)) {
                    mRemovedPoints.add(uuid);
                } else {
                    mNewPoints.remove(uuid);
                }
                marker.remove();
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
        boolean markersChanged = mNewPoints.size() > 0 || mRemovedPoints.size() > 0;
        returnIntent.putExtra(MARKERS_CHANGED, markersChanged);
        returnIntent.putExtra(NEW_MARKERS_LIST, mNewPoints);
        returnIntent.putExtra(REMOVED_MARKERS_LIST, mRemovedPoints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}