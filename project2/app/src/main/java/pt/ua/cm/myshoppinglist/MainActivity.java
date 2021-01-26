package pt.ua.cm.myshoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.entities.ListModel;
import pt.ua.cm.myshoppinglist.ui.lists.ItemsFragment;
import pt.ua.cm.myshoppinglist.ui.lists.ListDetailAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListPreviewAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListScrollerAdapter;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.DialogCloseListener;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;
import pt.ua.cm.myshoppinglist.utils.ListItemClickListener;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;
import pt.ua.cm.myshoppinglist.utils.RecyclerListTouchHelper;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.LIST_ID;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.LIST_NAME;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS_CHANGED;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.NEW_MARKERS_LIST;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.REMOVED_MARKERS_LIST;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.LOC_ACTIVITY_INTENT_CODE;

public class MainActivity extends AppCompatActivity implements DialogCloseListener, ListItemClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "MainActivity";

    private FirebaseDbHandler db;
    private ListPreviewAdapter listPreviewAdapter;
    private RecyclerView listsRecyclerView;
    private ListDetailAdapter listsAdapter;
    private ListScrollerAdapter listScrollerAdapter;

    Context context = this;

    private List<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAP", "----CHECKING IF LOGCAT IS WORKING----");
        Objects.requireNonNull(getSupportActionBar()).hide();

        loginAnonymously();
    }

//    public List<ListModel> getAllItems() {
//        List<ListModel> itemList = db.getAllItems();
//        Collections.reverse(itemList);
//
//        return itemList;
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (listsAdapter != null)
            listsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listsAdapter != null)
            listsAdapter.stopListening();
    }

    private void loginAnonymously() {
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        this.currentUser = currentUser;

        if(currentUser != null)
            db = new FirebaseDbHandler(currentUser);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_lists, R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Make the center the default selection
        navView.setSelectedItemId(R.id.navigation_lists);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        if (listsAdapter != null)
            listsAdapter.notifyDataSetChanged();
        if (listScrollerAdapter != null)
            listScrollerAdapter.notifyDataSetChanged();
    }

    public ListPreviewAdapter getListPreviewAdapter(String listId) {

        Query query = FirebaseFirestore.getInstance()
                .collection(currentUser.getUid())
                .document(listId)
                .collection("items");

        FirestoreRecyclerOptions<ItemModel> options = new FirestoreRecyclerOptions.Builder<ItemModel>()
                .setQuery(query, ItemModel.class)
                .build();

        // Connecting object of required Adapter class to
        // the Adapter class itself
        listPreviewAdapter = new ListPreviewAdapter(options, listId, this);
        listPreviewAdapter.startListening();

        return listPreviewAdapter;
    }

    public ListDetailAdapter getListDetailAdapter(String listId) {

        Query query = FirebaseFirestore.getInstance()
                .collection(currentUser.getUid())
                .document(listId)
                .collection("items");

        FirestoreRecyclerOptions<ItemModel> options = new FirestoreRecyclerOptions.Builder<ItemModel>()
                .setQuery(query, ItemModel.class)
                .build();

        // Connecting object of required Adapter class to
        // the Adapter class itself
        listsAdapter = new ListDetailAdapter(options, db, listId, this);
        listsAdapter.startListening();

        return listsAdapter;
    }

    public ListScrollerAdapter getListScrollerAdapter() {

        Query query = FirebaseFirestore.getInstance()
                .collection(currentUser.getUid());

        FirestoreRecyclerOptions<ListModel> options = new FirestoreRecyclerOptions.Builder<ListModel>()
                .setQuery(query, ListModel.class)
                .build();

        listScrollerAdapter = new ListScrollerAdapter(options, this);
        listScrollerAdapter.startListening();

        return listScrollerAdapter;
    }


    public FirebaseDbHandler getDb() {
        return db;
    }

    public String getCurrentUserName() {
        return currentUser.getDisplayName();
    }

    public void initListScroller(View root) {
        listsRecyclerView = root.findViewById(R.id.listsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        listScrollerAdapter = getListScrollerAdapter();
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
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        listsAdapter = getListDetailAdapter(listId);
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listsAdapter);

        FloatingActionButton addItemButton = root.findViewById(R.id.bt_addItem);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listId", listId);
                bundle.putString("listName", listName);
                AddNewItem fragment = new AddNewItem();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), AddNewItem.TAG);
            }
        });

        // Define locations for this list
        FloatingActionButton addLocationButton = root.findViewById(R.id.bt_addLocation);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivitySetLocation.class);
                HashMap<String, LatLng> markers = new HashMap<>();
                intent.putExtra(LIST_NAME, listName);
                intent.putExtra(LIST_ID, listId);
                intent.putExtra(MARKERS, markers);
                startActivityForResult(intent, LOC_ACTIVITY_INTENT_CODE);
            }
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(listsAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);
    }

    @Override
    public void onListItemClick(ListModel list) {
        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMgr.beginTransaction();

        ItemsFragment itemsFragment = new ItemsFragment(); //my custom fragment

        Bundle bundle = new Bundle();
        bundle.putString("listId", list.getUuid());
        bundle.putString("listName", list.getListName());
        itemsFragment.setArguments(bundle);

        fragTrans.replace(R.id.nav_host_fragment, itemsFragment);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    /**
     * Receives the changes done markers list on ActivitySetLocation
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LOC_ACTIVITY_INTENT_CODE && resultCode == Activity.RESULT_OK) {
            // Check for changes
            boolean markersChanged = intent.getBooleanExtra(MARKERS_CHANGED, false);
            if (!markersChanged) {
                return;
            }
            String listId = (String) intent.getStringExtra(LIST_ID);

            // Get list of points that were removed and make the change in DB
            ArrayList<String> removedPoints =
                    (ArrayList<String>) intent.getSerializableExtra(REMOVED_MARKERS_LIST);
            for (String point : removedPoints) {
                db.deleteLocation(listId, point);
            }

            // Get list of new points added and make the change in DB
            HashMap<String, LatLng> newPoints =
                    (HashMap<String, LatLng>) intent.getSerializableExtra(NEW_MARKERS_LIST);
            for (HashMap.Entry<String, LatLng> entry : newPoints.entrySet()) {
                String uuid = entry.getKey();
                LatLng point = entry.getValue();
                db.addLocation(listId, uuid, point);
            }
        }
    }
}