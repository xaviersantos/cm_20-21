package pt.ua.cm.myshoppinglist.ui.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.ua.cm.myshoppinglist.ActivitySetLocation;
import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.AddNewList;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS_CHANGED;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.NEW_MARKERS_LIST;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.REMOVED_MARKERS_LIST;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.SET_LIST_MARKERS;

public class ListsFragment extends Fragment {

    private ListsModel listsModel;
    private RecyclerView listsRecyclerView;
    private ListPreviewAdapter listsAdapter;
    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listsModel = new ViewModelProvider(this).get(ListsModel.class);
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_lists);

        activity = (MainActivity) getActivity();

        initList(root);

        ImageButton addButton = root.findViewById(R.id.bt_addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle = new Bundle();
                //bundle.putString("listName", "listName");
                AddNewList fragment = new AddNewList();
                //fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), AddNewList.TAG);
            }
        });

        listsModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        
        return root;
    }

    public void initList(View root) {
        String listName = "listName";
        TextView listTitle = root.findViewById(R.id.listName);
        listTitle.setText(listName);
        listsRecyclerView = root.findViewById(R.id.itemsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listsAdapter = activity.getListsAdapter(listName);
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listsAdapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listName", listName);
                AddNewItem fragment = new AddNewItem();
                fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
            }
        });

        // Define locations for this list
        ImageButton addLocButton = root.findViewById(R.id.bt_addLocation);
        addLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivitySetLocation.class);
                HashMap<String, LatLng> markers = new HashMap<>();
                intent.putExtra("LIST_NAME", listName);
                intent.putExtra(MARKERS, markers);
                startActivityForResult(intent, SET_LIST_MARKERS);
            }
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(listsAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);
    }

    /**
     * Receives the changes done markers list on ActivitySetLocation
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        String listName = "listName"; //TODO
        if (requestCode == SET_LIST_MARKERS && resultCode == Activity.RESULT_OK) {
            // Check for changes
            boolean markersChanged = intent.getBooleanExtra(MARKERS_CHANGED, false);
            if (!markersChanged) {
                return;
            }
            // Get DB handler
            FirebaseDbHandler db = ((MainActivity) getActivity()).getDb();

            // Get list of points that were removed and make the change in DB
            ArrayList<String> removedPoints =
                    (ArrayList<String>) intent.getSerializableExtra(REMOVED_MARKERS_LIST);
            for (String point : removedPoints) {
                db.deleteLocation(listName, point);
            }

            // Get list of new points added and make the change in DB
            HashMap<String, LatLng> newPoints =
                    (HashMap<String, LatLng>) intent.getSerializableExtra(NEW_MARKERS_LIST);
            for (HashMap.Entry<String, LatLng> entry : newPoints.entrySet()) {
                String uuid = entry.getKey();
                LatLng point = entry.getValue();
                db.addLocation(listName, uuid, point);
            }
        }
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        listsAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override
    public void onStop() {
        super.onStop();
        listsAdapter.stopListening();
    }
}