package pt.ua.cm.myshoppinglist.ui.lists;

import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.AddNewList;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;
import pt.ua.cm.myshoppinglist.utils.RecyclerListTouchHelper;

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

    public void initList(View root, String listId, String listName) {
        TextView listTitle = root.findViewById(R.id.listName);
        listTitle.setText(listName);
        listsRecyclerView = root.findViewById(R.id.itemsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listsAdapter = activity.getListDetailAdapter(listId);
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listsAdapter);

        FloatingActionButton fab = root.findViewById(R.id.bt_addItem);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listId", listId);
                AddNewItem fragment = new AddNewItem();
                fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
            }
        });


        //TODO: apagar ja nao é aqui
        //// Define locations for this list
//        ImageButton addLocButton = root.findViewById(R.id.bt_addLocation);
//        addLocButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ActivitySetLocation.class);
//                HashMap<String, LatLng> markers = new HashMap<>();
//                Log.d("MAP", "------->"+markers);
//                intent.putExtra("LIST_NAME", listName);
//                intent.putExtra(LIST_ID, listId);
//                intent.putExtra(MARKERS, markers);
//                startActivityForResult(intent, SET_LOCATIONS_CODE);
//            }
//        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(listsAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);
    }


    //TODO apagar, ja nao é aqui
//    /**
//     * Receives the changes done markers list on ActivitySetLocation
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        String listName = "listName"; //TODO
//        if (requestCode == SET_LOCATIONS_CODE && resultCode == Activity.RESULT_OK) {
//            // Check for changes
//            boolean markersChanged = intent.getBooleanExtra(MARKERS_CHANGED, false);
//            if (!markersChanged) {
//                return;
//            }
//            // Get DB handler
//            FirebaseDbHandler db = ((MainActivity) getActivity()).getDb();
//
//            // Get list of points that were removed and make the change in DB
//            ArrayList<String> removedPoints =
//                    (ArrayList<String>) intent.getSerializableExtra(REMOVED_MARKERS_LIST);
//            for (String point : removedPoints) {
//                db.deleteLocation(listName, point);
//            }
//
//            // Get list of new points added and make the change in DB
//            HashMap<String, LatLng> newPoints =
//                    (HashMap<String, LatLng>) intent.getSerializableExtra(NEW_MARKERS_LIST);
//            for (HashMap.Entry<String, LatLng> entry : newPoints.entrySet()) {
//                String uuid = entry.getKey();
//                LatLng point = entry.getValue();
//                db.addLocation(listName, uuid, point);
//            }
//        }
//    }

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