package pt.ua.cm.myshoppinglist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pt.ua.cm.myshoppinglist.utils.AddNewItem;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.*;

public class ActivitySetLocation extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private boolean isCamInitPosSetup = false;
    private HashMap<String, LatLng> mPreviousPoints;    // <uuid : point>
    private HashMap<String, LatLng> mNewPoints;         // <uuid : point>
    private ArrayList<String> mRemovedPoints;           // uuid
    private String mListId;
    private String mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);


        Intent intent = getIntent();
		mListId = intent.getStringExtra(LIST_ID);
        mFirebaseUser = intent.getStringExtra("FIREBASE_USER");
        mPreviousPoints = new HashMap<>();
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
        loadMapMarkers();
    }

    /**
     * Loads previously saved markers from DB and places them on the map
     */
    private void loadMapMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(mFirebaseUser)
                .document(mListId)
                .collection("locations");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String markerId = (String) document.get("uuid");
                        HashMap<String, Double> coords = (HashMap<String, Double>) (document.get("coords"));
                        LatLng point = new LatLng(coords.get("latitude"), coords.get("longitude"));
                        MarkerOptions markerOpt = new MarkerOptions()
                                .position(new LatLng(point.latitude, point.longitude));
                        Marker marker = mMap.addMarker(markerOpt);
                        marker.setTag(markerId);
                        mPreviousPoints.put(markerId, point);
                    }
                } else {
                    Log.d("MAP", "Error getting documents: ", task.getException());
                }
            }
        });
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
                String markerId = UUID.randomUUID().toString();
                MarkerOptions markerOpt = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude));
                Marker marker = mMap.addMarker(markerOpt);
                marker.setTag(markerId);
                mNewPoints.put(markerId, point);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerId = (String) marker.getTag();
                if (mPreviousPoints.containsKey(markerId)) {
                    mRemovedPoints.add(markerId);
                } else {
                    mNewPoints.remove(markerId);
                }
                marker.remove();
                return true;
            }
        });

        FloatingActionButton confirmBtn = findViewById(R.id.bt_confirm_loc);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishLocationEdit();
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
        finishLocationEdit();
        super.onBackPressed();
    }

    /**
     * Confirms the changes to markers of this list and returns them
     */
    private void finishLocationEdit() {
        Intent returnIntent = new Intent();
        boolean markersChanged = mNewPoints.size() > 0 || mRemovedPoints.size() > 0;
        returnIntent.putExtra(LIST_ID, mListId);
        returnIntent.putExtra(MARKERS_CHANGED, markersChanged);
        returnIntent.putExtra(NEW_MARKERS_LIST, mNewPoints);
        returnIntent.putExtra(REMOVED_MARKERS_LIST, mRemovedPoints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}