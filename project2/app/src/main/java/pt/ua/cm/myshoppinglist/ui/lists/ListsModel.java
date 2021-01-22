package pt.ua.cm.myshoppinglist.ui.lists;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListsModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListsModel() {
        mText = new MutableLiveData<>();
        mText.setValue("My Lists");
    }

    public LiveData<String> getText() {
        return mText;
    }
}