package pt.ua.cm.myshoppinglist.ui.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.*;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mMapViewModel;

    private boolean isCamInitPosSetup = false;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mFirebaseUser;
    private HashMap<String, String> mListNames;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        // Init map
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*((MainActivity)getActivity()).getFirebaseUserID();*/

        mListNames = new HashMap<>(); // <list_id : list_name>

        return root;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(checkLocationPermissions(getActivity(), getContext()) ) {
            mMap.setMyLocationEnabled(true); // Self location button
        }
        showMyLocation();
        loadListNames();
    }

    // Trigger new location updates at interval
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // Get current position and update camera
        LocationServices.getFusedLocationProviderClient(getActivity())
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


    /**
     * Loads lists (id:name) pairs
     */
    private void loadListNames() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(mFirebaseUser);

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = (String) document.get("uuid");
                        String name = (String) document.get("listName");
                        mListNames.put(id, name);
                    }
                    loadMarkers();
                } else {
                    Log.d("MAP", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Loads the markers of the lists and places them on the map
     */
    private void loadMarkers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (HashMap.Entry<String, String> entry : mListNames.entrySet()) {
            String id = entry.getKey();
            String name = entry.getValue();
            CollectionReference colRef = db.collection(mFirebaseUser)
                .document(id)
                .collection("locations");

            colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Double> coords = (HashMap<String, Double>) (document.get("coords"));
                            LatLng point = new LatLng(coords.get("latitude"), coords.get("longitude"));
                            MarkerOptions markerOpt = new MarkerOptions()
                                    .position(new LatLng(point.latitude, point.longitude)).title(name);
                            mMap.addMarker(markerOpt);
                        }
                    } else {
                        Log.d("MAP", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }


}