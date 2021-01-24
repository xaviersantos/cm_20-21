package pt.ua.cm.myshoppinglist.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import pt.ua.cm.myshoppinglist.R;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> mText;
//    private MutableLiveData<GoogleMap> mMap;

    private GoogleMap mMap;

    public MapViewModel() {
//        mText.setValue("Items Location");
    }



    public LiveData<String> getText() {
        return mText;
    }

}