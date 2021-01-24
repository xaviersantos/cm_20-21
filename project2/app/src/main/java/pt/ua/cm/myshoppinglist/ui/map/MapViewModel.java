package pt.ua.cm.myshoppinglist.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<GoogleMap> mMap;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mMap = new MutableLiveData<>();

//        mText.setValue("Items Location");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<GoogleMap> getMap() {
        return mMap;
    }
}