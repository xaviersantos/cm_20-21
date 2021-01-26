package pt.ua.cm.myshoppinglist.entities;

import com.google.android.gms.maps.model.LatLng;

public class LocationModel {
    private String uuid;
    private LatLng coords;

    public LocationModel() {
    }

    public LocationModel(String uuid, LatLng coords) {
        this.uuid = uuid;
        this.coords = coords;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }
}