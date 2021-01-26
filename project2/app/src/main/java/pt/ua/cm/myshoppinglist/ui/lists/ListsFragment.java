package pt.ua.cm.myshoppinglist.ui.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pt.ua.cm.myshoppinglist.ActivitySetLocation;
import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.AddNewList;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;
import pt.ua.cm.myshoppinglist.utils.RecyclerListTouchHelper;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS_CHANGED;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.SET_LIST_MARKERS;

public class ListsFragment extends Fragment {

    private View root;
    private ListsModel listsModel;
    private RecyclerView listsRecyclerView;
    private ListDetailAdapter listsAdapter;
    private ListScrollerAdapter listScrollerAdapter;
    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listsModel = new ViewModelProvider(this).get(ListsModel.class);
        root = inflater.inflate(R.layout.fragment_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_lists);

        activity = (MainActivity) getActivity();

        activity.initListScroller(root);

        //initList(root, "listName");

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

    private void initListScroller(View root) {
        listsRecyclerView = root.findViewById(R.id.listsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listScrollerAdapter = activity.getListScrollerAdapter();
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listScrollerAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerListTouchHelper(listScrollerAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);
    }

    public void initList(View root, String listName) {
        TextView listTitle = root.findViewById(R.id.listName);
        listTitle.setText(listName);
        listsRecyclerView = root.findViewById(R.id.itemsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listsAdapter = activity.getListDetailAdapter(listName);
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listsAdapter);

        FloatingActionButton fab = root.findViewById(R.id.bt_addItem);

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
                ArrayList<LatLng> markers = new ArrayList<>();
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
            boolean markersChanged = intent.getBooleanExtra(MARKERS_CHANGED, false);
            if (!markersChanged) {
                return;
            }
            FirebaseDbHandler db = ((MainActivity) getActivity()).getDb();
            ArrayList<LatLng> mPoints = (ArrayList<LatLng>) intent.getSerializableExtra("MARKERS");
            for (LatLng point : mPoints) {
                Log.d("MAP", point.toString());
                db.addLocation(listName, point);
            }
        }
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        //listScrollerAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override
    public void onStop() {
        super.onStop();
        //listScrollerAdapter.stopListening();
    }
}